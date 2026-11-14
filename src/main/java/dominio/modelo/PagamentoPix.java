package dominio.modelo;

import dominio.politica.DescontoPix;
import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;

/** Pagamento via PIX: guarda a chave usada e o tipo dela. */
public class PagamentoPix extends Pagamento {
    private Long idPagamentoPix; // AUTO_INCREMENT
    private final String chavePix;
    private final String tipoChave; // CPF, CNPJ, E-mail, Telefone ou Aleatória

    /**
     * @param venda venda que está sendo paga
     * @param valorBase preço do veículo, antes do desconto
     * @param dataPagamento dia do pagamento
     * @param chavePix chave PIX informada pelo cliente
     * @param tipoChave tipo da chave (CPF, CNPJ, Email ou Telefone)
     * @param politicaDesconto regra de desconto (o AppConfig entrega DescontoPix)
     * @throws IllegalArgumentException se a chave ou o tipo estiverem vazios, ou a parte comum for inválida
     */
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

    /** @return a chave PIX, sem espaços nas pontas */
    public String getChavePix() { return chavePix; }
    /** @return o tipo da chave (CPF, CNPJ, Email ou Telefone) */
    public String getTipoChave() { return tipoChave; }

    /** @return ID do registro na tabela pagamentopix, ou null se ainda não foi gravado */
    public Long getIdPagamentoPix() {
        return idPagamentoPix;
    }

    /**
     * Define o ID do detalhe PIX; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdPagamentoPix(Long id) {
        if (this.idPagamentoPix != null) {
            throw new IllegalStateException("O ID do pagamento PIX já foi definido.");
        }
        this.idPagamentoPix = id;
    }

    /** @return recibo com a chave PIX, o valor com desconto e a data */
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
