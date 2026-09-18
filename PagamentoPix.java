import java.time.LocalDate;
public class PagamentoPix extends Pagamento {
    private String chavePix;
    private String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    public PagamentoPix(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento, String chavePix, String tipoChave) {
        super(venda, formaPagamento, valorPago, dataPagamento);
        this.chavePix = chavePix;
        this.tipoChave = tipoChave;
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
    
}
