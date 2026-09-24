import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    public static void main(String[] args) {
        // Como o nome do banco tem espaço, colocamos entre crases dentro da URL
        String url = "jdbc:mysql://localhost:3306/projeto_poo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String usuario = "root"; // Mude para o seu usuário do MySQL se for diferente
        String senha = "";   // Coloque a sua senha do MySQL aqui

        System.out.println("Tentando conectar ao banco projeto_poo...");

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {
            if (conexao != null) {
                System.out.println("Conexão com o MySQL realizada com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
}