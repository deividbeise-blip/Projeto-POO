package infra.conexao;

public class TesteBanco {

    public static void main(String[] args) {

        try {
            new ConexaoBanco().getConnection().close();

            System.out.println("Conexão com o MySQL realizada com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao conectar com o MySQL:");
            e.printStackTrace();
        }
    }
}
