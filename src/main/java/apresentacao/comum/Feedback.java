package apresentacao.comum;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Jeito único de avisar o usuário: sucesso na barra de status, erro e confirmação em diálogo.
 * Todos os métodos devem ser chamados na thread da tela (os retornos da
 * {@link TarefaEmSegundoPlano} já rodam nela).
 */
public final class Feedback {

    /** Mensagem mostrada quando o erro não é de regra de negócio (banco fora do ar, bug etc.). */
    public static final String MENSAGEM_ERRO_GENERICO =
            "Não foi possível concluir a operação. Verifique a conexão com o banco de dados.";

    private static final String SIM = "Sim";
    private static final String NAO = "Não";

    private Feedback() { }

    /**
     * Mostra uma mensagem de sucesso na barra de status da janela, que some sozinha.
     * Se a janela não tiver barra de status, mostra um diálogo simples.
     *
     * @param origem   componente da tela que fez a operação (usado para achar a janela)
     * @param mensagem o texto a mostrar
     */
    public static void sucesso(Component origem, String mensagem) {
        BarraStatus barra = BarraStatus.daJanelaDe(origem);
        if (barra != null) {
            barra.mostrarSucesso(mensagem);
        } else {
            mostrar(origem, new JOptionPane(mensagem, JOptionPane.INFORMATION_MESSAGE), "Sucesso");
        }
    }

    /**
     * Mostra um diálogo de erro com uma mensagem que o usuário entende.
     * Erros de regra de negócio mostram a própria mensagem da exceção; os demais mostram
     * {@link #MENSAGEM_ERRO_GENERICO} e têm o stack trace impresso em {@code System.err}.
     *
     * @param origem componente da tela que fez a operação (o diálogo fica centralizado na janela dele)
     * @param erro   o erro recebido
     */
    public static void erro(Component origem, Throwable erro) {
        String mensagem;
        if (ehErroDeNegocio(erro) && erro.getMessage() != null && !erro.getMessage().isBlank()) {
            mensagem = erro.getMessage();
        } else {
            mensagem = MENSAGEM_ERRO_GENERICO;
            if (erro != null) {
                erro.printStackTrace();
            }
        }
        mostrar(origem, new JOptionPane(mensagem, JOptionPane.ERROR_MESSAGE), "Erro");
    }

    /**
     * Pergunta algo ao usuário com os botões "Sim" e "Não". O foco começa em "Não",
     * para um Enter distraído não confirmar uma exclusão.
     *
     * @param origem   componente da tela que fez a pergunta
     * @param pergunta o texto da pergunta
     * @return {@code true} só se o usuário escolheu "Sim"; fechar o diálogo conta como "Não"
     */
    public static boolean confirmar(Component origem, String pergunta) {
        Object[] opcoes = {SIM, NAO};
        JOptionPane painel = new JOptionPane(pergunta, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION, null, opcoes, NAO);
        mostrar(origem, painel, "Confirmação");
        return SIM.equals(painel.getValue());
    }

    private static boolean ehErroDeNegocio(Throwable erro) {
        return erro instanceof IllegalArgumentException
                || erro instanceof IllegalStateException
                || erro instanceof VeiculoIndisponivelException
                || erro instanceof VendaInvalidaException;
    }

    /** Cria e mostra o diálogo (modal) do painel. */
    private static void mostrar(Component origem, JOptionPane painel, String titulo) {
        JDialog dialogo = painel.createDialog(origem, titulo);
        dialogo.setVisible(true);
        dialogo.dispose();
    }
}
