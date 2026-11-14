package dominio.repositorio;

import java.util.List;

import dominio.modelo.Cliente;

/**
 * Contrato de persistência de clientes.
 * O domínio e o serviço dependem só desta interface; a implementação com banco fica em {@code infra.dao.ClienteDAO}.
 */
public interface ClienteRepository {

    /**
     * Grava o cliente e preenche o ID gerado.
     *
     * @param cliente cliente novo, ainda sem ID
     */
    void salvar(Cliente cliente);

    /**
     * Devolve o cliente com esse ID, ou null se não existir.
     *
     * @param id ID do cliente
     * @return o cliente encontrado, ou null
     */
    Cliente buscarPorId(Long id);

    /**
     * Devolve todos os clientes cadastrados.
     *
     * @return lista de clientes; vazia se não houver nenhum
     */
    List<Cliente> listar();

    /**
     * Atualiza nome, CPF, e-mail e telefone do cliente que tem o ID do objeto recebido.
     *
     * @param cliente cliente com o ID já definido e os dados novos
     * @throws IllegalStateException se o cliente não existir mais
     */
    void atualizar(Cliente cliente);

    /**
     * Exclui o cliente.
     *
     * @param id ID do cliente a excluir
     * @throws IllegalStateException se o cliente possuir vendas ou não existir mais
     */
    void excluir(Long id);
}
