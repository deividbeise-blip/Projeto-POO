package apresentacao.estilo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Window;
import java.util.function.Consumer;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

/**
 * Identidade visual compartilhada pelas telas da aplicação: cores, fontes e espaçamentos.
 * As cores vêm de duas paletas (clara e escura) com os mesmos papéis; as telas leem a cor
 * pelos métodos ({@link #fundo()}, {@link #texto()}...) sem saber qual modo está ativo.
 *
 * <p>Troca de modo: {@link #aplicar(Modo)} troca o Look and Feel e a paleta e atualiza as janelas
 * abertas. O Look and Feel repinta sozinho o que usa as cores padrão dele; um componente que recebe
 * cor própria do tema deve recebê-la por {@link #colorir(JComponent, Consumer)}, que reaplica as
 * cores a cada troca.
 */
public final class Tema {

    /** Modos visuais disponíveis. */
    public enum Modo {
        /** Fundo claro e texto escuro (padrão). */
        CLARO,
        /** Fundo escuro e texto claro. */
        ESCURO
    }

    /**
     * Conjunto de cores de um modo. Cada campo é um papel; as duas paletas têm os mesmos papéis.
     *
     * @param primaria         azul da marca: menu lateral e botões principais
     * @param secundaria       item de menu selecionado e anel de foco
     * @param fundo            fundo das telas, atrás dos cartões
     * @param superficie       fundo dos cartões e da barra de status
     * @param texto            texto principal
     * @param textoSecundario  textos de apoio, menos importantes
     * @param borda            contorno de cartões, divisórias e grade das tabelas
     * @param sucesso          verde de sucesso (barra de status, veículo disponível)
     * @param erro             vermelho de erro (mensagens de validação, botão de exclusão)
     * @param aviso            amarelo de aviso (veículo em manutenção)
     * @param neutro           cinza neutro (veículo vendido)
     * @param textoSobreEscuro texto sobre a cor primária: menu lateral e botões primário e de perigo
     */
    private record Paleta(Color primaria, Color secundaria, Color fundo, Color superficie,
                          Color texto, Color textoSecundario, Color borda, Color sucesso,
                          Color erro, Color aviso, Color neutro, Color textoSobreEscuro) { }

    private static final Paleta CLARA = new Paleta(
            new Color(35, 67, 109),    // primária
            new Color(68, 115, 158),   // secundária
            new Color(244, 247, 251),  // fundo
            Color.WHITE,               // superfície
            new Color(31, 44, 59),     // texto
            new Color(95, 110, 126),   // texto secundário
            new Color(221, 228, 237),  // borda
            new Color(30, 122, 84),    // sucesso
            new Color(181, 48, 53),    // erro
            new Color(160, 103, 20),   // aviso
            new Color(108, 117, 125),  // neutro
            Color.WHITE);              // texto sobre escuro

    /*
     * Paleta escura: fundo quase preto, superfícies um pouco mais claras que o fundo e texto claro
     * (contraste do texto sobre a superfície ≈ 10:1). Primária e secundária continuam escuras o
     * bastante para o texto branco (≥ 4,5:1); as cores de status são mais claras que no modo claro
     * para ficarem legíveis sobre fundo escuro.
     */
    private static final Paleta ESCURA = new Paleta(
            new Color(47, 86, 138),    // primária
            new Color(66, 118, 182),   // secundária
            new Color(35, 38, 43),     // fundo
            new Color(47, 51, 58),     // superfície
            new Color(226, 231, 238),  // texto
            new Color(165, 174, 186),  // texto secundário
            new Color(72, 78, 88),     // borda
            new Color(110, 210, 155),  // sucesso
            new Color(232, 108, 108),  // erro
            new Color(235, 185, 90),   // aviso
            new Color(185, 192, 200),  // neutro
            new Color(245, 247, 250)); // texto sobre escuro

    private static Modo modo = Modo.CLARO;
    private static Paleta paleta = CLARA;

    /** Client property em que {@link #colorir(JComponent, Consumer)} guarda como pintar o componente. */
    private static final String CHAVE_CORES = "Tema.cores";

    /** Espaçamentos padrão, em pixels: use sempre estes em margens e distâncias entre componentes. */
    public static final int PEQUENO = 8, MEDIO = 16, GRANDE = 24, MARGEM_TELA = 32;
    /** Margem interna dos botões, em pixels (vertical e horizontal). */
    public static final int MARGEM_BOTAO_VERTICAL = 10, MARGEM_BOTAO_HORIZONTAL = 18;
    /** Altura das linhas de todas as tabelas, em pixels. */
    public static final int ALTURA_LINHA_TABELA = 28;
    /** Largura do menu lateral e altura de cada item dele, em pixels. */
    public static final int LARGURA_MENU = 240, ALTURA_ITEM_MENU = 44;
    /** Tamanho inicial e mínimo da janela principal: tudo precisa caber em 1280×720. */
    public static final int LARGURA_JANELA = 1280, ALTURA_JANELA = 720;
    /** Tamanho da janela do recibo da venda. */
    public static final int LARGURA_RECIBO = 480, ALTURA_RECIBO = 420;
    /** Fonte do título da tela. */
    public static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    /** Fonte do título dos cartões e do nome no menu. */
    public static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    /** Fonte de campos, tabelas e textos em geral. */
    public static final Font TEXTO_FONTE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    /** Fonte dos rótulos dos campos. */
    public static final Font ROTULO = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    /** Fonte dos botões e do status na tabela de veículos. */
    public static final Font BOTAO = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    /** Texto pequeno de apoio, como a mensagem de erro abaixo de um campo. */
    public static final Font LEGENDA = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    private Tema() { }

