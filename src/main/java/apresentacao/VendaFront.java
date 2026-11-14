package apresentacao;

import aplicacao.dto.DadosCartao;
import aplicacao.dto.DadosDinheiro;
import aplicacao.dto.DadosPagamento;
import aplicacao.dto.DadosPix;
import aplicacao.dto.DadosVenda;
import aplicacao.servico.ClienteService;
import aplicacao.servico.VeiculoService;
import aplicacao.servico.VendaService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Cliente;
import dominio.modelo.Pagamento;
import dominio.modelo.PagamentoCartao;
import dominio.modelo.ResumoVenda;
import dominio.modelo.Veiculo;
import dominio.modelo.Vendedor;
import dominio.politica.DescontoPix;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Registro e consulta de vendas da concessionária. */
public class VendaFront extends JPanel implements TelaComDados<VendaFront.Dados>, TelaComBotaoPrincipal {
    private enum Forma {
        PIX("PIX"), CARTAO("Cartão"), DINHEIRO("Dinheiro");
        private final String nome;
        Forma(String nome) { this.nome = nome; }
        @Override public String toString() { return nome; }
    }

    record Dados(List<Cliente> clientes, List<Vendedor> vendedores,
                 List<Veiculo> veiculos, List<ResumoVenda> vendas) { }

    private final VendaService vendaService;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    private final JComboBox<Cliente> clientes = Componentes.combo(new Cliente[0]);
    private final JComboBox<Veiculo> veiculos = Componentes.combo(new Veiculo[0]);
    private final JComboBox<Vendedor> vendedores = Componentes.combo(new Vendedor[0]);
    private final JComboBox<Forma> formas = Componentes.combo(Forma.values());
    private final JTextField buscaCliente = Componentes.campoTexto();
    private final JTextField chavePix = Componentes.campoTexto();
    private final JComboBox<String> tipoChave = Componentes.combo(new String[]{"CPF", "CNPJ", "Email", "Telefone"});
    private final JTextField numero = Componentes.campoTexto();
    private final JTextField titular = Componentes.campoTexto();
    private final JTextField validade = Componentes.campoTexto();
    private final JPasswordField cvv = new JPasswordField();
    private final JSpinner parcelas = new JSpinner(new SpinnerNumberModel(1, 1, PagamentoCartao.MAX_PARCELAS, 1));
    private final JLabel resumo = Componentes.rotulo("Selecione um veículo para ver os valores.");
    private final JPanel camposPagamento = new JPanel(new CardLayout());
    private final JButton finalizar = Componentes.botaoPrimario("Finalizar venda");
    private final DefaultTableModel historico = new DefaultTableModel(
            new String[]{"Data", "Cliente", "Veículo", "Vendedor", "Pagamento", "Valor"}, 0) {
        @Override public boolean isCellEditable(int linha, int coluna) { return false; }
    };
    private List<Cliente> todosClientes = List.of();

