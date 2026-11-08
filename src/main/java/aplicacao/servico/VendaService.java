package aplicacao.servico;

import aplicacao.Transacao;
import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.modelo.*;
import dominio.repositorio.ClienteRepository;
import dominio.repositorio.DetalhePagamentoRepository;
import dominio.repositorio.PagamentoRepository;
import dominio.repositorio.VeiculoRepository;
import dominio.repositorio.VendaRepository;

import java.time.LocalDate;

public class VendaService {
    private final ClienteRepository clienteRepository;
    private final VendaRepository vendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final DetalhePagamentoRepository<PagamentoPix> pagamentoPixRepository;
    private final DetalhePagamentoRepository<PagamentoCartao> pagamentoCartaoRepository;
    private final VeiculoRepository veiculoRepository;
    private final Transacao transacao;

    public VendaService(ClienteRepository clienteRepository,
                        VendaRepository vendaRepository,
                        PagamentoRepository pagamentoRepository,
                        DetalhePagamentoRepository<PagamentoPix> pagamentoPixRepository,
                        DetalhePagamentoRepository<PagamentoCartao> pagamentoCartaoRepository,
                        VeiculoRepository veiculoRepository,
                        Transacao transacao) {
        this.clienteRepository = clienteRepository;
        this.vendaRepository = vendaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoPixRepository = pagamentoPixRepository;
        this.pagamentoCartaoRepository = pagamentoCartaoRepository;
        this.veiculoRepository = veiculoRepository;
        this.transacao = transacao;
    }

    public Pagamento venderComPix(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                  String chavePix, String tipoChave)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        Venda venda = new Venda(cliente, vendedor, veiculo, LocalDate.now());
        PagamentoPix pagamento = new PagamentoPix(venda, veiculo.getPreco(), LocalDate.now(), chavePix, tipoChave);
        return concluir(venda, pagamento, () -> pagamentoPixRepository.salvar(pagamento));
    }

    public Pagamento venderComCartao(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                     String numeroCartao, String titular, String validade,
                                     String cvv, int parcelas)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        Venda venda = new Venda(cliente, vendedor, veiculo, LocalDate.now());
        PagamentoCartao pagamento = new PagamentoCartao(venda, veiculo.getPreco(), LocalDate.now(),
                numeroCartao, titular, validade, cvv, parcelas);
        return concluir(venda, pagamento, () -> pagamentoCartaoRepository.salvar(pagamento));
    }

    private Pagamento concluir(Venda venda, Pagamento pagamento, Runnable salvarDetalhePagamento)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        venda.registrarPagamento(pagamento);

        // 1) Regras de negócio primeiro: se falhar aqui, NADA foi gravado no banco.
        venda.finalizarVenda();

        // 2) Só então persiste, tudo na mesma transação: se qualquer passo falhar, nada fica gravado.
        try {
            transacao.executar(() -> {
                if (venda.getCliente().getId_cliente() == null) {
                    clienteRepository.salvar(venda.getCliente());
                }
                vendaRepository.salvar(venda);
                pagamentoRepository.salvar(pagamento);
                salvarDetalhePagamento.run();
                vendaRepository.atualizarPagamento(venda);
                veiculoRepository.atualizarStatus(venda.getVeiculo());
            });
        } catch (RuntimeException e) {
            venda.getVeiculo().cancelarVenda(); // desfaz o estado em memória
            throw e;
        }
        return pagamento;
    }
}
