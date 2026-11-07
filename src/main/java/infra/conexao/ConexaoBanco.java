package infra.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Conexão MySQL. As credenciais vêm das variáveis de ambiente DB_URL, DB_USER e
 * DB_PASSWORD (veja .env.example); nada de senha, usuário ou host no código.
 */
public class ConexaoBanco implements ConnectionFactory {

    private final String url;
    private final String usuario;
    private final String senha;

    public ConexaoBanco() {
        List<String> faltando = new ArrayList<>();
        this.url = lerVariavel("DB_URL", faltando);
        this.usuario = lerVariavel("DB_USER", faltando);
        this.senha = lerVariavel("DB_PASSWORD", faltando);

        if (!faltando.isEmpty()) {
            throw new IllegalStateException(
                    "Variáveis de ambiente do banco não definidas: " + String.join(", ", faltando)
                            + ". Configure DB_URL, DB_USER e DB_PASSWORD antes de iniciar o sistema.");
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }


    private static String lerVariavel(String nome, List<String> faltando) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            faltando.add(nome);
        }
        return valor;
    }
}
