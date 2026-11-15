package apresentacao.estilo;

import java.awt.*;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

/**
 * Fábrica dos componentes visuais usados pelas telas (botões, campos, rótulos, cartões e formulários).
 * Centraliza fonte, cor e espaçamento vindos do {@link Tema}, para todas as telas terem a mesma aparência.
 */
public final class Componentes {

    private Componentes() { }

    /** As cores chegam como {@link Supplier} para serem lidas de novo da paleta ativa a cada troca de modo. */
    private static JButton botao(String texto, Supplier<Color> fundo, Supplier<Color> textoCor) {
        JButton botao = Tema.colorir(new JButton(texto), b -> {
            b.setBackground(fundo.get());
            b.setForeground(textoCor.get());
        });
        botao.setFont(Tema.BOTAO);
        botao.setMargin(new Insets(Tema.MARGEM_BOTAO_VERTICAL, Tema.MARGEM_BOTAO_HORIZONTAL,
                Tema.MARGEM_BOTAO_VERTICAL, Tema.MARGEM_BOTAO_HORIZONTAL));
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    /**
     * Botão da ação principal da tela (fundo na cor primária, texto claro).
     *
     * @param texto rótulo do botão
     * @return o botão estilizado, ainda sem ação
     */
    public static JButton botaoPrimario(String texto) { return botao(texto, Tema::primaria, Tema::textoSobreEscuro); }
    /**
     * Botão de ação secundária, como "Novo" (fundo da superfície, texto na cor do texto principal).
     * O texto usa a cor do texto, e não a primária, para continuar legível no modo escuro.
     *
     * @param texto rótulo do botão
     * @return o botão estilizado, ainda sem ação
     */
    public static JButton botaoSecundario(String texto) { return botao(texto, Tema::superficie, Tema::texto); }
    /**
     * Botão de ação destrutiva, como "Excluir" (fundo vermelho, texto claro).
     *
     * @param texto rótulo do botão
     * @return o botão estilizado, ainda sem ação
     */
    public static JButton botaoPerigo(String texto) { return botao(texto, Tema::erro, Tema::textoSobreEscuro); }

    /**
     * Item do menu lateral (fundo na cor primária). O texto e o anel de foco usam a cor de texto
     * sobre escuro, para aparecerem sobre o fundo do menu nos dois modos. O fundo começa na cor
     * primária, mas quem marca o item selecionado é quem o mantém (inclusive ao trocar de modo).
     *
     * @param texto  nome da tela
     * @param atalho letra do atalho Alt+letra; precisa existir no texto e não se repetir no menu
     * @return o botão do menu
     */
    public static JButton botaoMenu(String texto, char atalho) {
        JButton botao = botao(texto, Tema::primaria, Tema::textoSobreEscuro);
        // Substitui a pintura do botão: o fundo fica por conta de quem controla a seleção.
        Tema.colorir(botao, b -> {
            b.setForeground(Tema.textoSobreEscuro());
            b.putClientProperty("FlatLaf.style", Map.of("focusColor", Tema.textoSobreEscuro()));
        });
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setAlignmentX(Component.LEFT_ALIGNMENT);
        botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, Tema.ALTURA_ITEM_MENU));
        botao.setMnemonic(atalho);
        return botao;
    }

    /**
     * Botão do rodapé do menu lateral que alterna entre o modo claro e o escuro ({@link Tema#alternarModo()}).
     * O texto indica o modo para o qual vai trocar ("Modo escuro" ou "Modo claro") e muda a cada clique.
     * Tem o visual de um item do menu, sempre com o fundo do menu.
     *
     * @param atalho letra do atalho Alt+letra; precisa existir em "Modo escuro" e em "Modo claro"
     *               e não se repetir no menu nem nos botões das telas
     * @return o botão, já com a ação de alternar o modo
     */
    public static JButton botaoModo(char atalho) {
        JButton botao = botaoMenu(textoBotaoModo(), atalho);
        Tema.colorir(botao, b -> {
            b.setBackground(Tema.primaria());
            b.setForeground(Tema.textoSobreEscuro());
            b.putClientProperty("FlatLaf.style", Map.of("focusColor", Tema.textoSobreEscuro()));
        });
        botao.addActionListener(e -> {
            Tema.alternarModo();
            botao.setText(textoBotaoModo());
        });
        return botao;
    }

    /** Nome do modo para o qual o botão de modo vai trocar. */
    private static String textoBotaoModo() {
        return Tema.modo() == Tema.Modo.ESCURO ? "Modo claro" : "Modo escuro";
    }

    /**
     * Campo de texto com a fonte padrão e largura inicial para cerca de 24 caracteres.
     *
     * @return o campo vazio
     */
    public static JTextField campoTexto() {
        JTextField campo = new JTextField(24);
        campo.setFont(Tema.TEXTO_FONTE);
        return campo;
    }

    /**
     * Texto comum de formulário e legendas, na fonte e cor padrão.
     *
     * @param texto conteúdo do rótulo
     * @return o rótulo
     */
    public static JLabel rotulo(String texto) {
        JLabel rotulo = new JLabel(texto);
        rotulo.setFont(Tema.ROTULO);
        Tema.colorir(rotulo, r -> r.setForeground(Tema.texto()));
        return rotulo;
    }

    /**
     * Título grande, usado no cabeçalho de cada tela.
     *
     * @param texto conteúdo do título
     * @return o rótulo de título
     */
    public static JLabel titulo(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(Tema.TITULO);
        Tema.colorir(titulo, t -> t.setForeground(Tema.texto()));
        return titulo;
    }

    /**
     * Lista suspensa com a fonte padrão. Para trocar os itens depois, use {@code setModel}.
     *
     * @param itens opções iniciais (pode ser um vetor vazio)
     * @param <T> tipo dos itens; o texto exibido vem do {@code toString()} de cada um
     * @return o combo
     */
    public static <T> JComboBox<T> combo(T[] itens) {
        JComboBox<T> combo = new JComboBox<>(itens);
        combo.setFont(Tema.TEXTO_FONTE);
        return combo;
    }

    /**
     * Painel transparente em grade, pronto para receber linhas por
     * {@link #adicionarCampo(JPanel, int, String, JComponent)}.
     *
     * @return o painel vazio
     */
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
        Tema.colorir(mensagemErro, m -> m.setForeground(Tema.erro()));
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

    /**
     * "Cartão" na cor da superfície, com borda arredondada e título no topo, usado para agrupar partes da tela.
     * O conteúdo deve ir em {@code BorderLayout.CENTER} (o topo já tem o título).
     *
     * @param texto título do cartão
     * @return o painel com {@code BorderLayout}
     */
    public static JPanel painelCartao(String texto) {
        JPanel painel = new JPanel(new BorderLayout(0, Tema.MEDIO));
        Tema.colorir(painel, p -> {
            p.setBackground(Tema.superficie());
            p.setBorder(new CompoundBorder(new LineBorder(Tema.borda(), 1, true),
                    BorderFactory.createEmptyBorder(Tema.GRANDE, Tema.GRANDE, Tema.GRANDE, Tema.GRANDE)));
        });
        JLabel titulo = rotulo(texto);
        titulo.setFont(Tema.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);
        return painel;
    }
}