    /**
     * Modo visual ativo.
     *
     * @return {@link Modo#CLARO} ou {@link Modo#ESCURO}
     */
    public static Modo modo() { return modo; }

    /** @return azul da marca, do modo ativo: menu lateral e botões principais */
    public static Color primaria() { return paleta.primaria(); }
    /** @return cor do item de menu selecionado e do anel de foco, no modo ativo */
    public static Color secundaria() { return paleta.secundaria(); }
    /** @return fundo das telas (atrás dos cartões), no modo ativo */
    public static Color fundo() { return paleta.fundo(); }
    /** @return fundo dos cartões e da barra de status, no modo ativo */
    public static Color superficie() { return paleta.superficie(); }
    /** @return cor do texto principal, no modo ativo */
    public static Color texto() { return paleta.texto(); }
    /** @return cor dos textos de apoio, no modo ativo */
    public static Color textoSecundario() { return paleta.textoSecundario(); }
    /** @return contorno de cartões, divisórias e grade das tabelas, no modo ativo */
    public static Color borda() { return paleta.borda(); }
    /** @return verde de sucesso (barra de status, veículo disponível), no modo ativo */
    public static Color sucesso() { return paleta.sucesso(); }
    /** @return vermelho de erro (mensagens de validação, botão de exclusão), no modo ativo */
    public static Color erro() { return paleta.erro(); }
    /** @return amarelo de aviso (veículo em manutenção), no modo ativo */
    public static Color aviso() { return paleta.aviso(); }
    /** @return cinza neutro (veículo vendido), no modo ativo */
    public static Color neutro() { return paleta.neutro(); }
    /** @return texto sobre a cor primária (menu lateral, botões primário e de perigo), no modo ativo */
    public static Color textoSobreEscuro() { return paleta.textoSobreEscuro(); }

    /**
     * Configura os padrões do FlatLaf com as cores do modo ativo. Chame depois de instalar o
     * Look and Feel e antes de criar os componentes.
     */
    public static void aplicar() {
        UIManager.put("defaultFont", TEXTO_FONTE);
        // ColorUIResource: marca a cor como "vinda do tema", para o Look and Feel poder trocá-la depois.
        UIManager.put("Component.focusColor", new ColorUIResource(secundaria()));
        // O padrão do FlatLaf Light (focusWidth 0) quase não mostra o foco em botões com fundo próprio;
        // um anel de 2px deixa visível quem está com o foco ao navegar pelo teclado.
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Panel.background", new ColorUIResource(fundo()));
    }

    /**
     * Ativa um modo: instala o Look and Feel correspondente ({@link FlatLightLaf} ou {@link FlatDarkLaf}),
     * troca a paleta, reaplica os padrões de {@link #aplicar()} e atualiza todas as janelas abertas,
     * repintando os componentes registrados em {@link #colorir(JComponent, Consumer)}.
     * Pode ser chamado antes de abrir a primeira janela. Chame na thread da tela.
     *
     * @param novoModo o modo a ativar
     */
    public static void aplicar(Modo novoModo) {
        modo = novoModo;
        paleta = novoModo == Modo.ESCURO ? ESCURA : CLARA;
        if (novoModo == Modo.ESCURO) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        aplicar();
        FlatLaf.updateUI(); // updateComponentTreeUI em todas as janelas
        for (Window janela : Window.getWindows()) {
            recolorir(janela);
            janela.repaint();
        }
    }

    /** Alterna entre o modo claro e o escuro, sem reiniciar o aplicativo. Chame na thread da tela. */
    public static void alternarModo() {
        aplicar(modo == Modo.ESCURO ? Modo.CLARO : Modo.ESCURO);
    }

    /**
     * Pinta o componente com cores do tema agora e de novo a cada troca de modo.
     * Use para toda cor definida à mão ({@code setBackground}, {@code setForeground}, bordas coloridas,
     * cor da grade da tabela...). As cores devem ser lidas do {@code Tema} dentro de {@code cores},
     * para virem da paleta ativa. Cada componente guarda uma só pintura: registrar de novo substitui
     * a anterior (útil para trocar a cor padrão dada pelo {@link Componentes}).
     *
     * @param componente o componente a pintar
     * @param cores      aplica as cores no componente (ex.: {@code c -> c.setBackground(Tema.fundo())})
     * @param <T>        tipo do componente
     * @return o próprio componente, para encadear na criação
     */
    public static <T extends JComponent> T colorir(T componente, Consumer<? super T> cores) {
        cores.accept(componente);
        componente.putClientProperty(CHAVE_CORES, (Runnable) () -> cores.accept(componente));
        return componente;
    }

    /** Reaplica, de cima para baixo, as pinturas registradas por {@link #colorir} na árvore do componente. */
    private static void recolorir(Component componente) {
        if (componente instanceof JComponent jc && jc.getClientProperty(CHAVE_CORES) instanceof Runnable pintar) {
            pintar.run();
        }
        if (componente instanceof Container container) {
            for (Component filho : container.getComponents()) {
                recolorir(filho);
            }
        }
    }
}
