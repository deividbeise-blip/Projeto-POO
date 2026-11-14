package dominio.repositorio;

import java.util.List;

import dominio.modelo.Vendedor;

public interface VendedorRepository {

    /** Devolve o vendedor com esse ID, ou null se não existir. */
    Vendedor buscarPorId(Long id);

    List<Vendedor> listar();
}
