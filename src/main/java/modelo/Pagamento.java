package modelo;

import java.time.LocalDate;

public abstract class Pagamento {
    private Long id_pagamento; // AUTO_INCREMENT
    private final Venda venda;
    private final String formaPagamento;
    private final Double valorPago;
    private final LocalDate dataPagamento;

    /**
     * O valor pago já sai calculado pela PoliticaDesconto recebida,
     * então o objeto nasce com o valor final correto (antes de qualquer gravação).
     */
    protected Pagamento(Venda venda, String formaPagamento, double valorBase,
                        LocalDate dataPagamento, PoliticaDesconto politica) {
        if (venda == null) {
            throw new IllegalArgumentException("Pagamento exige uma venda.");
        }
        if (formaPagamento == null || formaPagamento.isBlank()) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória.");
        }
        if (valorBase <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero.");
        }
        if (dataPagamento == null) {
            throw new IllegalArgumentException("Data do pagamento é obrigatória.");
        }
        if (politica == null) {
            throw new IllegalArgumentException("Política de desconto é obrigatória.");
        }
        this.venda = venda;
        this.formaPagamento = formaPagamento;
        this.dataPagamento = dataPagamento;
        this.valorPago = politica.calcular(valorBase);
    }

    public Long getId_pagamento() {
        return id_pagamento;
    }

    public void setId_pagamento(Long id) {
        if (this.id_pagamento != null) {
            throw new IllegalStateException("O ID do pagamento já foi definido.");
        }
        this.id_pagamento = id;
    }

    public Venda getVenda() { return venda; }
    public String getFormaPagamento() { return formaPagamento; }
    public Double getValorPago() { return valorPago; }
    public LocalDate getDataPagamento() { return dataPagamento; }

    public boolean validarPagamento() {
        return valorPago > 0;
    }

    public boolean validarFormaPagamento() {
        return formaPagamento.equalsIgnoreCase("pix") || formaPagamento.equalsIgnoreCase("cartão");
    }

    public void exibirResumo() {
        System.out.println("Resumo do Pagamento:");
        System.out.println("ID: " + id_pagamento);
        System.out.println("Forma de Pagamento: " + formaPagamento);
        System.out.println("Valor Pago: " + valorPago);
        System.out.println("Data do Pagamento: " + dataPagamento);
    }

    /** Cada forma de pagamento monta seu próprio recibo (polimorfismo). */
    public abstract String obterReciboDetalhado();
}
