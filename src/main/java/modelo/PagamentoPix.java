package modelo;
import java.time.LocalDate;

public class PagamentoPix extends Pagamento {
    private String chavePix;
    private String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    public PagamentoPix(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento, String chavePix, String tipoChave) {
        super(venda, formaPagamento, valorPago, dataPagamento);
        this.chavePix = chavePix;
        this.tipoChave = tipoChave;

        // Regra automática: todo pagamento via PIX recebe 5% de desconto assim que é criado.
        aplicarDesconto(5.0);
    }

    public String getChavePix() {
        return chavePix;
    }
    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public String getTipoChave() {
        return tipoChave;
    }

    public void setTipoChave(String tipoChave) {
        this.tipoChave = tipoChave;
    }

    @Override
    public String obterReciboDetalhado() {
        return "===== Recibo de Pagamento (PIX) =====\n" +
                "Chave PIX: " + chavePix + " (" + tipoChave + ")\n" +
                "Valor pago (com 5% de desconto já aplicado): " + getValorPago() + "\n" +
                "Data do pagamento: " + getDataPagamento() + "\n" +
                "======================================";
    }
}
