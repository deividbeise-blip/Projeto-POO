package infra.conexao;

import java.sql.Connection;
import java.sql.SQLException;

/** Abstração de "onde vem a conexão com o banco". */
public interface ConnectionFactory {
    /**
     * Entrega uma conexão pronta para uso. Quem recebe deve fechá-la (try-with-resources).
     *
     * @return conexão aberta com o banco
     * @throws SQLException se não for possível conectar
     */
    Connection getConnection() throws SQLException;
}
