package modelo;

/** Regra de desconto substituível: novas políticas podem ser criadas sem alterar Pagamento. */
public interface PoliticaDesconto {
    double calcular(double valorBase);
}
