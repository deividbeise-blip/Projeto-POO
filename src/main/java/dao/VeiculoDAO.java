package dao;

import conexao.ConexaoBanco;
import modelo.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class VeiculoDAO {
    public void salvar(Veiculo veiculo) {

        String sql = """
                INSERT INTO veiculo
                (marca_veiculo, modelo_veiculo, ano_veiculo, placa_veiculo, preco_veiculo, status_veiculo, moto, concessionaria_veiculo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, veiculo.getMarca());
            comando.setString(2, veiculo.getModelo());
            comando.setInt(3, veiculo.getAno());
            comando.setString(4, veiculo.getPlaca());
            comando.setDouble(5, veiculo.getPreco());
            comando.setString(6, veiculo.getStatus().name());
            comando.setBoolean(7, veiculo.isMoto());
            comando.setLong(8, veiculo.getConcessionaria().getId_concessionaria());
            comando.setString(4, veiculo.getPlaca());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    veiculo.setId_veiculo(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar veículo no banco.", e);
        }
    }
}