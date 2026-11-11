package dominio.modelo;

import dominio.politica.DescontoPix;
import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;

public class PagamentoPix extends Pagamento {
    private Long idPagamentoPix; // AUTO_INCREMENT
    private final String chavePix;
    private final String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    public PagamentoPix(Venda venda, double valorBase, LocalDate dataPagamento,
                        String chavePix, String tipoChave, PoliticaDesconto politicaDesconto) {
        super(venda, "PIX", valorBase, dataPagamento, politicaDesconto);
        if (chavePix == null || chavePix.isBlank()) {
            throw new IllegalArgumentException("Informe a chave PIX.");
        }
        if (tipoChave == null || tipoChave.isBlank()) {
            throw new IllegalArgumentException("Informe o tipo da chave PIX.");
        }
        this.chavePix = chavePix.trim();
        this.tipoChave = tipoChave;
    }

    public String getChavePix() { return chavePix; }
    public String getTipoChave() { return tipoChave; }

    public Long getIdPagamentoPix() {
        return idPagamentoPix;
    }

    public void setIdPagamentoPix(Long id) {
        if (this.idPagamentoPix != null) {
            throw new IllegalStateException("O ID do pagamento PIX já foi definido.");
        }
        this.idPagamentoPix = id;
    }

    @Override
    public String obterReciboDetalhado() {
        return "===== Recibo de Pagamento (PIX) =====\n" +
                "Chave PIX: " + chavePix + " (" + tipoChave + ")\n" +
                "Valor pago (com " + String.format("%.0f", DescontoPix.PERCENTUAL) + "% de desconto PIX): "
                + getValorPago() + "\n" +
                "Data do pagamento: " + getDataPagamento() + "\n" +
                "======================================";
    }
}
