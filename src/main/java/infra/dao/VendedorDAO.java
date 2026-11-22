package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.modelo.Vendedor;
import dominio.repositorio.VendedorRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

/** Acesso à tabela vendedor. */
public class VendedorDAO implements VendedorRepository {

    static final String MENSAGEM_DUPLICADO = "Já existe um vendedor com esse nome ou CPF.";
    static final String MENSAGEM_COM_VENDAS = "Este vendedor possui vendas e não pode ser excluído.";

    // Uma consulta só: o JOIN traz a concessionária junto, sem consulta extra por linha.
    private static final String SELECT_COM_CONCESSIONARIA = """
            SELECT v.id_vendedor, v.name_vendedor, v.cpf_vendedor, v.comissao_percentual,
                   c.id_concessionaria, c.name_concessionaria, c.address_concessionaria
            FROM vendedor v
            JOIN concessionaria c ON c.id_concessionaria = v.concessionario_vendedor
            """;

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public VendedorDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT e coloca no objeto o ID gerado.
     *
     * @param vendedor vendedor novo, ainda sem ID
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     * @throws PersistenciaException se o banco falhar por outro motivo
     */
    @Override
    public void salvar(Vendedor vendedor) {
        // A coluna no banco é "concessionario_vendedor" (sem "a"), não "concessionaria_vendedor".
        long id;
        try {
            id = executor.inserir(
                    """
                    INSERT INTO vendedor
                    (name_vendedor, cpf_vendedor, comissao_percentual, concessionario_vendedor)
                    VALUES (?, ?, ?, ?)
                    """,
                    comando -> {
                        comando.setString(1, vendedor.getNome());
                        comando.setString(2, vendedor.getCpf());
                        comando.setDouble(3, vendedor.getComissaoPercentual());
                        comando.setLong(4, vendedor.getConcessionaria().getIdConcessionaria());
                    },
                    "Erro ao salvar vendedor no banco.");
        } catch (PersistenciaException e) {
            throw traduzirDuplicado(e);
        }
        vendedor.setIdVendedor(id);
    }

    /**
     * @param id ID do vendedor
     * @return o vendedor com sua concessionária, ou null se não existir
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public Vendedor buscarPorId(Long id) {
        return executor.consultarUm(
                SELECT_COM_CONCESSIONARIA + " WHERE v.id_vendedor = ?",
                comando -> comando.setLong(1, id),
                VendedorDAO::mapear,
                "Erro ao buscar vendedor no banco.");
    }

    /**
     * @return todos os vendedores, em ordem de nome
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public List<Vendedor> listar() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA + " ORDER BY v.name_vendedor",
                VendedorDAO::mapear,
                "Erro ao listar vendedores.");
    }

    /**
     * Atualiza nome, CPF, comissão e concessionária pelo ID do objeto.
     *
     * @param vendedor vendedor com ID e dados novos
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     * @throws PersistenciaException se o banco falhar por outro motivo
     */
    @Override
    public void atualizar(Vendedor vendedor) {
        try {
            executor.atualizar(
                    """
                    UPDATE vendedor
                    SET name_vendedor = ?, cpf_vendedor = ?, comissao_percentual = ?, concessionario_vendedor = ?
                    WHERE id_vendedor = ?
                    """,
                    comando -> {
                        comando.setString(1, vendedor.getNome());
                        comando.setString(2, vendedor.getCpf());
                        comando.setDouble(3, vendedor.getComissaoPercentual());
                        comando.setLong(4, vendedor.getConcessionaria().getIdConcessionaria());
                        comando.setLong(5, vendedor.getIdVendedor());
                    },
                    "Erro ao atualizar vendedor no banco.");
        } catch (PersistenciaException e) {
            throw traduzirDuplicado(e);
        }
    }

    /**
     * Apaga o vendedor. Uma violação de chave estrangeira significa que há vendas dele.
     *
     * @param id ID do vendedor
     * @throws IllegalStateException se o vendedor tiver vendas
     * @throws PersistenciaException se o banco falhar por outro motivo
     */
    @Override
    public void excluir(Long id) {
        try {
            executor.atualizar(
                    "DELETE FROM vendedor WHERE id_vendedor = ?",
                    comando -> comando.setLong(1, id),
                    "Erro ao excluir vendedor no banco.");
        } catch (PersistenciaException e) {
            if (ErrosSql.violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_COM_VENDAS, e);
            }
            throw e;
        }
    }

    private static RuntimeException traduzirDuplicado(PersistenciaException e) {
        return ErrosSql.violaValorUnico(e) ? new IllegalStateException(MENSAGEM_DUPLICADO, e) : e;
    }

    private static Vendedor mapear(ResultSet resultado) throws SQLException {
        Concessionaria concessionaria = new Concessionaria(
                resultado.getLong("id_concessionaria"),
                resultado.getString("name_concessionaria"),
                resultado.getString("address_concessionaria"));

        return new Vendedor(
                resultado.getLong("id_vendedor"),
                resultado.getString("name_vendedor"),
                resultado.getString("cpf_vendedor"),
                resultado.getDouble("comissao_percentual"),
                concessionaria);
    }
}
