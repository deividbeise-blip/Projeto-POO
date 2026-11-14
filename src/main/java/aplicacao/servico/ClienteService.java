package aplicacao.servico;

import java.util.List;

import dominio.modelo.Cliente;
import dominio.repositorio.ClienteRepository;

/**
 * Casos de uso de clientes: cadastrar, listar, atualizar e excluir.
 * <p>
 * É a camada que a tela chama. Ela cria o {@link Cliente} (o que valida os dados) e só então pede
 * ao {@link ClienteRepository} para gravar, de modo que dado inválido nunca chega ao banco.
 *
 * @see Cliente
 * @see ClienteRepository
 */
public class ClienteService {
    private final ClienteRepository clienteRepository;

    /**
     * Cria o serviço.
     *
     * @param clienteRepository onde os clientes são guardados
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Valida (construtor de Cliente) e só então grava.
     *
     * @param nome     nome do cliente
     * @param cpf      CPF, com ou sem pontuação
     * @param email    e-mail
     * @param telefone telefone com DDD, com ou sem pontuação
     * @return o cliente gravado, já com o ID gerado pelo banco
     * @throws IllegalArgumentException se algum dado for inválido
     * @throws infra.excecao.PersistenciaException se o banco falhar
     */
    public Cliente cadastrar(String nome, String cpf, String email, String telefone) {
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        clienteRepository.salvar(cliente);
        return cliente;
    }

    /**
     * Lista todos os clientes cadastrados.
     *
     * @return os clientes, cada um com o ID preenchido
     * @throws infra.excecao.PersistenciaException se o banco falhar
     */
    public List<Cliente> listar() {
        return clienteRepository.listar();
    }

    /**
     * Valida e atualiza o cliente de ID informado. Como o {@link Cliente} é imutável, os dados
     * novos viram um novo objeto, que recebe o ID do cliente existente.
     *
     * @param id       ID do cliente a atualizar
     * @param nome     novo nome
     * @param cpf      novo CPF, com ou sem pontuação
     * @param email    novo e-mail
     * @param telefone novo telefone com DDD, com ou sem pontuação
     * @return o cliente atualizado
     * @throws IllegalArgumentException se o ID for nulo ou algum dado for inválido
     * @throws IllegalStateException se o cliente não existir mais
     * @throws infra.excecao.PersistenciaException se o banco falhar
     */
    public Cliente atualizar(Long id, String nome, String cpf, String email, String telefone) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para editar.");
        }
        Cliente cliente = new Cliente(nome, cpf, email, telefone);
        cliente.setId_cliente(id);
        clienteRepository.atualizar(cliente);
        return cliente;
    }

    /**
     * Exclui o cliente.
     *
     * @param id ID do cliente a excluir
     * @throws IllegalArgumentException se o ID for nulo
     * @throws IllegalStateException com a mensagem "Este cliente possui vendas e não pode ser
     *         excluído." se houver vendas ligadas a ele, ou se o cliente não existir mais
     * @throws infra.excecao.PersistenciaException se o banco falhar por outro motivo
     */
    public void excluir(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Selecione um cliente para excluir.");
        }
        clienteRepository.excluir(id);
    }
}