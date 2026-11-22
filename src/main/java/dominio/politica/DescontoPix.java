package dominio.politica;

/** Pagamento via PIX: o cliente paga o preço com {@link #PERCENTUAL}% de desconto. */
public class DescontoPix implements PoliticaDesconto {
    /** Percentual de desconto do PIX; também é usado no recibo e no resumo da tela. */
    public static final double PERCENTUAL = 5.0;

    /**
     * @param valorBase preço do veículo, em reais
     * @return o preço menos {@link #PERCENTUAL}%
     */
    @Override
    public double calcular(double valorBase) {
        return valorBase - valorBase * PERCENTUAL / 100;
    }
}
