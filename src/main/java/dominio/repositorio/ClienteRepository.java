package dominio.repositorio;

import java.util.List;

import dominio.modelo.Cliente;

public interface ClienteRepository {

    /** Grava o cliente e preenche o ID gerado. */
    void salvar(Cliente cliente);

    /** Devolve o cliente com esse ID, ou null se não existir. */
    Cliente buscarPorId(Long id);

    List<Cliente> listar();
}
