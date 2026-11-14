package dominio.repositorio;

import java.util.List;

import dominio.modelo.Concessionaria;

/** Onde as concessionárias são gravadas e lidas. */
public interface ConcessionariaRepository {

    /**
     * Grava a concessionária e preenche o ID gerado.
     *
     * @param concessionaria concessionária nova, ainda sem ID
     * @throws IllegalStateException se já existir outra com o mesmo nome ou endereço
     */
    void salvar(Concessionaria concessionaria);

    /**
     * Devolve a concessionária com esse ID, ou null se não existir.
     *
     * @param id ID da concessionária
     * @return a concessionária encontrada, ou null
     */
    Concessionaria buscarPorId(Long id);

    /**
     * Devolve todas as concessionárias, em ordem de nome.
     *
     * @return lista de concessionárias; vazia se não houver nenhuma
     */
    List<Concessionaria> listar();

    /**
     * Grava nome e endereço da concessionária que tem o ID do objeto recebido.
     *
     * @param concessionaria concessionária com o ID já definido e os dados novos
     * @throws IllegalStateException se já existir outra com o mesmo nome ou endereço
     */
    void atualizar(Concessionaria concessionaria);

    /**
     * Exclui a concessionária.
     *
     * @param id ID da concessionária a excluir
     * @throws IllegalStateException se ela ainda tiver veículos ou vendedores
     */
    void excluir(Long id);
}
