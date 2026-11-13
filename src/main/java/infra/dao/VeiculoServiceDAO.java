package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;
import dominio.repositorio.VeiculoServiceRepository;
import infra.conexao.ConnectionFactory;

public class VeiculoServiceDAO implements VeiculoServiceRepository {

    // Uma consulta só: o JOIN traz a concessionária junto, sem consulta extra por linha.
    private static final String SELECT_COM_CONCESSIONARIA = """
            SELECT v.id_veiculo, v.marca_veiculo, v.modelo_veiculo, v.ano_veiculo, v.placa_veiculo,
                   v.preco_veiculo, v.status_veiculo, v.moto,
                   c.id_concessionaria, c.name_concessionaria, c.address_concessionaria
            FROM veiculoservice v
            JOIN concessionaria c ON c.id_concessionaria = v.concessionario_veiculo
            """;

    private final ExecutorSql executor;

    public VeiculoServiceDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    @Override
    public void salvar(Veiculo veiculo) {
        // A coluna no banco é "concessionario_veiculo" (sem "a"), não "concessionaria_veiculo".
        long id = executor.inserir(
                """
                INSERT INTO veiculoservice
                (marca_veiculo, modelo_veiculo, ano_veiculo, placa_veiculo, preco_veiculo, status_veiculo, moto, concessionario_veiculo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                comando -> {
                    comando.setString(1, veiculo.getMarca());
                    comando.setString(2, veiculo.getModelo());
                    comando.setInt(3, veiculo.getAno());
                    comando.setString(4, veiculo.getPlaca());
                    comando.setDouble(5, veiculo.getPreco());
                    comando.setString(6, paraColunaEnum(veiculo.getStatus()));
                    comando.setBoolean(7, veiculo.isMoto());
                    comando.setLong(8, veiculo.getConcessionaria().getIdConcessionaria());
                },
                "Erro ao salvar veículo no banco.");
        veiculo.setIdVeiculo(id);
    }

    /**
     * Persiste só o status. Não existe coluna "vendido" separada no banco: o próprio
     * status_veiculo já contém essa informação.
     */
    @Override
    public void atualizarStatus(Veiculo veiculo) {
        executor.atualizar(
                "UPDATE veiculoservice SET status_veiculo = ? WHERE id_veiculo = ?",
                comando -> {
                    comando.setString(1, paraColunaEnum(veiculo.getStatus()));
                    comando.setLong(2, veiculo.getIdVeiculo());
                },
                "Erro ao atualizar status do veículo no banco.");
    }

    @Override
    public Veiculo buscarPorId(Long id) {
        return executor.consultarUm(
                SELECT_COM_CONCESSIONARIA + " WHERE v.id_veiculo = ?",
                comando -> comando.setLong(1, id),
                VeiculoServiceDAO::mapear,
                "Erro ao buscar veículo no banco.");
    }

    @Override
    public List<Veiculo> listarDisponiveis() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA + " WHERE v.status_veiculo = 'Disponivel'",
                VeiculoServiceDAO::mapear,
                "Erro ao listar veículos disponíveis.");
    }

    @Override
    public List<Veiculo> listarTodos() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA,
                VeiculoServiceDAO::mapear,
                "Erro ao listar veículos.");
    }

    @Override
    public void excluir(Long id) {
        Veiculo veiculo = buscarPorId(id);
        if (veiculo == null || veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new IllegalStateException("Não é possível excluir um veículo que não está disponível.");
        }

        executor.atualizar(
                "DELETE FROM veiculoservice WHERE id_veiculo = ?",
                comando -> comando.setLong(1, id),
                "Erro ao excluir veículo no banco.");
    }

    @Override
    public void atualizar(Veiculo veiculo) {
        if (veiculo.getStatus() != StatusVeiculo.DISPONIVEL) {
            throw new IllegalStateException("Não é possível atualizar um veículo que não está disponível.");
        }
        
        executor.atualizar(
                """
                UPDATE veiculoservice
                SET marca_veiculo = ?, modelo_veiculo = ?, ano_veiculo = ?, placa_veiculo = ?,
                    preco_veiculo = ?, status_veiculo = ?, moto = ?, concessionario_veiculo = ?
                WHERE id_veiculo = ?
                """,
                comando -> {
                    comando.setString(1, veiculo.getMarca());
                    comando.setString(2, veiculo.getModelo());
                    comando.setInt(3, veiculo.getAno());
                    comando.setString(4, veiculo.getPlaca());
                    comando.setDouble(5, veiculo.getPreco());
                    comando.setString(6, paraColunaEnum(veiculo.getStatus()));
                    comando.setBoolean(7, veiculo.isMoto());
                    comando.setLong(8, veiculo.getConcessionaria().getIdConcessionaria());
                    comando.setLong(9, veiculo.getIdVeiculo());
                },
                "Erro ao atualizar veículo no banco.");
    }

    private static Veiculo mapear(ResultSet resultado) throws SQLException {
        Concessionaria concessionaria = new Concessionaria(
                resultado.getLong("id_concessionaria"),
                resultado.getString("name_concessionaria"),
                resultado.getString("address_concessionaria"));

        return new Veiculo(
                resultado.getLong("id_veiculo"),
                resultado.getString("marca_veiculo"),
                resultado.getString("modelo_veiculo"),
                resultado.getInt("ano_veiculo"),
                resultado.getString("placa_veiculo"),
                resultado.getDouble("preco_veiculo"),
                paraEnumJava(resultado.getString("status_veiculo")),
                concessionaria,
                resultado.getBoolean("moto"));
    }

    /**
     * A coluna status_veiculo é ENUM('Disponivel','Vendido','Em_Manutencao'), diferente da
     * grafia toda maiúscula do enum StatusVeiculo em Java. Convertem nos dois sentidos.
     */
    private static String paraColunaEnum(StatusVeiculo status) {
        return switch (status) {
            case DISPONIVEL -> "Disponivel";
            case VENDIDO -> "Vendido";
            case EM_MANUTENCAO -> "Em_Manutencao";
        };
    }

    private static StatusVeiculo paraEnumJava(String valorColuna) {
        return switch (valorColuna) {
            case "Disponivel" -> StatusVeiculo.DISPONIVEL;
            case "Vendido" -> StatusVeiculo.VENDIDO;
            case "Em_Manutencao" -> StatusVeiculo.EM_MANUTENCAO;
            default -> throw new IllegalStateException("Status desconhecido no banco: " + valorColuna);
        };
    }
}
