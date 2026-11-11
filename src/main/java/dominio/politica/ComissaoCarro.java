package dominio.politica;

/** Carro: o vendedor recebe o percentual cheio. */
public class ComissaoCarro implements PoliticaComissao {
    @Override
    public double calcular(double valorVenda, double percentualVendedor) {
        return valorVenda * percentualVendedor / 100;
    }
}
