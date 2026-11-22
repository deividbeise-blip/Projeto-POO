package aplicacao.dto;

/**
 * Dados digitados para pagar com PIX. A validação fica no construtor de {@code PagamentoPix}.
 *
 * @param chavePix a chave PIX do pagador
 * @param tipoChave tipo da chave: CPF, CNPJ, Email ou Telefone
 */
public record DadosPix(String chavePix, String tipoChave) implements DadosPagamento {
}
