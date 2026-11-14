package aplicacao.dto;

/**
 * Dados de entrada do pagamento em dinheiro.
 * Não tem campos: o valor sai do preço do veículo e não há detalhe a informar.
 */
public record DadosDinheiro() implements DadosPagamento {
}
