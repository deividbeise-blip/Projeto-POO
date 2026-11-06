package dominio.repositorio;

import java.util.List;

import dominio.modelo.Vendedor;

public interface VendedorRepository {

    List<Vendedor> listar();
}
