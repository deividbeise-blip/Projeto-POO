package dominio.repositorio;

import java.util.List;

import dominio.modelo.Vendedor;

/** Onde os vendedores são gravados e lidos. */
public interface VendedorRepository {

    /**
     * Grava o vendedor e preenche o ID gerado.
     *
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     */
    void salvar(Vendedor vendedor);

    /** Devolve o vendedor com esse ID, ou null se não existir. */
    Vendedor buscarPorId(Long id);

    /** Devolve todos os vendedores, em ordem de nome. */
    List<Vendedor> listar();

    /**
     * Grava nome, CPF, comissão e concessionária do vendedor que tem o ID do objeto recebido.
     *
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     */
    void atualizar(Vendedor vendedor);

    /**
     * Exclui o vendedor.
     *
     * @throws IllegalStateException se ele tiver vendas
     */
    void excluir(Long id);
}
