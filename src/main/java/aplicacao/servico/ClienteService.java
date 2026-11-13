package aplicacao.servico;

import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /** Valida (construtor de Cliente) e só então grava. Lança IllegalArgumentException se inválido. */
    public Cliente cadastrar(String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        clienteRepository.salvar(cliente);
        return cliente;
    }

    /** Devolve todos os clientes cadastrados. */
    public List<Cliente> listar() {
        return clienteRepository.listar();
    }

    /** Valida e atualiza o cliente de ID informado. Lança IllegalArgumentException se inválido. */
    public Cliente atualizar(Long id, String nome, String cpf, String email, String telefone) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para editar.");
        }
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        cliente.setIdCliente(id);
        clienteRepository.atualizar(cliente);
        return cliente;
    }

    /**
     * Exclui o cliente. Lança IllegalStateException com a mensagem
     * "Este cliente possui vendas e não pode ser excluído."
     */
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para excluir.");
        }
        clienteRepository.excluir(id);
    }
}