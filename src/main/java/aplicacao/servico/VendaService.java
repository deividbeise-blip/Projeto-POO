package aplicacao.servico;

import aplicacao.Transacao;
import aplicacao.dto.DadosCartao;
import aplicacao.dto.DadosPagamento;
import aplicacao.dto.DadosPix;
import aplicacao.dto.DadosVenda;
import aplicacao.pagamento.FormaPagamento;
import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.modelo.Cliente;
import dominio.modelo.Pagamento;
import dominio.modelo.Veiculo;
import dominio.modelo.Venda;
import dominio.modelo.Vendedor;
import dominio.repositorio.ClienteRepository;
import dominio.repositorio.PagamentoRepository;
import dominio.repositorio.VeiculoRepository;
import dominio.repositorio.VendaRepository;
import dominio.repositorio.VendedorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class VendaService {
    private final ClienteRepository clienteRepository;
    private final VendedorRepository vendedorRepository;
    private final VeiculoRepository veiculoRepository;
    private final VendaRepository vendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final Map<Class<? extends DadosPagamento>, FormaPagamento<?, ?>> formasPagamento;
    private final Transacao transacao;

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

    public List<Vendedor> listarVendedores() {
        return vendedorRepository.listar();
    }

    /**
     * Realiza uma venda com qualquer forma de pagamento registrada no AppConfig.
     * A forma é escolhida pelo tipo do DTO de pagamento (DadosPix, DadosCartao...).
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

    /** @deprecated temporário: a tela de vendas passa a usar {@link #vender(DadosVenda)}. */
    @Deprecated
    public Pagamento venderComPix(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                  String chavePix, String tipoChave)
            throws VeiculoIndisponivelException, VendaInvalidaException {
        return vender(new DadosVenda(cliente.getId_cliente(), vendedor.getId_vendedor(),
                veiculo.getId_veiculo(), new DadosPix(chavePix, tipoChave)));
    }

    /** @deprecated temporário: a tela de vendas passa a usar {@link #vender(DadosVenda)}. */
    @Deprecated
    public Pagamento venderComCartao(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                     String numeroCartao, String titular, String validade,
                                     String cvv, int parcelas)
            throws VeiculoIndisponivelException, VendaInvalidaException {
        return vender(new DadosVenda(cliente.getId_cliente(), vendedor.getId_vendedor(),
                veiculo.getId_veiculo(), new DadosCartao(numeroCartao, titular, validade, cvv, parcelas)));
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
