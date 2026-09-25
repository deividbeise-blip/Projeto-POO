package modelo;
import java.time.LocalDate;

public class PagamentoCartao extends Pagamento {
    private String numeroCartao;
    private String nomeTitular;
    private String validade;
    private String cvv;
    private int numeroParcelas;
    private Double valorParcela;

    public PagamentoCartao(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento,
                           String numeroCartao, String nomeTitular, String validade, String cvv) {
        super(venda, formaPagamento, valorPago, dataPagamento);
        this.numeroCartao = numeroCartao;
        this.nomeTitular = nomeTitular;
        this.validade = validade;
        this.cvv = cvv;
    }
    // Foi criado o set somente para o atributo validade, pois é o único que pode ser alterado após a criação do objeto.
    // Os outros atributos são considerados imutáveis e não possuem métodos set. 
    public String getNumeroCartao() {
        return numeroCartao;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public String getValidade() {
        return validade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }
    public int getnumeroParcelas() {
        return numeroParcelas;
    }
    public void setnumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }
    public Double getValorParcela() {
        return valorParcela;
    }
    public void setValorParcela(Double valorParcela) {
        this.valorParcela = valorParcela;
    }
    public boolean validarNumeroCartao() {
        return numeroCartao.matches("\\d{16}");
    }
    public Double calcularParcelas(int numeroParcelas) {
        if (numeroParcelas < 1 || numeroParcelas > 12) {
            throw new IllegalArgumentException("Número de parcelas inválido. Deve ser entre 1 e 12.");
        }
        this.numeroParcelas = numeroParcelas;
        this.valorParcela = getValorPago() / numeroParcelas;
        return (getValorPago() / numeroParcelas);
    }

    @Override
    public String obterReciboDetalhado() {
        String numeroMascarado = numeroCartao != null && numeroCartao.length() >= 4
                ? "**** **** **** " + numeroCartao.substring(numeroCartao.length() - 4)
                : numeroCartao;

        StringBuilder recibo = new StringBuilder();
        recibo.append("===== Recibo de Pagamento (Cartão) =====\n");
        recibo.append("Titular: ").append(nomeTitular).append("\n");
        recibo.append("Cartão: ").append(numeroMascarado).append("\n");
        recibo.append("Valor pago: ").append(getValorPago()).append("\n");
        if (numeroParcelas > 0) {
            recibo.append("Parcelado em: ").append(numeroParcelas).append("x de ").append(valorParcela).append("\n");
        } else {
            recibo.append("Pagamento à vista\n");
        }
        recibo.append("Data do pagamento: ").append(getDataPagamento()).append("\n");
        recibo.append("=========================================");
        return recibo.toString();
    }
}
