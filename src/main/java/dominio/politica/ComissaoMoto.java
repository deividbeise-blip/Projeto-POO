package dominio.politica;

/** Moto: o percentual do vendedor é reduzido em alguns pontos (nunca abaixo de zero). */
public class ComissaoMoto implements PoliticaComissao {
    private static final double REDUCAO_PERCENTUAL = 3;

    @Override
    public double calcular(double valorVenda, double percentualVendedor) {
        double percentual = Math.max(0, percentualVendedor - REDUCAO_PERCENTUAL);
        return valorVenda * percentual / 100;
    }
}
