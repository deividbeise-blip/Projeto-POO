package dominio.repositorio;

import java.util.List;

import dominio.modelo.Cliente;

public interface ClienteRepository {

    /** Grava o cliente e preenche o ID gerado. */
    void salvar(Cliente cliente);

    List<Cliente> listar();
}
