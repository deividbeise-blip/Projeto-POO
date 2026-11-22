package apresentacao.comum;

import apresentacao.estilo.Componentes;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Marca campos inválidos no formulário: contorno vermelho (FlatLaf) e mensagem pequena abaixo do campo.
 * As regras em si NÃO ficam aqui: elas vivem no domínio (ex.: {@code Cliente.validarCpf}) e a tela só
 * as chama por {@link #verificar(JComponent, Runnable)}.
 *
 * <pre>{@code
 * boolean ok = Validacao.verificar(campoCpf, () -> Cliente.validarCpf(campoCpf.getText()));
 * }</pre>
 */
public final class Validacao {

    private static final String PROPRIEDADE_CONTORNO = "JComponent.outline";
    private static final String CONTORNO_ERRO = "error";
    private static final String CHAVE_LIMPEZA_INSTALADA = "Validacao.limpezaInstalada";

    private Validacao() { }

    /**
     * Marca o campo como inválido: contorno vermelho e {@code mensagem} logo abaixo dele.
     * A marcação some sozinha quando o usuário volta a digitar (ou escolhe outro item) no campo.
     * Se o campo não foi adicionado por {@code Componentes.adicionarCampo}, a mensagem vira dica (tooltip).
     *
     * @param campo    o campo inválido
     * @param mensagem o motivo, de preferência a mensagem da regra do domínio
     */
    public static void marcarErro(JComponent campo, String mensagem) {
        campo.putClientProperty(PROPRIEDADE_CONTORNO, CONTORNO_ERRO);
        if (!Componentes.definirMensagemErro(campo, mensagem)) {
            campo.setToolTipText(mensagem);
        }
        instalarLimpezaAutomatica(campo);
    }

    /**
     * Remove o contorno vermelho e a mensagem de erro do campo.
     *
     * @param campo o campo
     */
    public static void limpar(JComponent campo) {
        if (campo.getClientProperty(PROPRIEDADE_CONTORNO) == null) {
            return; // nada marcado: evita apagar uma dica (tooltip) que a tela tenha definido
        }
        campo.putClientProperty(PROPRIEDADE_CONTORNO, null);
        if (!Componentes.definirMensagemErro(campo, null)) {
            campo.setToolTipText(null);
        }
    }

    /**
     * Roda uma regra do domínio para o campo: se ela lançar {@link IllegalArgumentException},
     * marca o campo com a mensagem da exceção; se passar, limpa a marcação.
     *
     * @param campo o campo sendo validado
     * @param regra chamada à regra do domínio, ex.: {@code () -> Cliente.validarCpf(campoCpf.getText())}
     * @return {@code true} se o valor é válido
     */
    public static boolean verificar(JComponent campo, Runnable regra) {
        try {
            regra.run();
            limpar(campo);
            return true;
        } catch (IllegalArgumentException e) {
            marcarErro(campo, e.getMessage());
            return false;
        }
    }

    /** Limpa a marcação na primeira mudança feita pelo usuário. Instala uma vez só por campo. */
    private static void instalarLimpezaAutomatica(JComponent campo) {
        if (campo.getClientProperty(CHAVE_LIMPEZA_INSTALADA) != null) {
            return;
        }
        campo.putClientProperty(CHAVE_LIMPEZA_INSTALADA, Boolean.TRUE);
        if (campo instanceof JTextComponent texto) {
            texto.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { limpar(campo); }
                @Override public void removeUpdate(DocumentEvent e) { limpar(campo); }
                @Override public void changedUpdate(DocumentEvent e) { limpar(campo); }
            });
        } else if (campo instanceof JComboBox<?> combo) {
            combo.addActionListener(e -> limpar(campo));
        } else if (campo instanceof JSpinner spinner) {
            spinner.addChangeListener(e -> limpar(campo));
        }
    }
}
