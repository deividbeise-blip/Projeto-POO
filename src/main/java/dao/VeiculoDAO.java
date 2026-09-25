package dao;

import conexao.ConexaoBanco;
import modelo.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class VeiculoDAO {
    public void salvar(Cliente cliente) {

        String sql = """
                INSERT INTO cliente
                (name_cliente, cpf, email, phone)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, cliente.getName());
            comando.setString(2, cliente.getCpf());
            comando.setString(3, cliente.getEmail());
            comando.setString(4, cliente.getPhone());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    cliente.setId(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cliente no banco.", e);
        }
    }
}