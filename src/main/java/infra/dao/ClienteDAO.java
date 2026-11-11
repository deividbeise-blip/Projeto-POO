package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    /** Necessário para o combo de clientes na tela de Vendas. */
    @Override
    public List<Cliente> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM cliente",
                ClienteDAO::mapear,
                "Erro ao listar clientes.");
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
