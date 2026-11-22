package dominio.excecao;

/**
 * Regra de negócio: a venda está incompleta ou inconsistente (sem pagamento,
 * valor zerado, vendedor ou pagamento inválidos) e não pode ser finalizada.
 */
public class VendaInvalidaException extends Exception {

    /**
     * @param mensagem explicação pronta para exibir ao usuário
     */
    public VendaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
