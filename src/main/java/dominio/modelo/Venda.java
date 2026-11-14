package dominio.modelo;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.politica.ComissaoCarro;
import dominio.politica.ComissaoMoto;
import dominio.politica.PoliticaComissao;

import java.time.LocalDate;

/**
 * Venda de um veículo a um cliente, feita por um vendedor.
 * Concentra as regras da venda: valor final (com o desconto do pagamento),
 * comissão pelo tipo de veículo, recibo e validação antes de finalizar.
 */
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
     *
     * @param cliente quem compra
     * @param vendedor quem vende
     * @param veiculo o que é vendido; o preço dele é o valor final inicial
     * @param dataVenda dia da venda
     * @throws IllegalArgumentException se algum dos dados for nulo
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

    /**
     * Liga o pagamento à venda e atualiza o valor final (já com o desconto da forma de pagamento).
     *
     * @param pagamento pagamento criado para esta mesma venda
     * @throws IllegalArgumentException se o pagamento for nulo ou pertencer a outra venda
     */
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

    /** @return ID gerado pelo banco, ou null se ainda não foi gravada */
    public Long getIdVenda() {
        return idVenda;
    }

    /**
     * Define o ID gerado pelo banco; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdVenda(Long id) {
        if (this.idVenda != null) {
            throw new IllegalStateException("O ID da venda já foi definido.");
        }
        this.idVenda = id;
    }

    /** @return o pagamento registrado, ou null antes de {@link #registrarPagamento(Pagamento)} */
    public Pagamento getPagamento() { return pagamento; }
    /** @return quem compra; nunca null */
    public Cliente getCliente() { return cliente; }
    /** @return quem vende; nunca null */
    public Vendedor getVendedor() { return vendedor; }
    /** @return o veículo vendido; nunca null */
    public Veiculo getVeiculo() { return veiculo; }
    /** @return o dia da venda */
    public LocalDate getDataVenda() { return dataVenda; }
    /** @return valor em reais: o preço do veículo até o pagamento ser registrado, depois o valor com desconto */
    public double getValorFinal() { return valorFinal; }

    /**
     * Comissão do vendedor sobre o valor final, com a regra do tipo de veículo (carro ou moto).
     *
     * @return valor da comissão, em reais
     */
    public double calcularComissao() {
        return vendedor.calcularComissao(valorFinal, politicaComissao());
    }

    /**
     * Recibo da venda: o recibo do pagamento + o vendedor e a sua comissão.
     *
     * @return texto do recibo, em várias linhas
     * @throws IllegalStateException se ainda não houver pagamento registrado
     */
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

    /** @return true se há pagamento, valor final positivo e vendedor e pagamento válidos */
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
     *
     * @throws VendaInvalidaException se a venda estiver incompleta (ver {@link #validarVenda()})
     * @throws VeiculoIndisponivelException se o veículo já foi vendido ou está em manutenção
     */
    public void finalizarVenda() throws VeiculoIndisponivelException, VendaInvalidaException {
        if (!validarVenda()) {
            throw new VendaInvalidaException("Venda incompleta: verifique cliente, vendedor, veículo e pagamento.");
        }
        veiculo.vender(); // lança VeiculoIndisponivelException se vendido ou em manutenção
    }
}
