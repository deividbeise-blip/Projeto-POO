package dominio.politica;

/** Regra de comissão substituível: novos tipos de veículo ganham uma política nova sem alterar o Vendedor. */
public interface PoliticaComissao {
    double calcular(double valorVenda, double percentualVendedor);
}
