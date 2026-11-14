package dominio.modelo;

import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;

/**
 * Pagamento em dinheiro, à vista. Não tem dados próprios além da parte comum de {@link Pagamento};
 * o desconto (normalmente nenhum) vem da política recebida.
 */
public class PagamentoDinheiro extends Pagamento {

    /**
     * @param venda venda que está sendo paga
     * @param valorBase preço do veículo, antes da política de desconto
     * @param dataPagamento dia do pagamento
     * @param politicaDesconto regra que define o valor final
     * @throws IllegalArgumentException se algum dado da parte comum for inválido
     */
    public PagamentoDinheiro(Venda venda, double valorBase, LocalDate dataPagamento,
                             PoliticaDesconto politicaDesconto) {
        super(venda, "Dinheiro", valorBase, dataPagamento, politicaDesconto);
    }

    @Override
    public String obterReciboDetalhado() {
        return "===== Recibo de Pagamento (Dinheiro) =====\n" +
                "Pagamento à vista em dinheiro\n" +
                "Valor pago: " + String.format("%.2f", getValorPago()) + "\n" +
                "Data do pagamento: " + getDataPagamento() + "\n" +
                "===========================================";
    }
}
