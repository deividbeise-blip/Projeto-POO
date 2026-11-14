package dominio.repositorio;

import java.util.List;

import dominio.modelo.Cliente;

/**
 * Contrato de persistência dos clientes: o que o restante da aplicação pode pedir sem saber
 * como (nem onde) os dados são guardados. A implementação com banco de dados é o
 * {@link infra.dao.ClienteDAO}.
 *
 * @see dominio.modelo.Cliente
 * @see aplicacao.servico.ClienteService
 */
public interface ClienteRepository {

    /**
     * Grava o cliente e preenche o ID gerado.
     *
     * @param cliente o cliente a gravar; depois da chamada, {@link Cliente#getId_cliente()} devolve o ID
     */
    void salvar(Cliente cliente);

    /**
     * Lista todos os clientes cadastrados.
     *
     * @return os clientes, cada um já com o ID preenchido; lista vazia se não houver nenhum
     */
    List<Cliente> listar();

    /**
     * Atualiza nome, CPF, e-mail e telefone do cliente que tem o ID do objeto recebido.
     *
     * @param cliente o cliente com os dados novos; precisa ter o ID definido
     * @throws IllegalStateException se o cliente não existir mais
     */
    void atualizar(Cliente cliente);

    /**
     * Exclui o cliente.
     *
     * @param id o ID do cliente a excluir
     * @throws IllegalStateException se o cliente possuir vendas ou não existir mais
     */
    void excluir(Long id);
}