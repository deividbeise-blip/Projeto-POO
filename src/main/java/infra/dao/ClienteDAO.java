package infra.dao;

import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;
import infra.conexao.ConnectionFactory;

public class ClienteDAO implements ClienteRepository {

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
                    comando.setString(1, cliente.getName_cliente());
                    comando.setString(2, cliente.getCpf_cliente());
                    comando.setString(3, cliente.getEmail_cliente());
                    comando.setString(4, cliente.getPhone_cliente());
                },
                "Erro ao salvar cliente no banco.");
        cliente.setId_cliente(id);
    }

    /** Necessário para o combo de clientes na tela de Vendas. */
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
}
