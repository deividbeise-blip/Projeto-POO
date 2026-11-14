package infra.dao;

import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Concentra o JDBC repetido dos DAOs: abrir conexão, montar o PreparedStatement,
 * pegar o ID gerado e traduzir SQLException para PersistenciaException.
 * Usa sempre a ConnectionFactory recebida, então participa da transação em andamento (se houver).
 */
final class ExecutorSql {

    /** Preenche os parâmetros (?) do PreparedStatement. */
    @FunctionalInterface
    interface Preenchedor {
        void preencher(PreparedStatement comando) throws SQLException;
    }

    /** Transforma a linha atual do ResultSet em um objeto. */
    @FunctionalInterface
    interface Mapeador<T> {
        T mapear(ResultSet resultado) throws SQLException;
    }

    static final Preenchedor SEM_PARAMETROS = comando -> { };

    private final ConnectionFactory connectionFactory;

    ExecutorSql(ConnectionFactory connectionFactory) {
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "ConnectionFactory é obrigatória.");
    }

    /** INSERT; devolve o ID gerado pelo AUTO_INCREMENT. */
    long inserir(String sql, Preenchedor preenchedor, String mensagemErro) {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preenchedor.preencher(comando);
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    return chaves.getLong(1);
                }
            }
            throw new PersistenciaException(mensagemErro + " (o banco não devolveu o ID gerado)");

        } catch (SQLException e) {
            throw new PersistenciaException(mensagemErro, e);
        }
    }

    /** UPDATE/DELETE; devolve quantas linhas foram afetadas. */
    int atualizar(String sql, Preenchedor preenchedor, String mensagemErro) {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preenchedor.preencher(comando);
            return comando.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException(mensagemErro, e);
        }
    }

    /** SELECT com parâmetros; devolve todas as linhas mapeadas (lista vazia se não houver). */
    <T> List<T> consultar(String sql, Preenchedor preenchedor, Mapeador<T> mapeador, String mensagemErro) {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            preenchedor.preencher(comando);

            try (ResultSet resultado = comando.executeQuery()) {
                List<T> itens = new ArrayList<>();
                while (resultado.next()) {
                    itens.add(mapeador.mapear(resultado));
                }
                return itens;
            }

        } catch (SQLException e) {
            throw new PersistenciaException(mensagemErro, e);
        }
    }

    /** SELECT sem parâmetros. */
    <T> List<T> consultar(String sql, Mapeador<T> mapeador, String mensagemErro) {
        return consultar(sql, SEM_PARAMETROS, mapeador, mensagemErro);
    }

    /** Primeira linha do resultado, ou null se não houver nenhuma. */
    <T> T consultarUm(String sql, Preenchedor preenchedor, Mapeador<T> mapeador, String mensagemErro) {
        List<T> itens = consultar(sql, preenchedor, mapeador, mensagemErro);
        return itens.isEmpty() ? null : itens.get(0);
    }
}
