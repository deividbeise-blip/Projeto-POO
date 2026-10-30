package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL =
            "jdbc:mysql://betas:AVNS_O31MopHAFid95WTV-ZQ@mysql-4eaccc0-projeto-poo.k.aivencloud.com:21803/defaultdb?ssl-mode=REQUIRED"; 

    private static final String USUARIO = "betas";

    private static final String SENHA = "AVNS_O31MopHAFid95WTV-ZQ";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(
                URL,
                USUARIO,
                SENHA
        );
    }
}