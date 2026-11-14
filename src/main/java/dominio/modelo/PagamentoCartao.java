package dominio.modelo;

import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Pagamento com cartão de crédito, à vista ou parcelado.
 * Valida número, titular, validade, CVV e parcelas na criação.
 */
public class PagamentoCartao extends Pagamento {
    /** Número máximo de parcelas aceito. */
    public static final int MAX_PARCELAS = 12;
    /** Quantidade exata de dígitos do número do cartão. */
    public static final int DIGITOS_CARTAO = 16;

    /** Formato da validade do cartão ("MM/aa", ex: "12/30"). Usado também pela persistência. */
    public static final DateTimeFormatter FORMATO_VALIDADE = DateTimeFormatter.ofPattern("MM/yy");

    private Long idPagamentoCartao;
    private final String numeroCartao;
    private final String nomeTitular;
    private final String validade;
    private final String cvv;
    private final int numeroParcelas;
    private final Double valorParcela;

    /**
     * @param venda venda que está sendo paga
     * @param valorBase preço do veículo, antes da política de desconto
     * @param dataPagamento dia do pagamento
     * @param numeroCartao número com exatamente {@link #DIGITOS_CARTAO} dígitos, sem espaços
     * @param nomeTitular nome impresso no cartão
     * @param validade mês/ano no formato MM/AA; não pode estar vencido
     * @param cvv código de segurança com 3 ou 4 dígitos
     * @param numeroParcelas de 1 a {@link #MAX_PARCELAS}
     * @param politicaDesconto regra de desconto (o AppConfig entrega SemDesconto)
     * @throws IllegalArgumentException se algum dado do cartão for inválido ou o cartão estiver vencido
     */
    public PagamentoCartao(Venda venda, double valorBase, LocalDate dataPagamento,
                           String numeroCartao, String nomeTitular, String validade,
                           String cvv, int numeroParcelas, PoliticaDesconto politicaDesconto) {
        super(venda, "Cartão", valorBase, dataPagamento, politicaDesconto);

        if (numeroCartao == null || !numeroCartao.matches("\\d{" + DIGITOS_CARTAO + "}")) {
            throw new IllegalArgumentException(
                    "Número de cartão inválido. Deve conter " + DIGITOS_CARTAO + " dígitos.");
        }
        if (nomeTitular == null || nomeTitular.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do titular.");
        }
        validarValidade(validade);
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV inválido. Deve conter 3 ou 4 dígitos.");
        }
        if (numeroParcelas < 1 || numeroParcelas > MAX_PARCELAS) {
            throw new IllegalArgumentException(
                    "Número de parcelas inválido. Deve ser entre 1 e " + MAX_PARCELAS + ".");
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

    /** @return ID do registro na tabela pagamentocartao, ou null se ainda não foi gravado */
    public Long getIdPagamentoCartao() {
        return idPagamentoCartao;
    }

    /**
     * Define o ID do detalhe do cartão; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdPagamentoCartao(Long id) {
        if (this.idPagamentoCartao != null) {
            throw new IllegalStateException("O ID do pagamento no cartão já foi definido.");
        }
        this.idPagamentoCartao = id;
    }

    /** @return o número completo, só dígitos (o recibo mostra mascarado) */
    public String getNumeroCartao() { return numeroCartao; }
    /** @return o nome do titular, sem espaços nas pontas */
    public String getNomeTitular() { return nomeTitular; }
    /** @return a validade no formato MM/AA (ver {@link #FORMATO_VALIDADE}) */
    public String getValidade() { return validade; }
    /** @return o código de segurança, 3 ou 4 dígitos */
    public String getCvv() { return cvv; }
    /** @return número de parcelas, de 1 a {@link #MAX_PARCELAS} */
    public int getNumeroParcelas() { return numeroParcelas; }
    /** @return valor de cada parcela em reais (valor pago dividido pelas parcelas) */
    public Double getValorParcela() { return valorParcela; }

    /** @return recibo com titular, número mascarado, valor, parcelas e data */
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
