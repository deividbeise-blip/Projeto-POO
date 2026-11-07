package infra.excecao;

/** Erro ao falar com o banco de dados. Não verificada: quem não sabe tratar, deixa subir. */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }
}
