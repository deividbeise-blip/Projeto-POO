package dominio.excecao;

/**
 * Regra de negócio: o veículo não pode passar para o status pedido
 * (vender um vendido ou em manutenção, mandar um vendido para manutenção etc.).
 * É verificada: quem chama precisa tratar e mostrar a mensagem ao usuário.
 */
public class VeiculoIndisponivelException extends Exception {

    /**
     * @param mensagem explicação pronta para exibir ao usuário
     */
    public VeiculoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
