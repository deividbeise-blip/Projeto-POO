package dominio.modelo;

import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;

/**
 * Parte comum de qualquer forma de pagamento (PIX, cartão, dinheiro).
 * O valor pago é calculado uma vez, na criação, pela {@link PoliticaDesconto} recebida;
 * cada subclasse acrescenta seus dados e monta o próprio recibo.
 */
public abstract class Pagamento {
    private Long idPagamento; // AUTO_INCREMENT
    private final Venda venda;
    private final String formaPagamento;
    private final Double valorPago;
    private final LocalDate dataPagamento;

    /**
     * O valor pago já sai calculado pela PoliticaDesconto recebida,
     * então o objeto nasce com o valor final correto (antes de qualquer gravação).
     *
     * @param venda venda que está sendo paga
     * @param formaPagamento nome de exibição da forma ("PIX", "Cartão", "Dinheiro")
     * @param valorBase preço antes do desconto, em reais, maior que zero
     * @param dataPagamento dia do pagamento
     * @param politica regra que transforma o valor base no valor pago
     * @throws IllegalArgumentException se algum dado for nulo, vazio ou o valor não for positivo
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

    /** @return ID gerado pelo banco, ou null se ainda não foi gravado */
    public Long getIdPagamento() {
        return idPagamento;
    }

    /**
     * Define o ID gerado pelo banco; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdPagamento(Long id) {
        if (this.idPagamento != null) {
            throw new IllegalStateException("O ID do pagamento já foi definido.");
        }
        this.idPagamento = id;
    }

    /** @return a venda paga por este pagamento; nunca null */
    public Venda getVenda() { return venda; }
    /** @return nome de exibição da forma ("PIX", "Cartão", "Dinheiro") */
    public String getFormaPagamento() { return formaPagamento; }
    /** @return valor final em reais, já com a política de desconto aplicada */
    public Double getValorPago() { return valorPago; }
    /** @return o dia do pagamento */
    public LocalDate getDataPagamento() { return dataPagamento; }

    /** @return true se o valor pago é positivo */
    public boolean validarPagamento() {
        return valorPago > 0;
    }

    /**
     * Cada forma de pagamento monta seu próprio recibo (polimorfismo).
     *
     * @return texto do recibo do pagamento, em várias linhas
     */
    public abstract String obterReciboDetalhado();
}
