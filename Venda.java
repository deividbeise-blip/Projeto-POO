import java.time.LocalDate;
import modelo.Cliente;

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

    // obtem a comissão do funcionário de acordo com o valor da venda
    public double getcomissao(){
        return vendedor.caucularcomissao(valorFinal);
    }

    // modifica a variável para um valor com desconto
    public void  desconto(double percentual){
        valorFinal = valorFinal -  valorFinal * percentual/100;
    }

    //validação da venda
    public boolean validarVenda(){
        return cliente != null && veiculo != null && vendedor != null;
    }
}

