import java.time.LocalDate;
import modelo.Cliente;
import modelo.Veiculo;
import excecao.*;

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
    // obtem o id da venda
    public Long getId() {
        return id;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Vendedor getVendedor() {
        return vendedor;
    }
    public Veiculo getVeiculo() {
        return veiculo;
    }
    public LocalDate getDataVenda() {
        return dataVenda;
    }
    public double getValorFinal() {
        return valorFinal;
    }

    // obtem a comissão do funcionário de acordo com o valor da venda
    public double getcomissao(){
        return vendedor.caucularcomissao(valorFinal, veiculo);
    }

    // modifica a variável para um valor com desconto
    public void  desconto(double percentual){
        valorFinal = valorFinal -  valorFinal * percentual/100;
    }

    //validação da venda
    public boolean validarVenda(){
        return cliente != null
                && veiculo != null
                && vendedor != null
                && dataVenda != null
                && valorFinal > 0
                && veiculo.verificarDisponibilidade()
                && vendedor.validarVendedor();
    }

    // venda de veiculo
    public void finalizarVenda () throws VeiculoIndisponivelException {
        veiculo.vender();
        validarVenda();
    }
}

