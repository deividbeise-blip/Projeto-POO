package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.modelo.Vendedor;
import dominio.repositorio.VendedorRepository;
import infra.conexao.ConnectionFactory;

public class VendedorDAO implements VendedorRepository {

    // Uma consulta só: o JOIN traz a concessionária junto, sem consulta extra por linha.
    private static final String SELECT_COM_CONCESSIONARIA = """
            SELECT v.id_vendedor, v.name_vendedor, v.cpf_vendedor, v.comissao_percentual,
                   c.id_concessionaria, c.name_concessionaria, c.address_concessionaria
            FROM vendedor v
            JOIN concessionaria c ON c.id_concessionaria = v.concessionario_vendedor
            """;

    private final ExecutorSql executor;

    public VendedorDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    public void salvar(Vendedor vendedor) {
        // A coluna no banco é "concessionario_vendedor" (sem "a"), não "concessionaria_vendedor".
        long id = executor.inserir(
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
                "Erro ao salvar Vendedor no banco.");
        vendedor.setIdVendedor(id);
    }

    @Override
    public Vendedor buscarPorId(Long id) {
        return executor.consultarUm(
                SELECT_COM_CONCESSIONARIA + " WHERE v.id_vendedor = ?",
                comando -> comando.setLong(1, id),
                VendedorDAO::mapear,
                "Erro ao buscar vendedor no banco.");
    }

    @Override
    public List<Vendedor> listar() {
        return executor.consultar(
                SELECT_COM_CONCESSIONARIA,
                VendedorDAO::mapear,
                "Erro ao listar vendedores.");
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
