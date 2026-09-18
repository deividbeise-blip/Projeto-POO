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
}
