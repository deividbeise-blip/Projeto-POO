package dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados de uma venda concluída para exibição no histórico (somente leitura).
 *
 * @param data dia da venda
 * @param cliente nome do cliente
 * @param veiculo marca e modelo do veículo
 * @param vendedor nome do vendedor
 * @param formaPagamento forma já no formato de exibição ("PIX", "Cartão", "Dinheiro")
 * @param valor valor efetivamente pago, em reais
 */
public record ResumoVenda(LocalDate data, String cliente, String veiculo, String vendedor,
                          String formaPagamento, BigDecimal valor) { }
