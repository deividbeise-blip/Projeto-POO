package dao;

import conexao.ConexaoBanco;
import modelo.Concessionaria;
import modelo.StatusVeiculo;
import modelo.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public void atualizar(Veiculo veiculo, String atributo, String novoValor) {
        // Nome de coluna não pode ser parametrizado com "?", por isso é concatenado diretamente.
        // ATENÇÃO: "atributo" nunca deve vir de entrada do usuário sem validação, para evitar SQL Injection.
        String sql = "UPDATE veiculo SET " + atributo + " = ? WHERE id_veiculo = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoValor);
            comando.setLong(2, veiculo.getId_veiculo());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar veículo no banco.", e);
        }
    }

    public List<Veiculo> listarDisponiveis() {
        String sql = "SELECT * FROM veiculo WHERE status_veiculo = 'DISPONIVEL'";
        List<Veiculo> veiculos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();

            while (resultado.next()) {
                Concessionaria concessionaria = concessionariaDAO.buscarPorId(resultado.getLong("concessionaria_veiculo"));

                Veiculo veiculo = new Veiculo(
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
                veiculos.add(veiculo);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar veículos disponíveis.", e);
        }

        return veiculos;
    }

    public List<Veiculo> listarTodos() {
    String sql = "SELECT * FROM veiculo";
    List<Veiculo> veiculos = new ArrayList<>();

    try (Connection conexao = ConexaoBanco.conectar();
         PreparedStatement comando = conexao.prepareStatement(sql);
         ResultSet resultado = comando.executeQuery()) {

        ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();

        while (resultado.next()) {
            Concessionaria concessionaria = concessionariaDAO.buscarPorId(resultado.getLong("concessionaria_veiculo"));

            Veiculo veiculo = new Veiculo(
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
            veiculos.add(veiculo);
        }

    } catch (Exception e) {
        throw new RuntimeException("Erro ao listar veículos.", e);
    }

    return veiculos;
}
}