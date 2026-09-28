package modelo;

import java.time.LocalDate;

public class PagamentoPix extends Pagamento {
    private Long id_pagamentoPix; // AUTO_INCREMENT
    private final String chavePix;
    private final String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    public PagamentoPix(Venda venda, double valorBase, LocalDate dataPagamento,
                        String chavePix, String tipoChave) {
        super(venda, "PIX", valorBase, dataPagamento, new DescontoPix());
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

    public Long getId_pagamentoPix() {
        return id_pagamentoPix;
    }

    public void setId_pagamentoPix(Long id) {
        if (this.id_pagamentoPix != null) {
            throw new IllegalStateException("O ID do pagamento PIX já foi definido.");
        }
        this.id_pagamentoPix = id;
    }

    @Override
    public String obterReciboDetalhado() {
        return "===== Recibo de Pagamento (PIX) =====\n" +
                "Chave PIX: " + chavePix + " (" + tipoChave + ")\n" +
                "Valor pago (com 5% de desconto PIX): " + getValorPago() + "\n" +
                "Data do pagamento: " + getDataPagamento() + "\n" +
                "======================================";
    }
}
