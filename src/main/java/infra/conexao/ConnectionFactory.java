package infra.conexao;

import java.sql.Connection;
import java.sql.SQLException;

/** Abstração de "onde vem a conexão com o banco". */
public interface ConnectionFactory {
    Connection getConnection() throws SQLException;
}
