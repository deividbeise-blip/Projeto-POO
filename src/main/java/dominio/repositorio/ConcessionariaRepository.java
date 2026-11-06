package dominio.repositorio;

import java.util.List;

import dominio.modelo.Concessionaria;

public interface ConcessionariaRepository {

    Concessionaria buscarPorId(Long id);

    List<Concessionaria> listar();
}
