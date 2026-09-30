package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexao.ConexaoBanco;
import modelo.Cliente;

public class ClienteDAO {

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

            comando.setString(1, cliente.getName_cliente());
            comando.setString(2, cliente.getCpf_cliente());
            comando.setString(3, cliente.getEmail_cliente());
            comando.setString(4, cliente.getPhone_cliente());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    cliente.setId_cliente(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cliente no banco.", e);
        }
    }

    public void atualizar(Cliente cliente, String atributo, String novoValor) {
        String sql = "UPDATE cliente SET " + atributo + " = ? WHERE id_cliente = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoValor);
            comando.setLong(2, cliente.getId_cliente());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar cliente no banco.", e);
        }
    }

    /** Necessário para o combo de clientes na tela de Vendas. */
    public List<Cliente> listar() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                Cliente cliente = new Cliente(
                        resultado.getString("name_cliente"),
                        resultado.getString("cpf"),
                        resultado.getString("email"),
                        resultado.getString("phone")
                );
                cliente.setId_cliente(resultado.getLong("id_cliente"));
                clientes.add(cliente);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar clientes.", e);
        }

        return clientes;
    }
}
