package infra.dao;

import java.sql.SQLException;

/**
 * Reconhece, na cadeia de causas de uma exceção, os erros de integridade do MySQL que os DAOs
 * transformam em mensagens que o usuário entende.
 */
final class ErrosSql {

    /** MySQL: "Cannot delete or update a parent row: a foreign key constraint fails". */
    private static final int MYSQL_CHAVE_ESTRANGEIRA_PAI = 1451;
    /** MySQL: "Duplicate entry ... for key ...". */
    private static final int MYSQL_VALOR_DUPLICADO = 1062;

    private ErrosSql() { }

    /**
     * Diz se o banco recusou um DELETE/UPDATE porque outra tabela aponta para a linha.
     * Sem o código do MySQL, aceita qualquer violação de integridade (SQLState 23xxx).
     */
    static boolean violaChaveEstrangeira(Throwable erro) {
        SQLException sql = procurarSql(erro);
        if (sql == null) {
            return false;
        }
        String estado = sql.getSQLState();
        return sql.getErrorCode() == MYSQL_CHAVE_ESTRANGEIRA_PAI
                || (sql.getErrorCode() == 0 && estado != null && estado.startsWith("23"));
    }

    /** Diz se o banco recusou um INSERT/UPDATE por repetir um valor de coluna UNIQUE. */
    static boolean violaValorUnico(Throwable erro) {
        SQLException sql = procurarSql(erro);
        return sql != null && sql.getErrorCode() == MYSQL_VALOR_DUPLICADO;
    }

    private static SQLException procurarSql(Throwable erro) {
        for (Throwable causa = erro; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sql) {
                return sql;
            }
        }
        return null;
    }
}
