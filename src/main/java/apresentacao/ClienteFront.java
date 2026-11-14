package apresentacao;

import aplicacao.servico.ClienteService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.Validacao;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Cliente;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

/**
 * Cadastro de clientes: lista com busca à esquerda, formulário à direita.
 * Clicar numa linha carrega o cliente no formulário (modo edição); "Novo" volta ao modo cadastro.
 * Os campos são validados na tela antes de chamar o {@link ClienteService}, e todo acesso ao banco
 * roda em segundo plano; erros (inclusive cliente com vendas) aparecem como mensagem ao usuário.
 * Atalhos: Enter salva, Alt+N novo, Alt+S salvar, Alt+X excluir.
 */
public class ClienteFront extends JPanel implements TelaComBotaoPrincipal {

    private final ClienteService clienteService;
    private final ClienteTableModel modeloTabela = new ClienteTableModel();
    private final TableRowSorter<ClienteTableModel> ordenador = new TableRowSorter<>(modeloTabela);

    private JTable tabela;
    private JTextField campoBusca;
    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoTelefone;
    private JButton botaoNovo;
    private JButton botaoSalvar;
    private JButton botaoExcluir;

    /** null = modo cadastro; preenchido = modo edição do cliente com esse ID. */
    private Long idEmEdicao;

    /**
     * Monta a tela e já carrega a lista de clientes em segundo plano.
     *
     * @param clienteService serviço usado para listar, cadastrar, atualizar e excluir clientes
     */
    public ClienteFront(ClienteService clienteService) {
        this.clienteService = clienteService;

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
        recarregar();
    }

    // ---------------------------------------------------------------- montagem da tela

    private JPanel criarPainelLista() {
        JPanel cartao = Componentes.painelCartao("Clientes");

        campoBusca = Componentes.campoTexto();
        campoBusca.getAccessibleContext().setAccessibleName("Buscar clientes");
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou CPF");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        tabela = new JTable(modeloTabela);
        tabela.getAccessibleContext().setAccessibleName("Lista de clientes");
        tabela.setRowSorter(ordenador);
        tabela.setFont(Tema.TEXTO_FONTE);
        tabela.setRowHeight(Tema.ALTURA_LINHA_TABELA);
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
        JPanel cartao = Componentes.painelCartao("Dados do cliente");

        campoNome = Componentes.campoTexto();
        campoCpf = Componentes.campoTexto();
        campoEmail = Componentes.campoTexto();
        campoTelefone = Componentes.campoTexto();

        JPanel formulario = Componentes.painelFormulario();
        Componentes.adicionarCampo(formulario, 0, "Nome:", campoNome);
        Componentes.adicionarCampo(formulario, 1, "CPF:", campoCpf);
        Componentes.adicionarCampo(formulario, 2, "E-mail:", campoEmail);
        Componentes.adicionarCampo(formulario, 3, "Telefone:", campoTelefone);

        botaoNovo = Componentes.botaoSecundario("Novo");
        botaoExcluir = Componentes.botaoPerigo("Excluir");
        botaoSalvar = Componentes.botaoPrimario("Salvar");
        // Alt+letra: C, V, E, O e D já são do menu lateral.
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
     * O Enter nesta tela salva o cliente (cadastra ou atualiza).
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
        ordenador.setRowFilter(new RowFilter<ClienteTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends ClienteTableModel, ? extends Integer> entrada) {
                Cliente cliente = entrada.getModel().getCliente(entrada.getIdentifier());
                return cliente.getNome().toLowerCase().contains(texto)
                        || (!digitos.isEmpty() && cliente.getCpf().contains(digitos));
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
        campoEmail.setText("");
        campoTelefone.setText("");
        limparErros();
        botaoExcluir.setEnabled(false);
        campoNome.requestFocusInWindow();
    }

    /** Clique numa linha: carrega o cliente no formulário e entra em modo edição. */
    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Cliente cliente = modeloTabela.getCliente(tabela.convertRowIndexToModel(linha));
        idEmEdicao = cliente.getIdCliente();
        limparErros();
        campoNome.setText(cliente.getNome());
        campoCpf.setText(ClienteTableModel.formatarCpf(cliente.getCpf()));
        campoEmail.setText(cliente.getEmail());
        campoTelefone.setText(ClienteTableModel.formatarTelefone(cliente.getTelefone()));
        botaoExcluir.setEnabled(true);
    }

    private void salvar() {
        boolean valido = Validacao.verificar(campoNome, () -> Cliente.validarNome(campoNome.getText()));
        valido &= Validacao.verificar(campoCpf, () -> Cliente.validarCpf(campoCpf.getText()));
        valido &= Validacao.verificar(campoEmail, () -> Cliente.validarEmail(campoEmail.getText()));
        valido &= Validacao.verificar(campoTelefone, () -> Cliente.validarTelefone(campoTelefone.getText()));
        if (!valido) {
            return;
        }

        // Lê os campos aqui, na thread da tela: o trabalho em segundo plano não pode mexer em componentes.
        Long id = idEmEdicao;
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();

        TarefaEmSegundoPlano.executar(botaoSalvar,
                () -> id == null
                        ? clienteService.cadastrar(nome, cpf, email, telefone)
                        : clienteService.atualizar(id, nome, cpf, email, telefone),
                cliente -> {
                    Feedback.sucesso(this, id == null
                            ? "Cliente cadastrado com sucesso."
                            : "Cliente atualizado com sucesso.");
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
        if (!Feedback.confirmar(this, "Excluir o cliente " + campoNome.getText().trim() + "?")) {
            return;
        }

        TarefaEmSegundoPlano.executar(botaoExcluir,
                () -> {
                    clienteService.excluir(id);
                    return id;
                },
                excluido -> {
                    Feedback.sucesso(this, "Cliente excluído com sucesso.");
                    novo();
                    recarregar();
                },
                erro -> Feedback.erro(this, erro));
    }

    /** Recarrega a lista do banco em segundo plano. */
    private void recarregar() {
        TarefaEmSegundoPlano.executar(
                clienteService::listar,
                modeloTabela::setClientes,
                erro -> Feedback.erro(this, erro));
    }

    private void limparErros() {
        Validacao.limpar(campoNome);
        Validacao.limpar(campoCpf);
        Validacao.limpar(campoEmail);
        Validacao.limpar(campoTelefone);
    }
}