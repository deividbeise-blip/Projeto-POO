package dominio.politica;

/** Regra de desconto substituível: novas políticas podem ser criadas sem alterar Pagamento. */
public interface PoliticaDesconto {
    /**
     * Aplica a regra ao preço e devolve o valor que o cliente paga.
     *
     * @param valorBase preço do veículo, em reais
     * @return valor final, em reais (igual ao base quando não há desconto)
     */
    double calcular(double valorBase);
}
