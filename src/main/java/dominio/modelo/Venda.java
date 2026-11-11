package dominio.modelo;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.politica.ComissaoCarro;
import dominio.politica.ComissaoMoto;
import dominio.politica.PoliticaComissao;

import java.time.LocalDate;

public class Venda {
    private Long idVenda;
    private final Cliente cliente;
    private final Vendedor vendedor;
    private final Veiculo veiculo;
    private final LocalDate dataVenda;
    private double valorFinal;
    private Pagamento pagamento;

    /**
     * A venda nasce sem pagamento (o Pagamento exige uma Venda no construtor).
     * O pagamento é ligado depois, por registrarPagamento().
     */
    public Venda(Cliente cliente, Vendedor vendedor, Veiculo veiculo, LocalDate dataVenda) {
        if (cliente == null) throw new IllegalArgumentException("Selecione um cliente.");
        if (vendedor == null) throw new IllegalArgumentException("Selecione um vendedor.");
        if (veiculo == null) throw new IllegalArgumentException("Selecione um veículo.");
        if (dataVenda == null) throw new IllegalArgumentException("Data da venda é obrigatória.");

        this.cliente = cliente;
        this.vendedor = vendedor;
        this.veiculo = veiculo;
        this.dataVenda = dataVenda;
        this.valorFinal = veiculo.getPreco();
    }

    /** Liga o pagamento à venda e atualiza o valor final (já com o desconto da forma de pagamento). */
    public void registrarPagamento(Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException("Pagamento é obrigatório.");
        }
        if (pagamento.getVenda() != this) {
            throw new IllegalArgumentException("O pagamento pertence a outra venda.");
        }
        this.pagamento = pagamento;
        this.valorFinal = pagamento.getValorPago();
    }

    public Long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Long id) {
        if (this.idVenda != null) {
            throw new IllegalStateException("O ID da venda já foi definido.");
        }
        this.idVenda = id;
    }

    public Pagamento getPagamento() { return pagamento; }
    public Cliente getCliente() { return cliente; }
    public Vendedor getVendedor() { return vendedor; }
    public Veiculo getVeiculo() { return veiculo; }
    public LocalDate getDataVenda() { return dataVenda; }
    public double getValorFinal() { return valorFinal; }

    // comissão do vendedor de acordo com o valor final da venda
    public double calcularComissao() {
        return vendedor.calcularComissao(valorFinal, politicaComissao());
    }

    /** Recibo da venda: o recibo do pagamento + o vendedor e a sua comissão. */
    public String obterRecibo() {
        if (pagamento == null) {
            throw new IllegalStateException("A venda ainda não tem pagamento registrado.");
        }
        return pagamento.obterReciboDetalhado() + "\n" +
                "Vendedor: " + vendedor.getNome() + "\n" +
                "Comissão do vendedor: R$ " + String.format("%.2f", calcularComissao());
    }

    /** Único ponto que escolhe a regra de comissão pelo tipo do veículo. */
    private PoliticaComissao politicaComissao() {
        return veiculo.isMoto() ? new ComissaoMoto() : new ComissaoCarro();
    }

    public boolean validarVenda() {
        // cliente, vendedor, veículo e data já são garantidos pelo construtor;
        // o pagamento não, porque é ligado depois por registrarPagamento().
        return pagamento != null
                && valorFinal > 0
                && vendedor.validarVendedor()
                && pagamento.validarPagamento();
    }

    /**
     * Regra de negócio: só finaliza se a venda estiver completa e o veículo puder ser vendido.
     * Falhas lançam exceção ANTES de qualquer alteração de estado.
     */
    public void finalizarVenda() throws VeiculoIndisponivelException, VendaInvalidaException {
        if (!validarVenda()) {
            throw new VendaInvalidaException("Venda incompleta: verifique cliente, vendedor, veículo e pagamento.");
        }
        veiculo.vender(); // lança VeiculoIndisponivelException se vendido ou em manutenção
    }
}
