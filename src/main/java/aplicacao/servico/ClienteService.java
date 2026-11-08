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

    public List<Cliente> listar() {
        return clienteRepository.listar();
    }
}
