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
        numeroParcelas = numeroParcelas;
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
}