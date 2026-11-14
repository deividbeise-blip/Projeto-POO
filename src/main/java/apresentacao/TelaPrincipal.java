package apresentacao;

import apresentacao.comum.BarraStatus;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import com.formdev.flatlaf.FlatLightLaf;
import config.AppConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Janela principal: menu lateral com as cinco áreas (Clientes, Veículos, Vendas, Concessionárias,
 * Vendedores), título da área atual e barra de status no rodapé.
 * Atalhos do menu: Alt+C, Alt+V, Alt+E, Alt+O e Alt+D. Ao trocar de área, os dados dela são
 * recarregados em segundo plano e o Enter passa a acionar o botão principal daquela tela.
 */
public class TelaPrincipal extends JFrame {
    private final CardLayout layoutTelas = new CardLayout();
    private final JPanel telas = new JPanel(layoutTelas);
    private final JLabel titulo = Componentes.titulo("Clientes");
    private final Map<String, JButton> botoesMenu = new LinkedHashMap<>();
    private final Map<String, Component> telasPorNome = new LinkedHashMap<>();

    /**
     * Monta a janela e todas as telas com os services do {@link AppConfig}. Chame na thread da tela.
     *
     * @param config fornece os casos de uso já montados com suas dependências
     */
    public TelaPrincipal(AppConfig config) {
        setTitle("Concessionária");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        setSize(1280, 720);
        setLocationRelativeTo(null);
        ClienteFront clientes = new ClienteFront(config.clienteService());
        VeiculoFront veiculos = new VeiculoFront(config.veiculoService());
        VendaFront vendas = new VendaFront(config.vendaService(), config.clienteService(), config.veiculoService());
        telas.setOpaque(false);
        adicionarTela("Clientes", clientes);
        adicionarTela("Veículos", veiculos);
        adicionarTela("Vendas", vendas);
        adicionarTela("Concessionárias", new ConcessionariaFront(config.concessionariaService()));
        adicionarTela("Vendedores", new VendedorFront(config.vendedorService()));
        JPanel conteudo = new JPanel(new BorderLayout(0, Tema.GRANDE));
        conteudo.setBackground(Tema.FUNDO);
        conteudo.setBorder(new EmptyBorder(Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA));
        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(telas, BorderLayout.CENTER);
        add(criarMenu(), BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        add(new BarraStatus(), BorderLayout.SOUTH);
        mostrarTela("Clientes");
    }

    private JPanel criarMenu() {
        JPanel menu = new JPanel();
        menu.setBackground(Tema.PRIMARIA);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(Tema.MARGEM_TELA, Tema.MEDIO, Tema.GRANDE, Tema.MEDIO));
        menu.setPreferredSize(new Dimension(240, 0));
        JLabel marca = new JLabel("CONCESSIONÁRIA");
        marca.setForeground(Color.WHITE);
        marca.setFont(Tema.SUBTITULO);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        menu.add(marca);
        menu.add(Box.createVerticalStrut(Tema.MARGEM_TELA));
        Map<String, Character> atalhos = new LinkedHashMap<>();
        atalhos.put("Clientes", 'C');
        atalhos.put("Veículos", 'V');
        atalhos.put("Vendas", 'E');
        atalhos.put("Concessionárias", 'O');
        atalhos.put("Vendedores", 'D');
        for (Map.Entry<String, Character> item : atalhos.entrySet()) {
            String nome = item.getKey();
            JButton botao = Componentes.botaoMenu(nome, item.getValue());
            botao.addActionListener(e -> navegar(nome));
            botoesMenu.put(nome, botao);
            menu.add(botao);
            menu.add(Box.createVerticalStrut(Tema.PEQUENO));
        }
        return menu;
    }

    /** Troca de tela na hora; os dados dela chegam em segundo plano, sem travar a janela. */
    private void navegar(String nome) {
        mostrarTela(nome);
        if (telasPorNome.get(nome) instanceof TelaComDados<?> tela) {
            carregar(tela);
        }
    }

    private <T> void carregar(TelaComDados<T> tela) {
        TarefaEmSegundoPlano.executar(tela::carregarDados, tela::exibirDados, erro -> Feedback.erro(this, erro));
    }

    /** Troca a tela visível e faz o Enter acionar o botão principal dela (se tiver). */
    private void mostrarTela(String nome) {
        layoutTelas.show(telas, nome);
        titulo.setText(nome);
        atualizarSelecao(nome);
        Component tela = telasPorNome.get(nome);
        getRootPane().setDefaultButton(
                tela instanceof TelaComBotaoPrincipal comBotao ? comBotao.botaoPrincipal() : null);
    }

    private void adicionarTela(String nome, Component tela) {
        telas.add(tela, nome);
        telasPorNome.put(nome, tela);
    }

    private void atualizarSelecao(String nome) {
        botoesMenu.forEach((chave, botao) -> {
            botao.setBackground(chave.equals(nome) ? Tema.SECUNDARIA : Tema.PRIMARIA);
            botao.setForeground(Color.WHITE);
        });
    }

    /**
     * Ponto de entrada do sistema: aplica o FlatLaf e o {@link Tema}, monta o {@link AppConfig}
     * e abre a janela. Se a montagem falhar (ex.: DB_URL, DB_USER ou DB_PASSWORD não definidas),
     * mostra o motivo num diálogo em vez de fechar sem aviso.
     *
     * @param args não usados
     */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        Tema.aplicar();
        SwingUtilities.invokeLater(() -> {
            try {
                new TelaPrincipal(new AppConfig()).setVisible(true);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, "Não foi possível iniciar: " + ex.getMessage(),
                        "Erro ao iniciar", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
