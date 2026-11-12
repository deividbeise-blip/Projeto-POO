package apresentacao.comum;

import java.awt.Component;
import java.awt.Cursor;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Roda operações demoradas (banco de dados, por exemplo) fora da thread da tela,
 * para a janela não travar enquanto espera a resposta.
 *
 * <pre>{@code
 * TarefaEmSegundoPlano.executar(
 *     () -> clienteService.listar(),        // roda fora da thread da tela
 *     clientes -> mostrar(clientes),         // roda na tela, quando terminar
 *     erro -> Feedback.erro(this, erro));    // roda na tela, se der erro
 * }</pre>
 *
 * Os dois retornos ({@code aoConcluir} e {@code aoFalhar}) sempre rodam na thread da tela,
 * então podem mexer em componentes Swing à vontade. O {@code trabalho} NÃO pode mexer em
 * componentes: leia os valores dos campos antes e passe-os para dentro do lambda.
 */
public final class TarefaEmSegundoPlano {

    /** Chave usada no JRootPane para contar quantas tarefas com cursor de espera estão rodando. */
    private static final String CHAVE_TAREFAS_ATIVAS = "TarefaEmSegundoPlano.ativas";
    /** Chave usada no componente de origem para guardar o cursor que ele tinha antes da espera. */
    private static final String CHAVE_CURSOR_ANTERIOR = "TarefaEmSegundoPlano.cursorAnterior";

    private TarefaEmSegundoPlano() { }

    /**
     * Trabalho executado fora da thread da tela. Diferente de {@link java.util.function.Supplier},
     * pode lançar exceções verificadas (ex.: {@code VeiculoIndisponivelException}).
     *
     * @param <T> tipo do resultado
     */
    @FunctionalInterface
    public interface Trabalho<T> {

        /**
         * Executa o trabalho.
         *
         * @return o resultado, entregue depois ao {@code aoConcluir}
         * @throws Exception qualquer erro; é entregue ao {@code aoFalhar}
         */
        T executar() throws Exception;
    }

    /**
     * Executa {@code trabalho} em segundo plano e entrega o resultado (ou o erro) na thread da tela.
     *
     * @param trabalho   o que roda fora da thread da tela (acesso ao banco, por exemplo)
     * @param aoConcluir chamado na thread da tela com o resultado, se deu certo
     * @param aoFalhar   chamado na thread da tela com a causa real do erro, se deu errado
     * @param <T>        tipo do resultado
     * @throws NullPointerException se algum argumento for nulo
     */
    public static <T> void executar(Trabalho<T> trabalho, Consumer<? super T> aoConcluir,
                                    Consumer<? super Throwable> aoFalhar) {
        executar(null, trabalho, aoConcluir, aoFalhar);
    }

    /**
     * Igual a {@link #executar(Trabalho, Consumer, Consumer)}, mas também sinaliza que algo está
     * rodando: enquanto o trabalho não termina, {@code origem} fica desabilitado (evita clique duplo)
     * e a janela mostra o cursor de espera. Ao terminar, com sucesso ou erro, tudo volta ao normal
     * antes de chamar {@code aoConcluir} ou {@code aoFalhar}.
     *
     * @param origem     componente que disparou a ação (normalmente o botão); pode ser {@code null}
     * @param trabalho   o que roda fora da thread da tela
     * @param aoConcluir chamado na thread da tela com o resultado, se deu certo
     * @param aoFalhar   chamado na thread da tela com a causa real do erro, se deu errado
     * @param <T>        tipo do resultado
     * @throws NullPointerException se {@code trabalho}, {@code aoConcluir} ou {@code aoFalhar} for nulo
     */
    public static <T> void executar(Component origem, Trabalho<T> trabalho, Consumer<? super T> aoConcluir,
                                    Consumer<? super Throwable> aoFalhar) {
        Objects.requireNonNull(trabalho, "trabalho é obrigatório.");
        Objects.requireNonNull(aoConcluir, "aoConcluir é obrigatório.");
        Objects.requireNonNull(aoFalhar, "aoFalhar é obrigatório.");

        JRootPane janela = origem == null ? null : SwingUtilities.getRootPane(origem);
        iniciarEspera(origem, janela);

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return trabalho.executar();
            }

            @Override
            protected void done() {
                encerrarEspera(origem, janela);
                T resultado;
                try {
                    resultado = get();
                } catch (ExecutionException e) {
                    aoFalhar.accept(e.getCause() != null ? e.getCause() : e);
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    aoFalhar.accept(e);
                    return;
                } catch (RuntimeException e) { // CancellationException
                    aoFalhar.accept(e);
                    return;
                }
                try {
                    aoConcluir.accept(resultado);
                } catch (RuntimeException e) { // erro ao mostrar o resultado também chega ao usuário
                    aoFalhar.accept(e);
                }
            }
        }.execute();
    }

    private static void iniciarEspera(Component origem, JRootPane janela) {
        if (origem != null) {
            origem.setEnabled(false);
            if (origem instanceof JComponent componente && origem.isCursorSet()) {
                componente.putClientProperty(CHAVE_CURSOR_ANTERIOR, origem.getCursor());
            }
            origem.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
        if (janela != null) {
            int ativas = tarefasAtivas(janela) + 1;
            janela.putClientProperty(CHAVE_TAREFAS_ATIVAS, ativas);
            janela.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }

    private static void encerrarEspera(Component origem, JRootPane janela) {
        if (origem != null) {
            origem.setEnabled(true);
            Object anterior = origem instanceof JComponent componente
                    ? componente.getClientProperty(CHAVE_CURSOR_ANTERIOR) : null;
            origem.setCursor(anterior instanceof Cursor cursor ? cursor : null);
        }
        if (janela != null) {
            // Só volta o cursor quando a ÚLTIMA tarefa da janela termina.
            int ativas = Math.max(0, tarefasAtivas(janela) - 1);
            janela.putClientProperty(CHAVE_TAREFAS_ATIVAS, ativas);
            if (ativas == 0) {
                janela.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private static int tarefasAtivas(JRootPane janela) {
        Object valor = janela.getClientProperty(CHAVE_TAREFAS_ATIVAS);
        return valor instanceof Integer n ? n : 0;
    }
}
