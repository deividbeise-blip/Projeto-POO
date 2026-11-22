package infra.excecao;

/** Erro ao falar com o banco de dados. Não verificada: quem não sabe tratar, deixa subir. */
public class PersistenciaException extends RuntimeException {

    /**
     * @param mensagem descrição da operação que falhou, para mostrar ao usuário
     * @param causa erro original do JDBC
     */
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    /** @param mensagem descrição do problema, quando não há exceção de origem */
    public PersistenciaException(String mensagem) {
        super(mensagem);
    }
}
