package dao;

import conexao.ConexaoBanco;
import modelo.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    vendedor.setId_vendedor(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar Vendedor no banco.", e);
        }
    }
}