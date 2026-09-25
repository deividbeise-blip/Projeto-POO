package modelo;
import java.time.LocalDate;

import dao.PagamentoDAO;

public abstract class Pagamento {
    private Long id_pagamento; //AUTO_INCREMENT
    private Venda venda;
    private String formaPagamento;
    private Double valorPago;
    private LocalDate dataPagamento;

    public Pagamento(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento) {
        this.venda = venda;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.dataPagamento = dataPagamento;
        try {
            PagamentoDAO pagamentoDAO = new PagamentoDAO();
            pagamentoDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no banco. Banco deve estar offline", e);
        }
    }

    public Long getId_pagamento() {
        return id_pagamento;
    }

    public void setId_pagamento(Long id_pagamento) {
        this.id_pagamento = id_pagamento;
    }

    public Venda getVenda() {
        return venda;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public Double getValorPago() {
        return valorPago;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public boolean validarPagamento() {
        return valorPago>0;
    }
    public boolean validarFormaPagamento() {
        return formaPagamento.equalsIgnoreCase("pix") || formaPagamento.equalsIgnoreCase("cartão");
    }
    public void aplicarDesconto(Double desconto) {
        if (desconto > 0 && desconto <= 100) {
            valorPago -= valorPago * (desconto / 100);
        }
    }
    public void exibirResumo() {
        System.out.println("Resumo do Pagamento:");
        System.out.println("ID: " + id_pagamento);
        System.out.println("Venda: " + venda);
        System.out.println("Forma de Pagamento: " + formaPagamento);
        System.out.println("Valor Pago: " + valorPago);
        System.out.println("Data do Pagamento: " + dataPagamento);
    }

    /**
     * Método abstrato que obriga cada subclasse (PagamentoPix, PagamentoCartao)
     * a fornecer sua própria versão do recibo, de acordo com suas particularidades.
     * Garante o critério de Polimorfismo (cada forma de pagamento gera um recibo diferente)
     * e reforça a Abstração (a classe mãe não sabe "como" o recibo é montado, só que ele existe).
     */
    public abstract String obterReciboDetalhado();
}
