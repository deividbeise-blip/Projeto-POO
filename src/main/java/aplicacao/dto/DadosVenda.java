package aplicacao.dto;

/**
 * Dados de entrada de uma venda: só IDs e os dados do pagamento escolhido.
 * O tipo concreto de {@code pagamento} decide qual forma de pagamento será usada.
 *
 * @param idCliente ID do cliente comprador
 * @param idVendedor ID do vendedor responsável
 * @param idVeiculo ID do veículo vendido
 * @param pagamento dados da forma escolhida ({@link DadosPix}, {@link DadosCartao}, {@link DadosDinheiro}...)
 */
public record DadosVenda(Long idCliente, Long idVendedor, Long idVeiculo, DadosPagamento pagamento) {
}
