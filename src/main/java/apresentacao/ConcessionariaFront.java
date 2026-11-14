package apresentacao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import aplicacao.servico.ConcessionariaService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.comum.Validacao;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Concessionaria;

/**
 * Cadastro de concessionárias: lista com busca à esquerda, formulário à direita.
 * (A tela de vendas se chama {@code VendaFront}; esta é a de cadastro das lojas.)
 * Clicar numa linha carrega a concessionária no formulário (modo edição); "Novo" volta ao modo cadastro.
 */
public class ConcessionariaFront extends JPanel
        implements TelaComDados<List<Concessionaria>>, TelaComBotaoPrincipal {

    private final ConcessionariaService concessionariaService;
    private final ConcessionariaTableModel modeloTabela = new ConcessionariaTableModel();
    private final TableRowSorter<ConcessionariaTableModel> ordenador = new TableRowSorter<>(modeloTabela);

    private JTable tabela;
    private JTextField campoBusca;
    private JTextField campoNome;
    private JTextField campoEndereco;
    private JButton botaoNovo;
    private JButton botaoSalvar;
    private JButton botaoExcluir;

    /** null = modo cadastro; preenchido = modo edição da concessionária com esse ID. */
    private Long idEmEdicao;

    /**
     * Monta a tela. A lista chega pelo {@link #carregarDados()}, chamado pela {@code TelaPrincipal}
     * em segundo plano quando o usuário abre esta aba.
     *
     * @param concessionariaService serviço usado para listar, cadastrar, atualizar e excluir
     */
    public ConcessionariaFront(ConcessionariaService concessionariaService) {
        this.concessionariaService = concessionariaService;

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
     * Busca as concessionárias. Roda fora da thread da tela.
     *
     * @return as concessionárias atuais
     */
    @Override
    public List<Concessionaria> carregarDados() {
        return concessionariaService.listar();
    }

    /**
     * Mostra a lista na tabela. A troca da lista limpa a seleção, então a tela volta ao modo cadastro.
     *
     * @param concessionarias resultado de {@link #carregarDados()}
     */
    @Override
    public void exibirDados(List<Concessionaria> concessionarias) {
        modeloTabela.setConcessionarias(concessionarias);
        if (idEmEdicao != null) {
            novo();
        }
    }

    /** Recarrega a lista do banco em segundo plano. */
    private void recarregar() {
        TarefaEmSegundoPlano.executar(this::carregarDados, this::exibirDados, erro -> Feedback.erro(this, erro));
    }

    // ---------------------------------------------------------------- montagem da tela

    private JPanel criarPainelLista() {
        JPanel cartao = Componentes.painelCartao("Concessionárias");

        campoBusca = Componentes.campoTexto();
        campoBusca.getAccessibleContext().setAccessibleName("Buscar concessionárias");
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou endereço");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        tabela = new JTable(modeloTabela);
        tabela.getAccessibleContext().setAccessibleName("Lista de concessionárias");
        tabela.setRowSorter(ordenador);
        tabela.setFont(Tema.TEXTO_FONTE);
        tabela.setRowHeight(Tema.ALTURA_LINHA_TABELA);
        tabela.setGridColor(Tema.BORDA);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(Tema.BOTAO);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                carregarSelecionada();
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
        JPanel cartao = Componentes.painelCartao("Dados da concessionária");

        campoNome = Componentes.campoTexto();
        campoEndereco = Componentes.campoTexto();

        JPanel formulario = Componentes.painelFormulario();
        Componentes.adicionarCampo(formulario, 0, "Nome:", campoNome);
        Componentes.adicionarCampo(formulario, 1, "Endereço:", campoEndereco);

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
     * O Enter nesta tela salva a concessionária (cadastra ou atualiza).
     *
     * @return o botão "Salvar"
     */
    @Override
    public JButton botaoPrincipal() {
        return botaoSalvar;
    }

    // ---------------------------------------------------------------- busca

    /** Filtra a lista por nome ou endereço enquanto o usuário digita. */
    private void aplicarFiltro() {
        String texto = campoBusca.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
            return;
        }
        ordenador.setRowFilter(new RowFilter<ConcessionariaTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ConcessionariaTableModel, ? extends Integer> entrada) {
                Concessionaria concessionaria = entrada.getModel().getConcessionaria(entrada.getIdentifier());
                return concessionaria.getNome().toLowerCase().contains(texto)
                        || concessionaria.getEndereco().toLowerCase().contains(texto);
            }
        });
    }

    // ---------------------------------------------------------------- fluxo da tela

    /** Limpa o formulário e volta ao modo cadastro. */
    private void novo() {
        idEmEdicao = null;
        tabela.clearSelection();
        campoNome.setText("");
        campoEndereco.setText("");
        limparErros();
        botaoExcluir.setEnabled(false);
        campoNome.requestFocusInWindow();
    }

    /** Clique numa linha: carrega a concessionária no formulário e entra em modo edição. */
    private void carregarSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Concessionaria concessionaria = modeloTabela.getConcessionaria(tabela.convertRowIndexToModel(linha));
        idEmEdicao = concessionaria.getIdConcessionaria();
        limparErros();
        campoNome.setText(concessionaria.getNome());
        campoEndereco.setText(concessionaria.getEndereco());
        botaoExcluir.setEnabled(true);
    }

    private void salvar() {
        boolean valido = Validacao.verificar(campoNome,
                () -> Concessionaria.validarNome(campoNome.getText()));
        valido &= Validacao.verificar(campoEndereco,
                () -> Concessionaria.validarEndereco(campoEndereco.getText()));
        if (!valido) {
            return;
        }

        // Lê os campos aqui, na thread da tela: o trabalho em segundo plano não pode mexer em componentes.
        Long id = idEmEdicao;
        String nome = campoNome.getText();
        String endereco = campoEndereco.getText();

        TarefaEmSegundoPlano.executar(botaoSalvar,
                () -> id == null
                        ? concessionariaService.cadastrar(nome, endereco)
                        : concessionariaService.atualizar(id, nome, endereco),
                concessionaria -> {
                    Feedback.sucesso(this, id == null
                            ? "Concessionária cadastrada com sucesso."
                            : "Concessionária atualizada com sucesso.");
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
        if (!Feedback.confirmar(this, "Excluir a concessionária " + campoNome.getText().trim() + "?")) {
            return;
        }

        TarefaEmSegundoPlano.executar(botaoExcluir,
                () -> {
                    concessionariaService.excluir(id);
                    return id;
                },
                excluida -> {
                    Feedback.sucesso(this, "Concessionária excluída com sucesso.");
                    novo();
                    recarregar();
                },
                erro -> Feedback.erro(this, erro));
    }

    private void limparErros() {
        Validacao.limpar(campoNome);
        Validacao.limpar(campoEndereco);
    }
}
