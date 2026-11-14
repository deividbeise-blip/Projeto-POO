package apresentacao.comum;

import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Barra no rodapé da janela que mostra mensagens rápidas de sucesso e some sozinha.
 * Ao ser adicionada a uma janela, ela se registra no {@link JRootPane}; é assim que o
 * {@link Feedback} a encontra a partir de qualquer componente da mesma janela.
 */
public class BarraStatus extends JPanel {

    /** Tempo, em milissegundos, que a mensagem fica visível. */
    public static final int TEMPO_VISIVEL_MS = 4000;

    private static final String CHAVE = BarraStatus.class.getName();
    private static final String VAZIO = " "; // mantém a altura da barra mesmo sem mensagem

    private final JLabel mensagem = Componentes.rotulo(VAZIO);
    private final Timer temporizador = new Timer(TEMPO_VISIVEL_MS, e -> limpar());

    /** Cria a barra vazia, pronta para ser adicionada ao rodapé da janela. */
    public BarraStatus() {
        super(new BorderLayout());
        temporizador.setRepeats(false);
        setBackground(Tema.SUPERFICIE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA),
                BorderFactory.createEmptyBorder(Tema.PEQUENO, Tema.MARGEM_TELA, Tema.PEQUENO, Tema.MARGEM_TELA)));
        mensagem.setForeground(Tema.SUCESSO);
        add(mensagem, BorderLayout.CENTER);
    }

    /**
     * Mostra uma mensagem de sucesso, que some depois de {@link #TEMPO_VISIVEL_MS}.
     * Uma nova mensagem substitui a anterior e reinicia a contagem.
     *
     * @param texto a mensagem
     */
    public void mostrarSucesso(String texto) {
        mensagem.setText(texto);
        temporizador.restart();
    }

    /** Apaga a mensagem atual. */
    public void limpar() {
        temporizador.stop();
        mensagem.setText(VAZIO);
    }

    /**
     * Procura a barra de status da janela em que {@code componente} está.
     *
     * @param componente qualquer componente da janela; pode ser {@code null}
     * @return a barra, ou {@code null} se a janela não tiver uma (ou o componente não estiver numa janela)
     */
    public static BarraStatus daJanelaDe(Component componente) {
        JRootPane raiz = componente == null ? null : SwingUtilities.getRootPane(componente);
        return raiz == null ? null : (BarraStatus) raiz.getClientProperty(CHAVE);
    }

    /**
     * Ao entrar numa janela, registra esta barra no {@link JRootPane} dela,
     * para o {@link Feedback} encontrá-la por {@link #daJanelaDe(Component)}.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane raiz = getRootPane();
        if (raiz != null) {
            raiz.putClientProperty(CHAVE, this);
        }
    }

    /**
     * Ao sair da janela, desfaz o registro (se ainda for a barra registrada) e para o temporizador.
     */
    @Override
    public void removeNotify() {
        JRootPane raiz = getRootPane();
        if (raiz != null && raiz.getClientProperty(CHAVE) == this) {
            raiz.putClientProperty(CHAVE, null);
        }
        temporizador.stop();
        super.removeNotify();
    }
}
