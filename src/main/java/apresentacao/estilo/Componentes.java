package apresentacao.estilo;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/** Fábrica de componentes visuais usados pelas telas. */
public final class Componentes {
    private Componentes() { }

    private static JButton botao(String texto, Color fundo, Color textoCor) {
        JButton botao = new JButton(texto);
        botao.setFont(Tema.BOTAO);
        botao.setBackground(fundo);
        botao.setForeground(textoCor);
        botao.setMargin(new Insets(10, 18, 10, 18));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    public static JButton botaoPrimario(String texto) { return botao(texto, Tema.PRIMARIA, Color.WHITE); }
    public static JButton botaoSecundario(String texto) { return botao(texto, Tema.SUPERFICIE, Tema.PRIMARIA); }
    public static JButton botaoPerigo(String texto) { return botao(texto, Tema.ERRO, Color.WHITE); }

    public static JTextField campoTexto() {
        JTextField campo = new JTextField(24);
        campo.setFont(Tema.TEXTO_FONTE);
        return campo;
    }

    public static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.ROTULO);
        rotulo.setForeground(Tema.TEXTO);
        return rotulo;
    }

    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(Tema.TITULO);
        titulo.setForeground(Tema.TEXTO);
        return titulo;
    }

    public static <T> JComboBox<T> combo(T[] itens) {
        JComboBox<T> combo = new JComboBox<>(itens);
        combo.setFont(Tema.TEXTO_FONTE);
        return combo;
    }

    public static JPanel painelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setOpaque(false);
        return painel;
    }

    /** Adiciona uma linha alinhada ao formulário compartilhado. */
    public static void adicionarCampo(JPanel formulario, int linha, String nome, JComponent campo) {
        JLabel rotulo = rotulo(nome);
        rotulo.setLabelFor(campo);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = linha;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(Tema.PEQUENO, 0, Tema.PEQUENO, Tema.GRANDE);
        formulario.add(rotulo, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(Tema.PEQUENO, 0, Tema.PEQUENO, 0);
        formulario.add(campo, c);
    }

    public static JPanel painelCartao(String texto) {
        JPanel painel = new JPanel(new BorderLayout(0, Tema.MEDIO));
        painel.setBackground(Tema.SUPERFICIE);
        painel.setBorder(new CompoundBorder(new LineBorder(Tema.BORDA, 1, true),
                BorderFactory.createEmptyBorder(Tema.GRANDE, Tema.GRANDE, Tema.GRANDE, Tema.GRANDE)));
        JLabel titulo = rotulo(texto);
        titulo.setFont(Tema.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);
        return painel;
    }
}
