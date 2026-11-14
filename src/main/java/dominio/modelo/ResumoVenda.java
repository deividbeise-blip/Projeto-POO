package dominio.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Dados de uma venda concluída para exibição no histórico. */
public record ResumoVenda(LocalDate data, String cliente, String veiculo, String vendedor,
                          String formaPagamento, BigDecimal valor) { }
