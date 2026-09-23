import java.time.LocalDate;

public class Pagamento {
    private Long id;
    private Venda venda;
    private String formaPagamento;
    private Double valorPago;
    private LocalDate dataPagamento;

    public Pagamento(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento) {
        this.venda = venda;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
    }

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public Double getValorPago() {
        return valorPago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public boolean validarPagamento() {
        return valorPago>0;
    }
    public boolean validarFormaPagamento() {
        return formaPagamento.equalsIgnoreCase("pix") || formaPagamento.equalsIgnoreCase("cartão");
    }
    public void aplicarDesconto(Double desconto) {
        if (desconto > 0 && desconto <= 100) {
            valorPago -= valorPago * (desconto / 100);
        }
    }
    public void exibirResumo() {
        System.out.println("Resumo do Pagamento:");
        System.out.println("ID: " + id);
        System.out.println("Venda: " + venda);
        System.out.println("Forma de Pagamento: " + formaPagamento);
        System.out.println("Valor Pago: " + valorPago);
        System.out.println("Data do Pagamento: " + dataPagamento);
    }
}