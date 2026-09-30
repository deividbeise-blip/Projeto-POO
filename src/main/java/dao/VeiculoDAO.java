package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexao.ConexaoBanco;
import modelo.Concessionaria;
import modelo.StatusVeiculo;
import modelo.Veiculo;

public class VeiculoDAO {

    public void salvar(Veiculo veiculo) {

        // A coluna no banco é "concessionario_veiculo" (sem "a"), não "concessionaria_veiculo".
        String sql = """
                INSERT INTO veiculo
                (marca_veiculo, modelo_veiculo, ano_veiculo, placa_veiculo, preco_veiculo, status_veiculo, moto, concessionario_veiculo)
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
            comando.setString(6, paraColunaEnum(veiculo.getStatus()));
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

    /**
     * Persiste só o status. Não existe coluna "vendido" separada no banco: o próprio
     * status_veiculo já contém essa informação (VENDIDO/DISPONIVEL/EM_MANUTENCAO).
     */
    public void atualizarStatus(Veiculo veiculo) {
        String sql = "UPDATE veiculo SET status_veiculo = ? WHERE id_veiculo = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, paraColunaEnum(veiculo.getStatus()));
            comando.setLong(2, veiculo.getId_veiculo());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status do veículo no banco.", e);
        }
    }

    public List<Veiculo> listarDisponiveis() {
        return listar("SELECT * FROM veiculo WHERE status_veiculo = 'Disponivel'",
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
        Concessionaria concessionaria = concessionariaDAO.buscarPorId(resultado.getLong("concessionario_veiculo"));

        // O terceiro parâmetro "vendido" recebe null: o construtor de Veiculo já deriva
        // esse valor a partir do status (VENDIDO = true, senão false), então não
        // precisamos de uma coluna própria para isso.
        return new Veiculo(
                resultado.getLong("id_veiculo"),
                resultado.getString("marca_veiculo"),
                resultado.getString("modelo_veiculo"),
                resultado.getInt("ano_veiculo"),
                resultado.getString("placa_veiculo"),
                resultado.getDouble("preco_veiculo"),
                paraEnumJava(resultado.getString("status_veiculo")),
                concessionaria,
                resultado.getBoolean("moto"),
                null
        );
    }

    /**
     * A coluna status_veiculo no banco é um ENUM com os valores exatos
     * 'Disponivel', 'Vendido' e 'Em_Manutencao' (só a primeira letra maiúscula),
     * diferente da grafia toda maiúscula do enum StatusVeiculo em Java.
     * Estes dois métodos convertem entre os dois formatos nos dois sentidos.
     */
    private String paraColunaEnum(StatusVeiculo status) {
        return switch (status) {
            case DISPONIVEL -> "Disponivel";
            case VENDIDO -> "Vendido";
            case EM_MANUTENCAO -> "Em_Manutencao";
        };
    }

    private StatusVeiculo paraEnumJava(String valorColuna) {
        return switch (valorColuna) {
            case "Disponivel" -> StatusVeiculo.DISPONIVEL;
            case "Vendido" -> StatusVeiculo.VENDIDO;
            case "Em_Manutencao" -> StatusVeiculo.EM_MANUTENCAO;
            default -> throw new IllegalStateException("Status desconhecido no banco: " + valorColuna);
        };
    }
}