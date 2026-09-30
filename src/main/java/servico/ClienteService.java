package servico;

import dao.ClienteDAO;
import modelo.Cliente;

public class ClienteService {
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /** Valida (construtor de Cliente) e só então grava. Lança IllegalArgumentException se inválido. */
    public Cliente cadastrar(String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        clienteDAO.salvar(cliente);
        return cliente;
    }
}
