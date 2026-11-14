package apresentacao.estilo;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

/** Identidade visual compartilhada pelas telas da aplicação. */
public final class Tema {
    /** Azul escuro da marca: menu lateral e botões principais. */
    public static final Color PRIMARIA = new Color(35, 67, 109);
    /** Azul médio: item de menu selecionado e anel de foco. */
    public static final Color SECUNDARIA = new Color(68, 115, 158);
    /** Fundo das telas, atrás dos cartões. */
    public static final Color FUNDO = new Color(244, 247, 251);
    /** Fundo dos cartões e da barra de status. */
    public static final Color SUPERFICIE = Color.WHITE;
    /** Cor do texto principal. */
    public static final Color TEXTO = new Color(31, 44, 59);
    /** Cor de textos de apoio, menos importantes. */
    public static final Color TEXTO_SECUNDARIO = new Color(95, 110, 126);
    /** Verde de sucesso (barra de status, veículo disponível). */
    public static final Color SUCESSO = new Color(30, 122, 84);
    /** Vermelho de erro (mensagens de validação, botão de exclusão). */
    public static final Color ERRO = new Color(181, 48, 53);
    /** Amarelo-escuro de aviso (veículo em manutenção). */
    public static final Color AVISO = new Color(160, 103, 20);
    /** Cinza neutro (veículo vendido). */
    public static final Color NEUTRO = new Color(108, 117, 125);
    /** Contorno de cartões e divisórias (ex.: barra de status). */
    public static final Color BORDA = new Color(221, 228, 237);
    /** Texto sobre fundo escuro: menu lateral e botões primário e de perigo. */
    public static final Color TEXTO_SOBRE_ESCURO = Color.WHITE;
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

    /** Configura os padrões do FlatLaf antes de criar os componentes. */
    public static void aplicar() {
        UIManager.put("defaultFont", TEXTO_FONTE);
        UIManager.put("Component.focusColor", SECUNDARIA);
        // O padrão do FlatLaf Light (focusWidth 0) quase não mostra o foco em botões com fundo próprio;
        // um anel de 2px deixa visível quem está com o foco ao navegar pelo teclado.
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Panel.background", FUNDO);
    }
}
