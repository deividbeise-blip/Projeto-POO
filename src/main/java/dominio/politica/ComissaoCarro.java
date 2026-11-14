package dominio.politica;

/** Carro: o vendedor recebe o percentual cheio. */
public class ComissaoCarro implements PoliticaComissao {
    /**
     * @param valorVenda valor final da venda, em reais
     * @param percentualVendedor percentual do vendedor, aplicado sem redução
     * @return {@code valorVenda * percentualVendedor / 100}
     */
    @Override
    public double calcular(double valorVenda, double percentualVendedor) {
        return valorVenda * percentualVendedor / 100;
    }
}
