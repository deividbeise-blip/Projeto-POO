package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

/**
 * Implementação JDBC de {@link ClienteRepository} sobre a tabela {@code cliente}.
 * Traduz as situações de negócio (cliente com vendas, cliente sumido) em IllegalStateException
 * com mensagem pronta para a tela; os demais erros de banco sobem como {@link PersistenciaException}.
 */
public class ClienteDAO implements ClienteRepository {

    static final String MENSAGEM_CLIENTE_COM_VENDAS = "Este cliente possui vendas e não pode ser excluído.";
    private static final String MENSAGEM_NAO_ENCONTRADO = "Cliente não encontrado. Atualize a lista e tente de novo.";

    private static final String COLUNAS = "id_cliente, name_cliente, cpf, email, phone";

    private final ExecutorSql executor;

    /**
     * @param connectionFactory fonte das conexões; participa da transação em andamento, se houver
     * @throws NullPointerException se a fábrica for nula
     */
    public ClienteDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT e coloca no objeto o ID gerado pelo AUTO_INCREMENT.
     *
     * @param cliente cliente novo, ainda sem ID
     * @throws IllegalStateException se o cliente já tiver ID
     * @throws PersistenciaException se o banco recusar a gravação
     */
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

    /**
     * @param id ID do cliente
     * @return o cliente encontrado, ou null se não existir
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public Cliente buscarPorId(Long id) {
        return executor.consultarUm(
                "SELECT " + COLUNAS + " FROM cliente WHERE id_cliente = ?",
                comando -> comando.setLong(1, id),
                ClienteDAO::mapear,
                "Erro ao buscar cliente no banco.");
    }

    /**
     * Necessário para o combo de clientes na tela de Vendas e para a lista da tela de Clientes.
     *
     * @return todos os clientes, sem ordem garantida; vazia se não houver nenhum
     * @throws PersistenciaException se a consulta falhar
     */
    @Override
    public List<Cliente> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM cliente",
                ClienteDAO::mapear,
                "Erro ao listar clientes.");
    }

    /**
     * Atualiza a linha do cliente pelo ID do objeto.
     *
     * @param cliente cliente com o ID já definido e os dados novos
     * @throws IllegalStateException se nenhuma linha foi alterada (cliente não encontrado)
     * @throws PersistenciaException se o banco recusar a atualização
     */
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

    /**
     * Apaga o cliente. Uma violação de chave estrangeira significa que há vendas ligadas a ele.
     *
     * @param id ID do cliente a excluir
     * @throws IllegalStateException se o cliente possuir vendas ou não for encontrado
     * @throws PersistenciaException se o banco falhar por outro motivo
     */
    @Override
    public void excluir(Long id) {
        int linhas;
        try {
            linhas = executor.atualizar(
                    "DELETE FROM cliente WHERE id_cliente = ?",
                    comando -> comando.setLong(1, id),
                    "Erro ao excluir cliente no banco.");
        } catch (PersistenciaException e) {
            if (ErrosSql.violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_CLIENTE_COM_VENDAS, e);
            }
            throw e;
        }
        if (linhas == 0) {
            throw new IllegalStateException(MENSAGEM_NAO_ENCONTRADO);
        }
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
