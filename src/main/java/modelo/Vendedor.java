package modelo;
import modelo.Veiculo;
public class Vendedor {
    private Long id;
    private String name;
    private String cpf;
    private Double comissaoPercentual;

    public Vendedor(String name, String cpf, Double comissaoPercentual) {
        this.name = name;
        this.cpf = cpf;
        this.comissaoPercentual = comissaoPercentual;
    }

    // pega o id do vendedor
    public Long getId() {
        return id;
    }
    // pega o nome do vendedor
    public String getName() {
        return name;
    }
    // pega o cpf do vendedor
    public String getCpf() {
        return cpf;
    }
    // pega o valor da comissao em porcentagem
    public Double getComissaoPercentual() {
        return comissaoPercentual;
    }

    // modifica o valor da variavel nome
    public void setName(String name) {
        this.name = name;
    }
    // modifica o valor da variavel cpf
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    // muda o valor da variavel comissaoPercentual
    public void setComissaoPercentual(Double comissaoPercentual) {
        this.comissaoPercentual = comissaoPercentual;
    }

    //caucula o valor da comissão de acordo com o valor da venda
    public double caucularcomissao(double valorVenda, Veiculo veiculo){

        double percentual = comissaoPercentual;
        double reducaoComissaoMoto = 3;

        if (veiculo.getMoto() == false) {
            return valorVenda * percentual/100;
        }
        else {
            percentual = percentual - reducaoComissaoMoto;
            return valorVenda * percentual/100;
        }

    }

    public boolean validarVendedor() {
        return name != null
                && cpf != null
                && comissaoPercentual > 0;
    }
}

