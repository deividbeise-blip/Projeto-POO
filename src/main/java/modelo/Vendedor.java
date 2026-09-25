package modelo;
import dao.VendedorDAO;
public class Vendedor {
    private Long id_vendedor;
    private String name;
    private String cpf;
    private Double comissaoPercentual;
    private Concessionaria concessionaria;

    public Vendedor(String name, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this.name = name;
        this.cpf = cpf;
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
        try {
            VendedorDAO vendedorDAO = new VendedorDAO();
            vendedorDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar vendedor no banco. Banco deve estar offline", e);
        }
    }
    
    // pega o id do vendedor
    public Long getId_vendedor() {
        return id_vendedor;
    }
    public void setId_vendedor(Long id_vendedor) {
        this.id_vendedor = id_vendedor;
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

        if (veiculo.isMoto() == false) {
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

    public Concessionaria getConcessionaria() {
        return concessionaria;
    }

    public void setConcessionaria(Concessionaria concessionaria) {
        this.concessionaria = concessionaria;
    }
}

