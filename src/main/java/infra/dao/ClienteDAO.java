package infra.dao;

import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

/**
 * Implementação de {@link ClienteRepository} que guarda os clientes na tabela {@code cliente}
 * do banco de dados (MySQL).
 * <p>
 * Todo o acesso JDBC passa pelo {@link ExecutorSql}, com comandos parametrizados. Erros do banco
 * chegam como {@link PersistenciaException}, exceto a exclusão de cliente que possui vendas, que
 * vira uma {@link IllegalStateException} com mensagem própria para o usuário.
 *
 * @see ClienteRepository
 * @see ExecutorSql
 */
public class ClienteDAO implements ClienteRepository {

    /** Mensagem mostrada quando o banco recusa excluir um cliente por causa da chave estrangeira. */
    static final String MENSAGEM_CLIENTE_COM_VENDAS = "Este cliente possui vendas e não pode ser excluído.";
    /** Mensagem usada quando o UPDATE/DELETE não encontra o cliente (ex.: já excluído por outra pessoa). */
    private static final String MENSAGEM_NAO_ENCONTRADO = "Cliente não encontrado. Atualize a lista e tente de novo.";

    /** Código de erro do MySQL: "Cannot delete or update a parent row: a foreign key constraint fails". */
    private static final int MYSQL_ERRO_CHAVE_ESTRANGEIRA = 1451;

    /** Colunas lidas pelo SELECT, na ordem em que o resultado é mapeado para {@link Cliente}. */
    private static final String COLUNAS = "id_cliente, name_cliente, cpf, email, phone";

    private final ExecutorSql executor;

    /**
     * Cria o DAO.
     *
     * @param connectionFactory de onde vêm as conexões com o banco; obrigatória
     */
    public ClienteDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Insere o cliente na tabela e preenche nele o ID gerado pelo banco.
     *
     * @param cliente o cliente a gravar
     * @throws PersistenciaException se o banco falhar
     */
    @Override
    public void salvar(Cliente cliente) {
        long id = executor.inserir(
                "INSERT INTO cliente (name_cliente, cpf, email, phone) VALUES (?, ?, ?, ?)",
                comando -> {
                    comando.setString(1, cliente.getName_cliente());
                    comando.setString(2, cliente.getCpf_cliente());
                    comando.setString(3, cliente.getEmail_cliente());
                    comando.setString(4, cliente.getPhone_cliente());
                },
                "Erro ao salvar cliente no banco.");
        cliente.setId_cliente(id);
    }

    /**
     * Lê todos os clientes da tabela. Necessário para o combo de clientes na tela de Vendas e
     * para a lista da tela de Clientes.
     *
     * @return os clientes com o ID preenchido; lista vazia se a tabela estiver vazia
     * @throws PersistenciaException se o banco falhar
     */
    @Override
    public List<Cliente> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM cliente",
                resultado -> {
                    Cliente cliente = new Cliente(
                            resultado.getString("name_cliente"),
                            resultado.getString("cpf"),
                            resultado.getString("email"),
                            resultado.getString("phone"));
                    cliente.setId_cliente(resultado.getLong("id_cliente"));
                    return cliente;
                },
                "Erro ao listar clientes.");
    }

    /**
     * Atualiza nome, CPF, e-mail e telefone do cliente com o ID do objeto recebido.
     *
     * @param cliente o cliente com os dados novos; precisa ter o ID definido
     * @throws IllegalStateException se nenhuma linha foi atualizada (o cliente não existe mais)
     * @throws PersistenciaException se o banco falhar
     */
    @Override
    public void atualizar(Cliente cliente) {
        int linhas = executor.atualizar(
                "UPDATE cliente SET name_cliente = ?, cpf = ?, email = ?, phone = ? WHERE id_cliente = ?",
                comando -> {
                    comando.setString(1, cliente.getName_cliente());
                    comando.setString(2, cliente.getCpf_cliente());
                    comando.setString(3, cliente.getEmail_cliente());
                    comando.setString(4, cliente.getPhone_cliente());
                    comando.setLong(5, cliente.getId_cliente());
                },
                "Erro ao atualizar cliente no banco.");
        if (linhas == 0) {
            throw new IllegalStateException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    /**
     * Exclui o cliente de ID informado.
     * <p>
     * Se o cliente tiver vendas, o banco recusa a exclusão por causa da chave estrangeira; esse
     * caso é reconhecido e traduzido para uma mensagem clara em vez do erro técnico.
     *
     * @param id o ID do cliente a excluir
     * @throws IllegalStateException com a mensagem "Este cliente possui vendas e não pode ser
     *         excluído." se houver vendas ligadas ao cliente, ou se ele não existir mais
     * @throws PersistenciaException se o banco falhar por qualquer outro motivo
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
            if (violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_CLIENTE_COM_VENDAS, e);
            }
            throw e;
        }
        if (linhas == 0) {
            throw new IllegalStateException(MENSAGEM_NAO_ENCONTRADO);
        }
    }

    /**
     * Procura, na cadeia de causas do erro, um {@link SQLException} de violação de integridade:
     * código 1451 do MySQL ou SQLState começando com {@code 23}.
     *
     * @param erro o erro capturado
     * @return {@code true} se o erro foi causado por uma violação de chave estrangeira/integridade
     */
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
}