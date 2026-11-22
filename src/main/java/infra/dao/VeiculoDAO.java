package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;
import dominio.repositorio.VeiculoRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

/**
 * Acesso à tabela veiculo. As leituras já trazem a concessionária por JOIN
 * e o status é convertido entre o ENUM do banco e {@link StatusVeiculo}.
 */
public class VeiculoDAO implements VeiculoRepository {

    static final String MENSAGEM_VEICULO_COM_VENDAS = "Este veículo possui vendas e não pode ser excluído.";

    // Uma consulta só: o JOIN traz a concessionária junto, sem consulta extra por linha.
    private static final String SELECT_COM_CONCESSIONARIA = """
            SELECT v.id_veiculo, v.marca_veiculo, v.modelo_veiculo, v.ano_veiculo, v.placa_veiculo,
                   v.preco_veiculo, v.status_veiculo, v.moto,
                   c.id_concessionaria, c.name_concessionaria, c.address_concessionaria
            FROM veiculo v
            JOIN concessionaria c ON c.id_concessionaria = v.concessionario_veiculo
            """;

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public VeiculoDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT e coloca no objeto o ID gerado.
     *
     * @param veiculo veículo novo, ainda sem ID
     * @throws PersistenciaException se o banco recusar a gravação (placa repetida, por exemplo)
     */
    @Override
    public void salvar(Veiculo veiculo) {
        // A coluna no banco é "concessionario_veiculo" (sem "a"), não "concessionaria_veiculo".
        long id = executor.inserir(
                """
                INSERT INTO veiculo
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
     *
     * @param veiculo veículo com ID e status novo
     * @throws PersistenciaException se a atualização falhar
     */
    @Override
    public void atualizarStatus(Veiculo veiculo) {
        executor.atualizar(
                "UPDATE veiculo SET status_veiculo = ? WHERE id_veiculo = ?",
                comando -> {
                    comando.setString(1, paraColunaEnum(veiculo.getStatus()));
                    comando.setLong(2, veiculo.getIdVeiculo());
                },
                "Erro ao atualizar status do veículo no banco.");
    }

    /**
     * @param id ID do veículo
     * @return o veículo com sua concessionária, ou null se não existir
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public Veiculo buscarPorId(Long id) {
        return executor.consultarUm(
                SELECT_COM_CONCESSIONARIA + " WHERE v.id_veiculo = ?",
                comando -> comando.setLong(1, id),
                VeiculoDAO::mapear,
                "Erro ao buscar veículo no banco.");
    }

    /**
     * @return só os veículos com status Disponivel, que podem ser vendidos
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public List<Veiculo> listarDisponiveis() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA + " WHERE v.status_veiculo = 'Disponivel'",
                VeiculoDAO::mapear,
                "Erro ao listar veículos disponíveis.");
    }

    /**
     * @return todos os veículos, de qualquer status
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public List<Veiculo> listarTodos() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA,
                VeiculoDAO::mapear,
                "Erro ao listar veículos.");
    }

    /**
     * Apaga o veículo. Se o banco recusar por chave estrangeira (há venda ligada a ele),
     * lança {@link IllegalStateException} com uma mensagem que o usuário entende.
     *
     * @param id ID do veículo
     * @throws IllegalStateException se houver venda ligada ao veículo
     * @throws PersistenciaException se o banco falhar por outro motivo
     */
    @Override
    public void excluir(Long id) {
        try {
            executor.atualizar(
                    "DELETE FROM veiculo WHERE id_veiculo = ?",
                    comando -> comando.setLong(1, id),
                    "Erro ao excluir veículo no banco.");
        } catch (PersistenciaException e) {
            if (ErrosSql.violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_VEICULO_COM_VENDAS, e);
            }
            throw e;
        }
    }

    /**
     * UPDATE de todos os dados do veículo, pelo ID.
     *
     * @param veiculo veículo com ID e dados novos
     * @throws PersistenciaException se o banco recusar a atualização
     */
    @Override
    public void atualizar(Veiculo veiculo) {
        executor.atualizar(
                """
                UPDATE veiculo
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
