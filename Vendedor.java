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

    //caucula o valor da comissão de acordo com o valor da venda
    public double caucularcomissao(double valorVenda){
        return valorVenda * comissaoPercentual/100;
    }
}

