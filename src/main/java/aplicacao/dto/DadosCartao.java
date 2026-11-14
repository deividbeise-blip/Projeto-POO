package aplicacao.dto;

public record DadosCartao(String numeroCartao, String titular, String validade,
                          String cvv, int parcelas) implements DadosPagamento {
}
