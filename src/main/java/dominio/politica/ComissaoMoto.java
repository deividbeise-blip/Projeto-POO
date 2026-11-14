package dominio.politica;

/** Moto: o percentual do vendedor é reduzido em alguns pontos (nunca abaixo de zero). */
public class ComissaoMoto implements PoliticaComissao {
    private static final double REDUCAO_PERCENTUAL = 3;

    /**
     * @param valorVenda valor final da venda, em reais
     * @param percentualVendedor percentual do vendedor; são descontados 3 pontos
     * @return a comissão com o percentual reduzido (zero se o percentual ficar negativo)
     */
    @Override
    public double calcular(double valorVenda, double percentualVendedor) {
        double percentual = Math.max(0, percentualVendedor - REDUCAO_PERCENTUAL);
        return valorVenda * percentual / 100;
    }
}
