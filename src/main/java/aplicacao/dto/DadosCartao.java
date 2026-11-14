package aplicacao.dto;

/**
 * Dados digitados para pagar com cartão. Chegam crus da tela;
 * quem valida é o construtor de {@code PagamentoCartao}.
 *
 * @param numeroCartao número do cartão, 16 dígitos
 * @param titular nome impresso no cartão
 * @param validade mês e ano de validade no formato MM/AA
 * @param cvv código de segurança, 3 ou 4 dígitos
 * @param parcelas quantidade de parcelas (1 a 12)
 */
public record DadosCartao(String numeroCartao, String titular, String validade,
                          String cvv, int parcelas) implements DadosPagamento {
}
