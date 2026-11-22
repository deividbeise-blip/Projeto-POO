package dominio.politica;

/** Política neutra: o cliente paga o preço cheio (cartão e dinheiro). */
public class SemDesconto implements PoliticaDesconto {
    /**
     * @param valorBase preço do veículo, em reais
     * @return o mesmo valor, sem alteração
     */
    @Override
    public double calcular(double valorBase) {
        return valorBase;
    }
}
