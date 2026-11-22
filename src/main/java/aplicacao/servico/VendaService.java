package aplicacao.servico;

import aplicacao.Transacao;
import aplicacao.dto.DadosPagamento;
import aplicacao.dto.DadosVenda;
import aplicacao.pagamento.FormaPagamento;
import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.modelo.Cliente;
import dominio.modelo.Pagamento;
import dominio.modelo.Veiculo;
import dominio.modelo.Venda;
import dominio.modelo.Vendedor;
import dominio.modelo.ResumoVenda;
import dominio.repositorio.ClienteRepository;
import dominio.repositorio.PagamentoRepository;
import dominio.repositorio.VeiculoRepository;
import dominio.repositorio.VendaRepository;
import dominio.repositorio.VendedorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Caso de uso de venda: monta a venda a partir de IDs, aplica as regras do domínio e grava
 * venda, pagamento, detalhe do pagamento e status do veículo numa única transação.
 * Não conhece nenhuma forma de pagamento concreta: recebe o mapa de formas pronto do AppConfig,
 * por isso uma forma nova (ex.: dinheiro) entra sem alterar esta classe.
 */
public class VendaService {
    private final ClienteRepository clienteRepository;
    private final VendedorRepository vendedorRepository;
    private final VeiculoRepository veiculoRepository;
    private final VendaRepository vendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final Map<Class<? extends DadosPagamento>, FormaPagamento<?, ?>> formasPagamento;
    private final Transacao transacao;

    /**
     * @param clienteRepository busca o cliente da venda
     * @param vendedorRepository busca o vendedor e lista os vendedores para a tela
     * @param veiculoRepository busca o veículo e grava o novo status (vendido)
     * @param vendaRepository grava a venda e consulta o histórico
     * @param pagamentoRepository grava a parte comum do pagamento
     * @param formasPagamento liga cada tipo de DTO ({@code DadosPix}, {@code DadosCartao}...) à sua forma;
     *                        é copiado, então mudanças posteriores no mapa original não afetam o serviço
     * @param transacao garante que tudo seja gravado junto ou nada seja gravado
     */
    public VendaService(ClienteRepository clienteRepository,
                        VendedorRepository vendedorRepository,
                        VeiculoRepository veiculoRepository,
                        VendaRepository vendaRepository,
                        PagamentoRepository pagamentoRepository,
                        Map<Class<? extends DadosPagamento>, FormaPagamento<?, ?>> formasPagamento,
                        Transacao transacao) {
        this.clienteRepository = clienteRepository;
        this.vendedorRepository = vendedorRepository;
        this.veiculoRepository = veiculoRepository;
        this.vendaRepository = vendaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.formasPagamento = Map.copyOf(formasPagamento);
        this.transacao = transacao;
    }

    /**
     * Vendedores que podem ser escolhidos na tela de venda.
     *
     * @return todos os vendedores cadastrados; vazia se não houver nenhum
     */
    public List<Vendedor> listarVendedores() {
        return vendedorRepository.listar();
    }

    /**
     * Retorna as vendas concluídas para o histórico da interface.
     *
     * @return vendas da mais recente para a mais antiga, já com a forma de pagamento no formato de exibição
     */
    public List<ResumoVenda> listarVendas() {
        return vendaRepository.listarHistorico();
    }

    /**
     * Realiza uma venda com qualquer forma de pagamento registrada no AppConfig.
     * A forma é escolhida pelo tipo do DTO de pagamento (DadosPix, DadosCartao...).
     *
     * @param dados IDs de cliente, vendedor e veículo, mais os dados do pagamento escolhido
     * @return o pagamento gravado, ligado à venda (use {@code getVenda().obterRecibo()} para o recibo)
     * @throws IllegalArgumentException se faltar dado, a forma não for suportada, alguma entidade não existir
     *                                  ou o dado do pagamento for inválido
     * @throws VeiculoIndisponivelException se o veículo já foi vendido ou está em manutenção
     * @throws VendaInvalidaException se a venda não passar nas regras do domínio
     */
    public Pagamento vender(DadosVenda dados) throws VeiculoIndisponivelException, VendaInvalidaException {
        if (dados == null || dados.pagamento() == null) {
            throw new IllegalArgumentException("Informe os dados da venda e do pagamento.");
        }
        FormaPagamento<?, ?> forma = formasPagamento.get(dados.pagamento().getClass());
        if (forma == null) {
            throw new IllegalArgumentException("Forma de pagamento não suportada");
        }

        Cliente cliente = buscarCliente(dados.idCliente());
        Vendedor vendedor = buscarVendedor(dados.idVendedor());
        Veiculo veiculo = buscarVeiculo(dados.idVeiculo());

        Venda venda = new Venda(cliente, vendedor, veiculo, LocalDate.now());
        return concluir(venda, forma, dados.pagamento());
    }

    /**
     * O mapa liga cada classe de DTO à sua forma de pagamento (DadosPix -> FormaPagamentoPix),
     * por isso o cast para D é seguro.
     */
    @SuppressWarnings("unchecked")
    private <D extends DadosPagamento, P extends Pagamento> P concluir(
            Venda venda, FormaPagamento<D, P> forma, DadosPagamento dadosPagamento)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        P pagamento = forma.criarPagamento(venda, (D) dadosPagamento);
        venda.registrarPagamento(pagamento);

        // 1) Regras de negócio primeiro: se falhar aqui, NADA foi gravado no banco.
        venda.finalizarVenda();

        // 2) Só então persiste, tudo na mesma transação, na ordem que as chaves estrangeiras exigem.
        try {
            transacao.executar(() -> {
                vendaRepository.salvar(venda);
                pagamentoRepository.salvar(pagamento);
                forma.salvarDetalhe(pagamento);
                vendaRepository.atualizarPagamento(venda);
                veiculoRepository.atualizarStatus(venda.getVeiculo());
            });
        } catch (RuntimeException e) {
            venda.getVeiculo().cancelarVenda(); // desfaz o estado em memória
            throw e;
        }
        return pagamento;
    }

    // ---- Busca das entidades a partir dos IDs recebidos no DTO ----

    private Cliente buscarCliente(Long id) {
        Cliente cliente = id == null ? null : clienteRepository.buscarPorId(id);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado (id " + id + ").");
        }
        return cliente;
    }

    private Vendedor buscarVendedor(Long id) {
        Vendedor vendedor = id == null ? null : vendedorRepository.buscarPorId(id);
        if (vendedor == null) {
            throw new IllegalArgumentException("Vendedor não encontrado (id " + id + ").");
        }
        return vendedor;
    }

    private Veiculo buscarVeiculo(Long id) {
        Veiculo veiculo = id == null ? null : veiculoRepository.buscarPorId(id);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não encontrado (id " + id + ").");
        }
        return veiculo;
    }
}
