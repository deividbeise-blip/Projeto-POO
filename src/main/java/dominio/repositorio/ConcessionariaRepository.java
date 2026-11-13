package dominio.repositorio;

import java.util.List;

import dominio.modelo.Concessionaria;

/** Onde as concessionárias são gravadas e lidas. */
public interface ConcessionariaRepository {

    /**
     * Grava a concessionária e preenche o ID gerado.
     *
     * @throws IllegalStateException se já existir outra com o mesmo nome ou endereço
     */
    void salvar(Concessionaria concessionaria);

    /** Devolve a concessionária com esse ID, ou null se não existir. */
    Concessionaria buscarPorId(Long id);

    /** Devolve todas as concessionárias, em ordem de nome. */
    List<Concessionaria> listar();

    /**
     * Grava nome e endereço da concessionária que tem o ID do objeto recebido.
     *
     * @throws IllegalStateException se já existir outra com o mesmo nome ou endereço
     */
    void atualizar(Concessionaria concessionaria);

    /**
     * Exclui a concessionária.
     *
     * @throws IllegalStateException se ela ainda tiver veículos ou vendedores
     */
    void excluir(Long id);
}
