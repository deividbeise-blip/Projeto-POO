package dominio.politica;

/** Regra de comissão substituível: novos tipos de veículo ganham uma política nova sem alterar o Vendedor. */
public interface PoliticaComissao {
    /**
     * Calcula quanto o vendedor recebe por uma venda.
     *
     * @param valorVenda valor final da venda, já com desconto, em reais
     * @param percentualVendedor percentual cadastrado do vendedor (ex.: 5 para 5%)
     * @return valor da comissão em reais
     */
    double calcular(double valorVenda, double percentualVendedor);
}
