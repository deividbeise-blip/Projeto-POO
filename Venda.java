import java.time.LocalDate;

public class Venda {
    private Long id;
    private Cliente cliente;
    private Vendedor vendedor;
    private Veiculo veiculo;
    private LocalDate dataVenda;
    private Double valorFinal;

    public Venda(Cliente cliente, Vendedor vendedor, Veiculo veiculo, LocalDate dataVenda, Double valorFinal) {
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.veiculo = veiculo;
        this.dataVenda = dataVenda;
        this.valorFinal = valorFinal;
    }
}