    public VendaFront(VendaService vendaService, ClienteService clienteService, VeiculoService veiculoService) {
        this.vendaService = vendaService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        setLayout(new BorderLayout());
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Nova venda", criarVenda());
        abas.addTab("Histórico", criarHistorico());
        add(abas, BorderLayout.CENTER);
        finalizar.setMnemonic(KeyEvent.VK_F);
        finalizar.addActionListener(e -> finalizarVenda());
        veiculos.addActionListener(e -> atualizarResumo());
        formas.addActionListener(e -> {
            Forma forma = (Forma) formas.getSelectedItem();
            if (forma != null) ((CardLayout) camposPagamento.getLayout()).show(camposPagamento, forma.name());
            atualizarResumo();
        });
        parcelas.addChangeListener(e -> atualizarResumo());
        buscaCliente.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });
        veiculos.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                    boolean selecionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
                if (valor instanceof Veiculo veiculo) setText(veiculo + " — " + moeda.format(veiculo.getPreco()));
                return this;
            }
        });
    }

    private JPanel criarVenda() {
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        buscaCliente.putClientProperty("JTextField.placeholderText", "Buscar cliente por nome ou CPF");
        JPanel cliente = Componentes.painelFormulario();
        Componentes.adicionarCampo(cliente, 0, "Buscar", buscaCliente);
        Componentes.adicionarCampo(cliente, 1, "Cliente", clientes);
        adicionarCartao(conteudo, "1. Cliente", cliente);
        JPanel veiculo = Componentes.painelFormulario();
        Componentes.adicionarCampo(veiculo, 0, "Veículo disponível", veiculos);
        adicionarCartao(conteudo, "2. Veículo", veiculo);
        JPanel vendedor = Componentes.painelFormulario();
        Componentes.adicionarCampo(vendedor, 0, "Vendedor", vendedores);
        adicionarCartao(conteudo, "3. Vendedor", vendedor);

        JPanel pix = Componentes.painelFormulario();
        Componentes.adicionarCampo(pix, 0, "Tipo de chave", tipoChave);
        Componentes.adicionarCampo(pix, 1, "Chave PIX", chavePix);
        JPanel cartao = Componentes.painelFormulario();
        Componentes.adicionarCampo(cartao, 0, "Número", numero);
        Componentes.adicionarCampo(cartao, 1, "Titular", titular);
        Componentes.adicionarCampo(cartao, 2, "Validade (MM/AA)", validade);
        Componentes.adicionarCampo(cartao, 3, "CVV", cvv);
        Componentes.adicionarCampo(cartao, 4, "Parcelas", parcelas);
        camposPagamento.setOpaque(false);
        camposPagamento.add(pix, Forma.PIX.name());
        camposPagamento.add(cartao, Forma.CARTAO.name());
        JPanel dinheiro = new JPanel(new BorderLayout());
        dinheiro.setOpaque(false);
        dinheiro.add(Componentes.rotulo("Pagamento à vista, sem desconto. Não há dados a preencher."), BorderLayout.NORTH);
        camposPagamento.add(dinheiro, Forma.DINHEIRO.name());
        JPanel pagamento = new JPanel(new BorderLayout(0, Tema.PEQUENO));
        pagamento.setOpaque(false);
        JPanel escolha = Componentes.painelFormulario();
        Componentes.adicionarCampo(escolha, 0, "Forma de pagamento", formas);
        pagamento.add(escolha, BorderLayout.NORTH);
        pagamento.add(camposPagamento, BorderLayout.CENTER);
        adicionarCartao(conteudo, "4. Pagamento", pagamento);
        JPanel painelResumo = new JPanel(new BorderLayout(Tema.MEDIO, Tema.MEDIO));
        painelResumo.setOpaque(false);
        painelResumo.add(resumo, BorderLayout.CENTER);
        painelResumo.add(finalizar, BorderLayout.EAST);
        adicionarCartao(conteudo, "Resumo da venda", painelResumo);

        JScrollPane rolagem = new JScrollPane(conteudo);
        rolagem.setBorder(null);
        rolagem.getVerticalScrollBar().setUnitIncrement(20);
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(rolagem, BorderLayout.CENTER);
        return painel;
    }

    private void adicionarCartao(JPanel destino, String titulo, JPanel conteudo) {
        JPanel cartao = Componentes.painelCartao(titulo);
        cartao.add(conteudo, BorderLayout.CENTER);
        cartao.setAlignmentX(Component.LEFT_ALIGNMENT);
        cartao.setMaximumSize(new Dimension(Integer.MAX_VALUE, cartao.getPreferredSize().height));
        destino.add(cartao);
        destino.add(Box.createVerticalStrut(Tema.PEQUENO));
    }

    private JPanel criarHistorico() {
        JPanel cartao = Componentes.painelCartao("Vendas realizadas");
        JTable tabela = new JTable(historico);
        tabela.setFillsViewportHeight(true);
        tabela.setRowHeight(28);
        tabela.setAutoCreateRowSorter(true);
        cartao.add(new JScrollPane(tabela), BorderLayout.CENTER);
        return cartao;
    }

    @Override public JButton botaoPrincipal() { return finalizar; }

    @Override public Dados carregarDados() {
        return new Dados(clienteService.listar(), vendaService.listarVendedores(),
                veiculoService.listarDisponiveis(), vendaService.listarVendas());
    }

    @Override public void exibirDados(Dados dados) {
        todosClientes = List.copyOf(dados.clientes());
        filtrarClientes();
        vendedores.setModel(new DefaultComboBoxModel<>(dados.vendedores().toArray(new Vendedor[0])));
        veiculos.setModel(new DefaultComboBoxModel<>(dados.veiculos().toArray(new Veiculo[0])));
        historico.setRowCount(0);
        DateTimeFormatter data = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (ResumoVenda venda : dados.vendas()) {
            historico.addRow(new Object[]{data.format(venda.data()), venda.cliente(), venda.veiculo(),
                    venda.vendedor(), venda.formaPagamento(), moeda.format(venda.valor())});
        }
        atualizarResumo();
    }

    private void filtrarClientes() {
        Cliente anterior = (Cliente) clientes.getSelectedItem();
        String termo = buscaCliente.getText().strip().toLowerCase(Locale.ROOT);
        List<Cliente> encontrados = todosClientes.stream()
                .filter(c -> c.getNome().toLowerCase(Locale.ROOT).contains(termo) || c.getCpf().contains(termo))
                .toList();
        clientes.setModel(new DefaultComboBoxModel<>(encontrados.toArray(new Cliente[0])));
        if (anterior != null && encontrados.contains(anterior)) clientes.setSelectedItem(anterior);
    }

    private void atualizarResumo() {
        Veiculo veiculo = (Veiculo) veiculos.getSelectedItem();
        if (veiculo == null) {
            resumo.setText("Selecione um veículo para ver os valores.");
            return;
        }
        BigDecimal preco = BigDecimal.valueOf(veiculo.getPreco());
        Forma forma = (Forma) formas.getSelectedItem();
        boolean pix = forma == Forma.PIX;
        BigDecimal desconto = pix ? preco.multiply(BigDecimal.valueOf(DescontoPix.PERCENTUAL))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal total = preco.subtract(desconto);
        int quantidade = (Integer) parcelas.getValue();
        String detalhe = forma != Forma.CARTAO ? "" : "  |  " + quantidade + "x de "
                + moeda.format(total.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP));
        resumo.setText("<html>Preço: " + moeda.format(preco) + " &nbsp; | &nbsp; Desconto: "
                + moeda.format(desconto) + " &nbsp; | &nbsp; Total: " + moeda.format(total) + detalhe + "</html>");
    }

    private void finalizarVenda() {
        Cliente cliente = (Cliente) clientes.getSelectedItem();
        Veiculo veiculo = (Veiculo) veiculos.getSelectedItem();
        Vendedor vendedor = (Vendedor) vendedores.getSelectedItem();
        if (cliente == null || veiculo == null || vendedor == null) {
            Feedback.erro(this, new IllegalArgumentException("Selecione cliente, veículo e vendedor."));
            return;
        }
        DadosPagamento pagamento = switch ((Forma) formas.getSelectedItem()) {
            case PIX -> new DadosPix(chavePix.getText(), (String) tipoChave.getSelectedItem());
            case CARTAO -> new DadosCartao(numero.getText(), titular.getText(), validade.getText(),
                    new String(cvv.getPassword()), (Integer) parcelas.getValue());
            case DINHEIRO -> new DadosDinheiro();
        };
        DadosVenda dados = new DadosVenda(cliente.getIdCliente(), vendedor.getIdVendedor(),
                veiculo.getIdVeiculo(), pagamento);
        TarefaEmSegundoPlano.executar(finalizar, () -> vendaService.vender(dados), this::vendaConcluida,
                erro -> Feedback.erro(this, erro));
    }

    private void vendaConcluida(Pagamento pagamento) {
        mostrarRecibo(pagamento.getVenda().obterRecibo());
        chavePix.setText(""); numero.setText(""); titular.setText(""); validade.setText(""); cvv.setText("");
        parcelas.setValue(1);
        TarefaEmSegundoPlano.executar(this::carregarDados, this::exibirDados, erro -> Feedback.erro(this, erro));
    }

    private void mostrarRecibo(String texto) {
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), "Recibo da venda", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel conteudo = Componentes.painelCartao("Venda realizada com sucesso");
        JTextArea recibo = new JTextArea(texto);
        recibo.setEditable(false);
        recibo.setLineWrap(true);
        recibo.setWrapStyleWord(true);
        recibo.setFont(Tema.TEXTO_FONTE);
        recibo.setBackground(Tema.SUPERFICIE);
        conteudo.add(new JScrollPane(recibo), BorderLayout.CENTER);
        JButton fechar = Componentes.botaoPrimario("Fechar");
        fechar.addActionListener(e -> dialogo.dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setOpaque(false);
        rodape.add(fechar);
        conteudo.add(rodape, BorderLayout.SOUTH);
        dialogo.add(conteudo);
        dialogo.getRootPane().setDefaultButton(fechar);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialogo.setSize(480, 420);
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
        dialogo.dispose();
    }
}
