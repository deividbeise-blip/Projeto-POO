package dominio.repositorio;

import java.util.List;

import dominio.modelo.Cliente;

public interface ClienteRepository {

    /**
     * Grava o cliente e preenche o ID gerado
     */
    void salvar(Cliente cliente);

    List<Cliente> listar();

    /**
     * Atualiza nome, CPF, e-mail e telefone do cliente que tem o ID do objeto recebido.
     *
     * @throws IllegalStateException se o cliente não existir mais
     */
    void atualizar(Cliente cliente);

    /**
     * Exclui o cliente.
     *
     * @throws IllegalStateException se o cliente possuir vendas ou não existir mais
     */
    void excluir(Long id);
}