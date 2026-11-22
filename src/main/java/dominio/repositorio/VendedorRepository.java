package dominio.repositorio;

import java.util.List;

import dominio.modelo.Vendedor;

/** Onde os vendedores são gravados e lidos. */
public interface VendedorRepository {

    /**
     * Grava o vendedor e preenche o ID gerado.
     *
     * @param vendedor vendedor novo, ainda sem ID
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     */
    void salvar(Vendedor vendedor);

    /**
     * Devolve o vendedor com esse ID, ou null se não existir.
     *
     * @param id ID do vendedor
     * @return o vendedor encontrado, ou null
     */
    Vendedor buscarPorId(Long id);

    /**
     * Devolve todos os vendedores, em ordem de nome.
     *
     * @return lista de vendedores; vazia se não houver nenhum
     */
    List<Vendedor> listar();

    /**
     * Grava nome, CPF, comissão e concessionária do vendedor que tem o ID do objeto recebido.
     *
     * @param vendedor vendedor com o ID já definido e os dados novos
     * @throws IllegalStateException se já existir outro com o mesmo nome ou CPF
     */
    void atualizar(Vendedor vendedor);

    /**
     * Exclui o vendedor.
     *
     * @param id ID do vendedor a excluir
     * @throws IllegalStateException se ele tiver vendas
     */
    void excluir(Long id);
}
