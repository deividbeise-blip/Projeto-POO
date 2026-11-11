package apresentacao;

import apresentacao.comum.BarraStatus;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import com.formdev.flatlaf.FlatLightLaf;
import config.AppConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/** Janela principal e navegação entre as áreas da concessionária. */
public class TelaPrincipal extends JFrame {
    private final CardLayout layoutTelas = new CardLayout();
    private final JPanel telas = new JPanel(layoutTelas);
    private final JLabel titulo = Componentes.titulo("Clientes");
    private final Map<String, JButton> botoesMenu = new LinkedHashMap<>();
    private final VeiculoFront veiculos;
    private final ConcessionariaFront vendas;

    public TelaPrincipal(AppConfig config) {
        setTitle("Concessionária");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        setSize(1280, 720);
        setLocationRelativeTo(null);
        ClienteFront clientes = new ClienteFront(config.clienteService());
        veiculos = new VeiculoFront(config.veiculoService());
        vendas = new ConcessionariaFront(config.vendaService(), config.clienteService(), config.veiculoService());
        telas.setOpaque(false);
        telas.add(clientes, "Clientes");
        telas.add(veiculos, "Veículos");
        telas.add(vendas, "Vendas");
        telas.add(painelEmBreve("Concessionárias"), "Concessionárias");
        telas.add(painelEmBreve("Vendedores"), "Vendedores");
        JPanel conteudo = new JPanel(new BorderLayout(0, Tema.GRANDE));
        conteudo.setBackground(Tema.FUNDO);
        conteudo.setBorder(new EmptyBorder(Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA));
        conteudo.add(titulo, BorderLayout.NORTH);
        conteudo.add(telas, BorderLayout.CENTER);
        add(criarMenu(), BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        add(new BarraStatus(), BorderLayout.SOUTH);
        atualizarSelecao("Clientes");
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
        for (String nome : new String[]{"Clientes", "Veículos", "Vendas", "Concessionárias", "Vendedores"}) {
            JButton botao = Componentes.botaoSecundario(nome);
            botao.setHorizontalAlignment(SwingConstants.LEFT);
            botao.setAlignmentX(Component.LEFT_ALIGNMENT);
            botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            botao.addActionListener(e -> navegar(nome));
            botoesMenu.put(nome, botao);
            menu.add(botao);
            menu.add(Box.createVerticalStrut(Tema.PEQUENO));
        }
        return menu;
    }

    private JPanel painelEmBreve(String nome) {
        JPanel cartao = Componentes.painelCartao(nome);
        JLabel mensagem = Componentes.rotulo("Tela em desenvolvimento.");
        mensagem.setForeground(Tema.TEXTO_SECUNDARIO);
        cartao.add(mensagem, BorderLayout.CENTER);
        return cartao;
    }

    private void navegar(String nome) {
        try {
            if ("Veículos".equals(nome)) veiculos.atualizarDados();
            if ("Vendas".equals(nome)) vendas.atualizarDados();
            layoutTelas.show(telas, nome);
            titulo.setText(nome);
            atualizarSelecao(nome);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Não foi possível carregar os dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarSelecao(String nome) {
        botoesMenu.forEach((chave, botao) -> {
            botao.setBackground(chave.equals(nome) ? Tema.SECUNDARIA : Tema.PRIMARIA);
            botao.setForeground(Color.WHITE);
        });
    }

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
