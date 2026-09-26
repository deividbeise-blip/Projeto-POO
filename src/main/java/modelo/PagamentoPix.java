package modelo;
import java.time.LocalDate;

import dao.PagamentoPixDAO;


public class PagamentoPix extends Pagamento {
    private Long id_pagamentoPix; //AUTO_INCREMENT
    private String chavePix;
    private String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    public PagamentoPix(Venda venda, String formaPagamento, Double valorPago, LocalDate dataPagamento, String chavePix, String tipoChave) {
        super(venda, formaPagamento, valorPago, dataPagamento);
        this.chavePix = chavePix;
        this.tipoChave = tipoChave;
        try {
            PagamentoPixDAO pagamentoPixDAO = new PagamentoPixDAO();
            pagamentoPixDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no banco. Banco deve estar offline", e);
        }
        // Regra automática: todo pagamento via PIX recebe 5% de desconto assim que é criado.
        aplicarDesconto(5.0);
    }

    public String getChavePix() {
        return chavePix;
    }
    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
        PagamentoPixDAO pagamentoPixDAO = new PagamentoPixDAO();
        pagamentoPixDAO.atualizar(this, "chave_pix", chavePix);
    }

    public String getTipoChave() {
        return tipoChave;
    }

    public void setTipoChave(String tipoChave) {
        this.tipoChave = tipoChave;
        PagamentoPixDAO pagamentoPixDAO = new PagamentoPixDAO();
        pagamentoPixDAO.atualizar(this, "tipo_chave_pix", tipoChave);
    }

    @Override
    public String obterReciboDetalhado() {
        return "===== Recibo de Pagamento (PIX) =====\n" +
                "Chave PIX: " + chavePix + " (" + tipoChave + ")\n" +
                "Valor pago (com 5% de desconto já aplicado): " + getValorPago() + "\n" +
                "Data do pagamento: " + getDataPagamento() + "\n" +
                "======================================";
    }

    public Long getId_pagamentoPix() {
        return id_pagamentoPix;
    }

    public void setId_pagamentoPix(Long id_pagamentoPix) {
        this.id_pagamentoPix = id_pagamentoPix;
    }
}
