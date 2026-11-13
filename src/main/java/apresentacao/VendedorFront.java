package apresentacao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import aplicacao.servico.VendedorService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.comum.Validacao;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Concessionaria;
import dominio.modelo.Vendedor;

/**
 * Cadastro de vendedores: lista com busca à esquerda, formulário à direita.
 * Clicar numa linha carrega o vendedor no formulário (modo edição); "Novo" volta ao modo cadastro.
 */
public class VendedorFront extends JPanel
        implements TelaComDados<VendedorFront.Dados>, TelaComBotaoPrincipal {

    private final VendedorService vendedorService;
    private final VendedorTableModel modeloTabela = new VendedorTableModel();
    private final TableRowSorter<VendedorTableModel> ordenador = new TableRowSorter<>(modeloTabela);

    private JTable tabela;
    private JTextField campoBusca;
    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoComissao;
    private JComboBox<Concessionaria> comboConcessionaria;
    private JButton botaoNovo;
    private JButton botaoSalvar;
    private JButton botaoExcluir;

    /** null = modo cadastro; preenchido = modo edição do vendedor com esse ID. */
    private Long idEmEdicao;

    /**
     * Monta a tela. Os dados chegam pelo {@link #carregarDados()}, chamado pela {@code TelaPrincipal}
     * em segundo plano quando o usuário abre esta aba.
     *
     * @param vendedorService serviço usado para listar, cadastrar, atualizar e excluir
     */
    public VendedorFront(VendedorService vendedorService) {
        this.vendedorService = vendedorService;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(
                Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;

        c.gridx = 0;
        c.weightx = 0.6;
        c.insets = new Insets(0, 0, 0, Tema.MEDIO);
        add(criarPainelLista(), c);

        c.gridx = 1;
        c.weightx = 0.4;
        c.insets = new Insets(0, 0, 0, 0);
        add(criarPainelFormulario(), c);

        novo();
    }

    // ---------------------------------------------------------------- dados

    /**
     * O que esta tela busca no banco.
     *
     * @param concessionarias opções do combo de concessionária
     * @param vendedores      linhas da tabela
     */
    record Dados(List<Concessionaria> concessionarias, List<Vendedor> vendedores) { }

    /**
     * Busca concessionárias e vendedores. Roda fora da thread da tela.
     *
     * @return concessionárias e vendedores atuais
     */
    @Override
    public Dados carregarDados() {
        return new Dados(vendedorService.listarConcessionarias(), vendedorService.listar());
    }

    /**
     * Preenche o combo de concessionárias (mantendo a escolhida, se ainda existir) e a tabela.
     * A troca da lista limpa a seleção, então a tela volta ao modo cadastro.
     *
     * @param dados resultado de {@link #carregarDados()}
     */
    @Override
    public void exibirDados(Dados dados) {
        Concessionaria escolhida = (Concessionaria) comboConcessionaria.getSelectedItem();
        comboConcessionaria.setModel(new DefaultComboBoxModel<>(
                dados.concessionarias().toArray(new Concessionaria[0])));
        selecionarConcessionaria(escolhida == null ? null : escolhida.getIdConcessionaria());

        modeloTabela.setVendedores(dados.vendedores());
        if (idEmEdicao != null) {
            novo();
        }
    }

    /** Recarrega concessionárias e vendedores em segundo plano. */
    private void recarregar() {
        TarefaEmSegundoPlano.executar(this::carregarDados, this::exibirDados, erro -> Feedback.erro(this, erro));
    }

    // ---------------------------------------------------------------- montagem da tela

    private JPanel criarPainelLista() {
        JPanel cartao = Componentes.painelCartao("Vendedores");

        campoBusca = Componentes.campoTexto();
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou CPF");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        tabela = new JTable(modeloTabela);
        tabela.setRowSorter(ordenador);
        tabela.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.TRAILING);
            }

            @Override
            protected void setValue(Object valor) {
                setText(valor instanceof Double comissao ? VendedorTableModel.formatarComissao(comissao) : "");
            }
        });
        tabela.setFont(Tema.TEXTO_FONTE);
        tabela.setRowHeight(28);
        tabela.setGridColor(Tema.BORDA);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(Tema.BOTAO);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionado();
            }
        });

        JPanel conteudo = new JPanel(new BorderLayout(0, Tema.PEQUENO));
        conteudo.setOpaque(false);
        conteudo.add(campoBusca, BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelFormulario() {
        JPanel cartao = Componentes.painelCartao("Dados do vendedor");

        campoNome = Componentes.campoTexto();
        campoCpf = Componentes.campoTexto();
        campoComissao = Componentes.campoTexto();
        campoComissao.putClientProperty("JTextField.placeholderText", "Ex.: 2,5");
        comboConcessionaria = Componentes.combo(new Concessionaria[0]);

        JPanel formulario = Componentes.painelFormulario();
        Componentes.adicionarCampo(formulario, 0, "Nome:", campoNome);
        Componentes.adicionarCampo(formulario, 1, "CPF:", campoCpf);
        Componentes.adicionarCampo(formulario, 2, "Comissão (%):", campoComissao);
        Componentes.adicionarCampo(formulario, 3, "Concessionária:", comboConcessionaria);

        botaoNovo = Componentes.botaoSecundario("Novo");
        botaoExcluir = Componentes.botaoPerigo("Excluir");
        botaoSalvar = Componentes.botaoPrimario("Salvar");
        // Alt+letra: C, V, E, O e D são do menu lateral; N, S e X seguem a tela de clientes.
        botaoNovo.setMnemonic('N');
        botaoSalvar.setMnemonic('S');
        botaoExcluir.setMnemonic('X');
        botaoNovo.addActionListener(e -> novo());
        botaoExcluir.addActionListener(e -> excluir());
        botaoSalvar.addActionListener(e -> salvar());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, Tema.PEQUENO, 0));
        botoes.setOpaque(false);
        botoes.add(botaoNovo);
        botoes.add(botaoExcluir);
        botoes.add(botaoSalvar);

        JPanel conteudo = new JPanel(new BorderLayout(0, Tema.MEDIO));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(botoes, BorderLayout.SOUTH);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    /**
     * O Enter nesta tela salva o vendedor (cadastra ou atualiza).
     *
     * @return o botão "Salvar"
     */
    @Override
    public JButton botaoPrincipal() {
        return botaoSalvar;
    }

    // ---------------------------------------------------------------- busca

    /** Filtra a lista por nome ou CPF enquanto o usuário digita (o CPF aceita pontos e traço). */
    private void aplicarFiltro() {
        String texto = campoBusca.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
            return;
        }
        String digitos = texto.replaceAll("\\D", "");
        ordenador.setRowFilter(new RowFilter<VendedorTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends VendedorTableModel, ? extends Integer> entrada) {
                Vendedor vendedor = entrada.getModel().getVendedor(entrada.getIdentifier());
                return vendedor.getNome().toLowerCase().contains(texto)
                        || (!digitos.isEmpty() && vendedor.getCpf().contains(digitos));
            }
        });
    }

    // ---------------------------------------------------------------- fluxo da tela

    /** Limpa o formulário e volta ao modo cadastro. */
    private void novo() {
        idEmEdicao = null;
        tabela.clearSelection();
        campoNome.setText("");
        campoCpf.setText("");
        campoComissao.setText("");
        limparErros();
        botaoExcluir.setEnabled(false);
        campoNome.requestFocusInWindow();
    }

    /** Clique numa linha: carrega o vendedor no formulário e entra em modo edição. */
    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Vendedor vendedor = modeloTabela.getVendedor(tabela.convertRowIndexToModel(linha));
        idEmEdicao = vendedor.getIdVendedor();
        limparErros();
        campoNome.setText(vendedor.getNome());
        campoCpf.setText(ClienteTableModel.formatarCpf(vendedor.getCpf()));
        campoComissao.setText(VendedorTableModel.formatarComissao(vendedor.getComissaoPercentual()));
        selecionarConcessionaria(vendedor.getConcessionaria().getIdConcessionaria());
        botaoExcluir.setEnabled(true);
    }

    private void salvar() {
        boolean valido = Validacao.verificar(campoNome, () -> Vendedor.validarNome(campoNome.getText()));
        valido &= Validacao.verificar(campoCpf, () -> Vendedor.validarCpf(campoCpf.getText()));
        valido &= Validacao.verificar(campoComissao,
                () -> Vendedor.validarComissao(converterComissao(campoComissao.getText())));
        valido &= Validacao.verificar(comboConcessionaria, () -> {
            if (comboConcessionaria.getSelectedItem() == null) {
                throw new IllegalArgumentException("Cadastre uma concessionária antes de cadastrar um vendedor.");
            }
        });
        if (!valido) {
            return;
        }

        // Lê os campos aqui, na thread da tela: o trabalho em segundo plano não pode mexer em componentes.
        Long id = idEmEdicao;
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        Double comissao = converterComissao(campoComissao.getText());
        Concessionaria concessionaria = (Concessionaria) comboConcessionaria.getSelectedItem();

        TarefaEmSegundoPlano.executar(botaoSalvar,
                () -> id == null
                        ? vendedorService.cadastrar(nome, cpf, comissao, concessionaria)
                        : vendedorService.atualizar(id, nome, cpf, comissao, concessionaria),
                vendedor -> {
                    Feedback.sucesso(this, id == null
                            ? "Vendedor cadastrado com sucesso."
                            : "Vendedor atualizado com sucesso.");
                    novo();
                    recarregar();
                },
                erro -> Feedback.erro(this, erro));
    }

    private void excluir() {
        Long id = idEmEdicao;
        if (id == null) {
            return;
        }
        if (!Feedback.confirmar(this, "Excluir o vendedor " + campoNome.getText().trim() + "?")) {
            return;
        }

        TarefaEmSegundoPlano.executar(botaoExcluir,
                () -> {
                    vendedorService.excluir(id);
                    return id;
                },
                excluido -> {
                    Feedback.sucesso(this, "Vendedor excluído com sucesso.");
                    novo();
                    recarregar();
                },
                erro -> Feedback.erro(this, erro));
    }

    // ---------------------------------------------------------------- conversões e apoio

    /**
     * Converte a comissão digitada. Aceita vírgula ou ponto e um "%" no fim: "2,5", "2.5", "2,5%".
     *
     * @throws IllegalArgumentException se não for um número
     */
    private static Double converterComissao(String texto) {
        String limpo = texto.replace("%", "").replaceAll("\\s", "").replace(",", ".");
        try {
            return Double.valueOf(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Comissão deve ser um número, ex.: 2,5.");
        }
    }

    /** Seleciona no combo a concessionária com esse ID (ou a primeira, se não achar). */
    private void selecionarConcessionaria(Long id) {
        for (int i = 0; i < comboConcessionaria.getItemCount(); i++) {
            if (comboConcessionaria.getItemAt(i).getIdConcessionaria().equals(id)) {
                comboConcessionaria.setSelectedIndex(i);
                return;
            }
        }
        if (comboConcessionaria.getItemCount() > 0) {
            comboConcessionaria.setSelectedIndex(0);
        }
    }

    private void limparErros() {
        Validacao.limpar(campoNome);
        Validacao.limpar(campoCpf);
        Validacao.limpar(campoComissao);
        Validacao.limpar(comboConcessionaria);
    }
}
