package modelo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PagamentoCartao extends Pagamento {
    private static final DateTimeFormatter FORMATO_VALIDADE = DateTimeFormatter.ofPattern("MM/yy");

    private Long id_PagamentoCartao;
    private final String numeroCartao;
    private final String nomeTitular;
    private final String validade;
    private final String cvv;
    private final int numeroParcelas;
    private final Double valorParcela;

    public PagamentoCartao(Venda venda, double valorBase, LocalDate dataPagamento,
                           String numeroCartao, String nomeTitular, String validade,
                           String cvv, int numeroParcelas) {
        super(venda, "Cartão", valorBase, dataPagamento, new SemDesconto());

        if (numeroCartao == null || !numeroCartao.matches("\\d{16}")) {
            throw new IllegalArgumentException("Número de cartão inválido. Deve conter 16 dígitos.");
        }
        if (nomeTitular == null || nomeTitular.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do titular.");
        }
        validarValidade(validade);
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV inválido. Deve conter 3 ou 4 dígitos.");
        }
        if (numeroParcelas < 1 || numeroParcelas > 12) {
            throw new IllegalArgumentException("Número de parcelas inválido. Deve ser entre 1 e 12.");
        }

        this.numeroCartao = numeroCartao;
        this.nomeTitular = nomeTitular.trim();
        this.validade = validade;
        this.cvv = cvv;
        this.numeroParcelas = numeroParcelas;
        this.valorParcela = getValorPago() / numeroParcelas;
    }

    private static void validarValidade(String validade) {
        try {
            YearMonth vencimento = YearMonth.parse(validade == null ? "" : validade.trim(), FORMATO_VALIDADE);
            if (vencimento.isBefore(YearMonth.now())) {
                throw new IllegalArgumentException("Cartão vencido.");
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Validade inválida. Use o formato MM/AA.");
        }
    }

    public Long getId_PagamentoCartao() {
        return id_PagamentoCartao;
    }

    public void setId_PagamentoCartao(Long id) {
        if (this.id_PagamentoCartao != null) {
            throw new IllegalStateException("O ID do pagamento no cartão já foi definido.");
        }
        this.id_PagamentoCartao = id;
    }

    public String getNumeroCartao() { return numeroCartao; }
    public String getNomeTitular() { return nomeTitular; }
    public String getValidade() { return validade; }
    public String getCvv() { return cvv; }
    public int getNumeroParcelas() { return numeroParcelas; }
    public Double getValorParcela() { return valorParcela; }

    @Override
    public String obterReciboDetalhado() {
        String numeroMascarado = "**** **** **** " + numeroCartao.substring(numeroCartao.length() - 4);

        StringBuilder recibo = new StringBuilder();
        recibo.append("===== Recibo de Pagamento (Cartão) =====\n");
        recibo.append("Titular: ").append(nomeTitular).append("\n");
        recibo.append("Cartão: ").append(numeroMascarado).append("\n");
        recibo.append("Valor pago: ").append(getValorPago()).append("\n");
        if (numeroParcelas > 1) {
            recibo.append("Parcelado em: ").append(numeroParcelas).append("x de ")
                    .append(String.format("%.2f", valorParcela)).append("\n");
        } else {
            recibo.append("Pagamento à vista\n");
        }
        recibo.append("Data do pagamento: ").append(getDataPagamento()).append("\n");
        recibo.append("=========================================");
        return recibo.toString();
    }
}
