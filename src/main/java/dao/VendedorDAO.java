package dao;

import conexao.ConexaoBanco;
import modelo.Concessionaria;
import modelo.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO {

    public void salvar(Vendedor vendedor) {

        String sql = """
                INSERT INTO vendedor
                (name_vendedor, cpf_vendedor, comissao_percentual, concessionaria_vendedor)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, vendedor.getName());
            comando.setString(2, vendedor.getCpf());
            comando.setDouble(3, vendedor.getComissaoPercentual());
            comando.setLong(4, vendedor.getConcessionaria().getId_concessionaria());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    vendedor.setId_vendedor(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar Vendedor no banco.", e);
        }
    }

    public void atualizar(Vendedor vendedor, String atributo, String novoValor) {
        String sql = "UPDATE vendedor SET " + atributo + " = ? WHERE id_vendedor = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoValor);
            comando.setLong(2, vendedor.getId_vendedor());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar vendedor no banco.", e);
        }
    }

    public List<Vendedor> listar() {
        String sql = "SELECT * FROM vendedor";
        List<Vendedor> vendedores = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();

            while (resultado.next()) {
                Concessionaria concessionaria = concessionariaDAO.buscarPorId(resultado.getLong("concessionaria_vendedor"));

                Vendedor vendedor = new Vendedor(
                        resultado.getLong("id_vendedor"),
                        resultado.getString("name_vendedor"),
                        resultado.getString("cpf_vendedor"),
                        resultado.getDouble("comissao_percentual"),
                        concessionaria
                );
                vendedores.add(vendedor);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar vendedores.", e);
        }

        return vendedores;
    }
}