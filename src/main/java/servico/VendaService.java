package servico;

import dao.ClienteDAO;
import dao.PagamentoCartaoDAO;
import dao.PagamentoDAO;
import dao.PagamentoPixDAO;
import dao.VeiculoDAO;
import dao.VendaDAO;
import excecao.VeiculoIndisponivelException;
import excecao.VendaInvalidaException;
import modelo.*;

import java.time.LocalDate;

public class VendaService {
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VendaDAO vendaDAO = new VendaDAO();
    private final PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private final PagamentoPixDAO pagamentoPixDAO = new PagamentoPixDAO();
    private final PagamentoCartaoDAO pagamentoCartaoDAO = new PagamentoCartaoDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    public Pagamento venderComPix(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                  String chavePix, String tipoChave)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        Venda venda = new Venda(cliente, vendedor, veiculo, LocalDate.now());
        PagamentoPix pagamento = new PagamentoPix(venda, veiculo.getPreco(), LocalDate.now(), chavePix, tipoChave);
        return concluir(venda, pagamento, () -> pagamentoPixDAO.salvar(pagamento));
    }

    public Pagamento venderComCartao(Cliente cliente, Vendedor vendedor, Veiculo veiculo,
                                     String numeroCartao, String titular, String validade,
                                     String cvv, int parcelas)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        Venda venda = new Venda(cliente, vendedor, veiculo, LocalDate.now());
        PagamentoCartao pagamento = new PagamentoCartao(venda, veiculo.getPreco(), LocalDate.now(),
                numeroCartao, titular, validade, cvv, parcelas);
        return concluir(venda, pagamento, () -> pagamentoCartaoDAO.salvar(pagamento));
    }

    private Pagamento concluir(Venda venda, Pagamento pagamento, Runnable salvarDetalhePagamento)
            throws VeiculoIndisponivelException, VendaInvalidaException {

        venda.registrarPagamento(pagamento);

        // 1) Regras de negócio primeiro: se falhar aqui, NADA foi gravado no banco.
        venda.finalizarVenda();

        // 2) Só então persiste, na ordem que as chaves estrangeiras exigem.
        try {
            if (venda.getCliente().getId_cliente() == null) {
                clienteDAO.salvar(venda.getCliente());
            }
            vendaDAO.salvar(venda);
            pagamentoDAO.salvar(pagamento);
            salvarDetalhePagamento.run();
            vendaDAO.atualizarPagamento(venda);
            veiculoDAO.atualizarStatus(venda.getVeiculo());
        } catch (RuntimeException e) {
            venda.getVeiculo().cancelarVenda(); // desfaz o estado em memória
            throw e;
        }
        return pagamento;
    }
}
