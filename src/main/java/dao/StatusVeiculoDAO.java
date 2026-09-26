package dao;
import modelo.Veiculo;
import conexao.ConexaoBanco;
import java.sql.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StatusVeiculoDAO {
    public void salvar(Veiculo veiculo) {

        String sql = """
                INSERT INTO statusveiculo
                (StatusVeiculo, statusveiculo_veiculo)
                VALUES (?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, veiculo.getStatus().name());
            comando.setLong(2, veiculo.getId_veiculo());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    veiculo.setId_veiculo(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar veiculo no banco.", e);
        }
    }
    public void atualizar(Veiculo veiculo, String atributo, String novoValor) {
        String sql = """
                UPDATE statusveiculo
                SET ? = ?
                WHERE id_statusveiculo = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, atributo);
            comando.setString(2, novoValor);
            comando.setLong(3, veiculo.getId_veiculo());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status do veiculo no banco.", e);
        }
    }
}
