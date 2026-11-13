package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

public class ClienteDAO implements ClienteRepository {

    static final String MENSAGEM_CLIENTE_COM_VENDAS = "Este cliente possui vendas e não pode ser excluído.";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Cliente não encontrado. Atualize a lista e tente de novo.";

    /** Código de erro do MySQL: "Cannot delete or update a parent row: a foreign key constraint fails". */
    private static final int MYSQL_ERRO_CHAVE_ESTRANGEIRA = 1451;

    private static final String COLUNAS = "id_cliente, name_cliente, cpf, email, phone";

    private final ExecutorSql executor;

    public ClienteDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    @Override
    public void salvar(Cliente cliente) {
        long id = executor.inserir(
                "INSERT INTO cliente (name_cliente, cpf, email, phone) VALUES (?, ?, ?, ?)",
                comando -> {
                    comando.setString(1, cliente.getNome());
                    comando.setString(2, cliente.getCpf());
                    comando.setString(3, cliente.getEmail());
                    comando.setString(4, cliente.getTelefone());
                },
                "Erro ao salvar cliente no banco.");
        cliente.setIdCliente(id);
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return executor.consultarUm(
                "SELECT " + COLUNAS + " FROM cliente WHERE id_cliente = ?",
                comando -> comando.setLong(1, id),
                ClienteDAO::mapear,
                "Erro ao buscar cliente no banco.");
    }

    /** Necessário para o combo de clientes na tela de Vendas e para a lista da tela de Clientes. */
    @Override
    public List<Cliente> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM cliente",
                ClienteDAO::mapear,
                "Erro ao listar clientes.");
    }

    @Override
    public void atualizar(Cliente cliente) {
        int linhas = executor.atualizar(
                "UPDATE cliente SET name_cliente = ?, cpf = ?, email = ?, phone = ? WHERE id_cliente = ?",
                comando -> {
                    comando.setString(1, cliente.getNome());
                    comando.setString(2, cliente.getCpf());
                    comando.setString(3, cliente.getEmail());
                    comando.setString(4, cliente.getTelefone());
                    comando.setLong(5, cliente.getIdCliente());
                },
                "Erro ao atualizar cliente no banco.");
        if (linhas == 0) {
            throw new IllegalStateException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    @Override
    public void excluir(Long id) {
        int linhas;
        try {
            linhas = executor.atualizar(
                    "DELETE FROM cliente WHERE id_cliente = ?",
                    comando -> comando.setLong(1, id),
                    "Erro ao excluir cliente no banco.");
        } catch (PersistenciaException e) {
            if (violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_CLIENTE_COM_VENDAS, e);
            }
            throw e;
        }
        if (linhas == 0) {
            throw new IllegalStateException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    /** Procura, na cadeia de causas, um SQLException de violação de integridade (SQLState 23xxx). */
    private static boolean violaChaveEstrangeira(Throwable erro) {
        for (Throwable causa = erro; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sql) {
                String estado = sql.getSQLState();
                if (sql.getErrorCode() == MYSQL_ERRO_CHAVE_ESTRANGEIRA
                        || (estado != null && estado.startsWith("23"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Cliente mapear(ResultSet resultado) throws SQLException {
        Cliente cliente = new Cliente(
                resultado.getString("name_cliente"),
                resultado.getString("cpf"),
                resultado.getString("email"),
                resultado.getString("phone"));
        cliente.setIdCliente(resultado.getLong("id_cliente"));
        return cliente;
    }
}
