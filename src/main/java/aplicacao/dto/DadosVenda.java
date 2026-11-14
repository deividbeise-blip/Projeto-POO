package aplicacao.dto;

/** Dados de entrada de uma venda: só IDs e os dados do pagamento escolhido. */
public record DadosVenda(Long idCliente, Long idVendedor, Long idVeiculo, DadosPagamento pagamento) {
}
