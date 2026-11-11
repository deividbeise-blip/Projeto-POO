package apresentacao.estilo;

import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/** Fábrica de componentes visuais usados pelas telas. */
public final class Componentes {
    private static final int ALTURA_ITEM_MENU = 44;

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

    /**
     * Item do menu lateral (fundo escuro). O anel de foco é claro para aparecer sobre o fundo
     * do menu; a cor de fundo de cada item é definida por quem marca o item selecionado.
     *
     * @param texto  nome da tela
     * @param atalho letra do atalho Alt+letra; precisa existir no texto e não se repetir no menu
     * @return o botão do menu
     */
    public static JButton botaoMenu(String texto, char atalho) {
        JButton botao = botao(texto, Tema.PRIMARIA, Tema.SUPERFICIE);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, ALTURA_ITEM_MENU));
        botao.setMnemonic(atalho);
        botao.putClientProperty("FlatLaf.style", Map.of("focusColor", Tema.SUPERFICIE));
        return botao;
    }

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

    /** Chave (client property do campo) que guarda o rótulo da mensagem de erro reservado para ele. */
    private static final String CHAVE_MENSAGEM_ERRO = "Componentes.mensagemErro";
    /** Texto do rótulo de erro vazio: um espaço mantém a altura e evita o formulário "pular". */
    private static final String SEM_MENSAGEM = " ";

    /**
     * Adiciona uma linha alinhada ao formulário compartilhado: rótulo à esquerda, campo à direita
     * e, logo abaixo do campo, um espaço reservado para a mensagem de erro da {@code Validacao}.
     * Cada linha ocupa duas linhas da grade, então {@code linha} continua sendo 0, 1, 2...
     *
     * @param formulario painel criado por {@link #painelFormulario()}
     * @param linha      posição do campo no formulário, começando em 0
     * @param nome       texto do rótulo
     * @param campo      o campo de entrada
     */
    public static void adicionarCampo(JPanel formulario, int linha, String nome, JComponent campo) {
        JLabel rotulo = rotulo(nome);
        rotulo.setLabelFor(campo);
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = linha * 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(Tema.PEQUENO, 0, 0, Tema.GRANDE);
        formulario.add(rotulo, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(Tema.PEQUENO, 0, 0, 0);
        formulario.add(campo, c);

        JLabel mensagemErro = new JLabel(SEM_MENSAGEM);
        mensagemErro.setFont(Tema.LEGENDA);
        mensagemErro.setForeground(Tema.ERRO);
        campo.putClientProperty(CHAVE_MENSAGEM_ERRO, mensagemErro);
        c.gridy = linha * 2 + 1;
        c.insets = new Insets(0, 0, 0, 0);
        formulario.add(mensagemErro, c);
    }

    /**
     * Devolve o rótulo de mensagem de erro reservado abaixo do campo por
     * {@link #adicionarCampo(JPanel, int, String, JComponent)}.
     *
     * @param campo o campo do formulário
     * @return o rótulo, ou {@code null} se o campo não foi adicionado por {@code adicionarCampo}
     */
    public static JLabel mensagemErroDo(JComponent campo) {
        Object valor = campo.getClientProperty(CHAVE_MENSAGEM_ERRO);
        return valor instanceof JLabel rotulo ? rotulo : null;
    }

    /**
     * Mostra (ou, com {@code null}, apaga) o texto de erro reservado abaixo do campo.
     *
     * @param campo    o campo do formulário
     * @param mensagem o texto, ou {@code null} para apagar
     * @return {@code true} se o campo tinha espaço reservado; {@code false} se não tinha (nada mudou)
     */
    public static boolean definirMensagemErro(JComponent campo, String mensagem) {
        JLabel rotulo = mensagemErroDo(campo);
        if (rotulo == null) {
            return false;
        }
        rotulo.setText(mensagem == null || mensagem.isBlank() ? SEM_MENSAGEM : mensagem);
        return true;
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
