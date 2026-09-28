package dao;

import conexao.ConexaoBanco;
import modelo.Concessionaria;
import modelo.StatusVeiculo;
import modelo.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    veiculo.setId_veiculo(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar veículo no banco.", e);
        }
    }

    /** Persiste o status e a flag "vendido" juntos, para nunca ficarem divergentes no banco. */
    public void atualizarStatus(Veiculo veiculo) {
        String sql = "UPDATE veiculo SET status_veiculo = ?, vendido = ? WHERE id_veiculo = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, veiculo.getStatus().name());
            comando.setBoolean(2, veiculo.getVendido());
            comando.setLong(3, veiculo.getId_veiculo());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status do veículo no banco.", e);
        }
    }

    public List<Veiculo> listarDisponiveis() {
        return listar("SELECT * FROM veiculo WHERE status_veiculo = 'DISPONIVEL'",
                "Erro ao listar veículos disponíveis.");
    }

    public List<Veiculo> listarTodos() {
        return listar("SELECT * FROM veiculo", "Erro ao listar veículos.");
    }

    private List<Veiculo> listar(String sql, String mensagemErro) {
        List<Veiculo> veiculos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();

            while (resultado.next()) {
                veiculos.add(mapear(resultado, concessionariaDAO));
            }

        } catch (Exception e) {
            throw new RuntimeException(mensagemErro, e);
        }

        return veiculos;
    }

    private Veiculo mapear(ResultSet resultado, ConcessionariaDAO concessionariaDAO) throws SQLException {
        Concessionaria concessionaria = concessionariaDAO.buscarPorId(resultado.getLong("concessionaria_veiculo"));

        return new Veiculo(
                resultado.getLong("id_veiculo"),
                resultado.getString("marca_veiculo"),
                resultado.getString("modelo_veiculo"),
                resultado.getInt("ano_veiculo"),
                resultado.getString("placa_veiculo"),
                resultado.getDouble("preco_veiculo"),
                StatusVeiculo.valueOf(resultado.getString("status_veiculo")),
                concessionaria,
                resultado.getBoolean("moto"),
                resultado.getBoolean("vendido")
        );
    }
}
