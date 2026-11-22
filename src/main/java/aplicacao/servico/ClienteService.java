package aplicacao.servico;

import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;

/**
 * Casos de uso de cliente usados pela tela: cadastrar, listar, atualizar e excluir.
 * Garante que nada inválido chegue ao repositório; as regras de cada campo ficam em {@link Cliente}.
 * Erros de banco chegam como {@code PersistenciaException} e não são tratados aqui.
 */
public class ClienteService {
    private final ClienteRepository clienteRepository;

    /**
     * @param clienteRepository onde os clientes são gravados e lidos
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Valida (construtor de Cliente) e só então grava.
     *
     * @param nome nome do cliente
     * @param cpf CPF com ou sem máscara
     * @param email e-mail do cliente
     * @param telefone telefone com DDD, com ou sem máscara
     * @return o cliente gravado, já com o ID gerado e os dados normalizados
     * @throws IllegalArgumentException se algum campo for inválido
     */
    public Cliente cadastrar(String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        clienteRepository.salvar(cliente);
        return cliente;
    }

    /**
     * Devolve todos os clientes cadastrados.
     *
     * @return lista de clientes; vazia se não houver nenhum
     */
    public List<Cliente> listar() {
        return clienteRepository.listar();
    }

    /**
     * Valida e atualiza o cliente de ID informado.
     *
     * @param id ID do cliente em edição
     * @param nome nome novo
     * @param cpf CPF novo, com ou sem máscara
     * @param email e-mail novo
     * @param telefone telefone novo, com ou sem máscara
     * @return o cliente com os dados atualizados e normalizados
     * @throws IllegalArgumentException se nenhum cliente foi selecionado (ID nulo) ou algum campo for inválido
     * @throws IllegalStateException se o cliente não existir mais no banco
     */
    public Cliente atualizar(Long id, String nome, String cpf, String email, String telefone) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para editar.");
        }
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        cliente.setIdCliente(id);
        clienteRepository.atualizar(cliente);
        return cliente;
    }

    /**
     * Exclui o cliente. Lança IllegalStateException com a mensagem
     * "Este cliente possui vendas e não pode ser excluído."
     *
     * @param id ID do cliente a excluir
     * @throws IllegalArgumentException se nenhum cliente foi selecionado (ID nulo)
     * @throws IllegalStateException se o cliente possuir vendas ou não existir mais
     */
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para excluir.");
        }
        clienteRepository.excluir(id);
    }
}