package apresentacao.estilo;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;

/** Identidade visual compartilhada pelas telas da aplicação. */
public final class Tema {
    public static final Color PRIMARIA = new Color(35, 67, 109);
    public static final Color SECUNDARIA = new Color(68, 115, 158);
    public static final Color FUNDO = new Color(244, 247, 251);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color TEXTO = new Color(31, 44, 59);
    public static final Color TEXTO_SECUNDARIO = new Color(95, 110, 126);
    public static final Color SUCESSO = new Color(30, 122, 84);
    public static final Color ERRO = new Color(181, 48, 53);
    public static final Color AVISO = new Color(160, 103, 20);
    /** Contorno de cartões e divisórias (ex.: barra de status). */
    public static final Color BORDA = new Color(221, 228, 237);
    public static final int PEQUENO = 8, MEDIO = 16, GRANDE = 24, MARGEM_TELA = 32;
    public static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    public static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 17);
    public static final Font TEXTO_FONTE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font ROTULO = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font BOTAO = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    private Tema() { }

    /** Configura os padrões do FlatLaf antes de criar os componentes. */
    public static void aplicar() {
        UIManager.put("defaultFont", TEXTO_FONTE);
        UIManager.put("Component.focusColor", SECUNDARIA);
        UIManager.put("Component.arc", 12);
        UIManager.put("Button.arc", 12);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("Panel.background", FUNDO);
    }
}
