package apresentacao;

import aplicacao.servico.ClienteService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
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
 * Tela de cadastro de clientes (CRUD completo): lista com busca à esquerda e formulário à direita
 * Funcionamento:
 *   <li>a tabela mostra nome, CPF, e-mail e telefone; o campo de busca filtra por nome ou CPF
 *       enquanto o usuário digita;</li>
 *   <li>clicar numa linha carrega o cliente no formulário e entra em <b>modo edição</b>;</li>
 *   <li><b>Novo</b> limpa o formulário e volta ao <b>modo cadastro</b>;</li>
 *   <li><b>Salvar</b> cadastra ou atualiza, conforme o modo;</li>
 *   <li><b>Excluir</b> (só em modo edição) pede confirmação antes de excluir;</li>
 *   <li>depois de cada operação a lista é recarregada do banco.</li>
 * </ul>
 * As operações com o banco rodam fora da thread da tela, pela {@link TarefaEmSegundoPlano}; os
 * avisos usam o {@link Feedback} e a marcação de campos inválidos usa a {@link Validacao}.
 *
 * @see ClienteService
 * @see ClienteTableModel
 */
public class ClienteFront extends JPanel {

    private final ClienteService clienteService;
    /** Dados da tabela; atualizado a cada recarga da lista. */
    private final ClienteTableModel modeloTabela = new ClienteTableModel();
    /** Cuida da ordenação por coluna e do filtro da busca, sem mexer nos dados do modelo. */
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
     * Monta a tela, deixa o formulário em modo cadastro e carrega a lista de clientes.
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

    /**
     * Monta o lado esquerdo: campo de busca em cima e a tabela de clientes embaixo.
     * Também liga o clique numa linha ao carregamento do cliente no formulário.
     *
     * @return o cartão com a busca e a tabela
     */
    private JPanel criarPainelLista() {
        JPanel cartao = Componentes.painelCartao("Clientes");

        campoBusca = Componentes.campoTexto();
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou CPF");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        tabela = new JTable(modeloTabela);
        tabela.setRowSorter(ordenador);
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

    /**
     * Monta o lado direito: os quatro campos do cliente (com espaço para a mensagem de erro de
     * cada um) e os botões Novo, Excluir e Salvar.
     *
     * @return o cartão com o formulário e os botões
     */
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

    // ---------------------------------------------------------------- busca

    /**
     * Filtra a lista por nome ou CPF enquanto o usuário digita (o CPF aceita pontos e traço).
     * O nome é comparado sem diferenciar maiúsculas de minúsculas e o CPF só pelos dígitos
     * digitados. Com o campo vazio, mostra todos os clientes.
     */
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
                return cliente.getName_cliente().toLowerCase().contains(texto)
                        || (!digitos.isEmpty() && cliente.getCpf_cliente().contains(digitos));
            }
        });
    }

    // ---------------------------------------------------------------- fluxo da tela

    /**
     * Limpa o formulário, tira a seleção da tabela e volta ao modo cadastro (o botão Excluir
     * fica desabilitado, porque não há cliente selecionado).
     */
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

    /**
     * Clique numa linha: carrega o cliente no formulário e entra em modo edição.
     * Se não houver linha selecionada (por exemplo, depois de recarregar a lista), não faz nada.
     */
    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Cliente cliente = modeloTabela.getCliente(tabela.convertRowIndexToModel(linha));
        idEmEdicao = cliente.getId_cliente();
        limparErros();
        campoNome.setText(cliente.getName_cliente());
        campoCpf.setText(ClienteTableModel.formatarCpf(cliente.getCpf_cliente()));
        campoEmail.setText(cliente.getEmail_cliente());
        campoTelefone.setText(ClienteTableModel.formatarTelefone(cliente.getPhone_cliente()));
        botaoExcluir.setEnabled(true);
    }

    /**
     * Botão Salvar: valida os quatro campos (marcando o erro de cada um) e, se estiver tudo certo,
     * cadastra (modo cadastro) ou atualiza (modo edição) em segundo plano. Em caso de sucesso,
     * mostra o aviso, volta ao modo cadastro e recarrega a lista; em caso de erro, mostra o
     * diálogo de erro e mantém o formulário como está.
     */
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

    /**
     * Botão Excluir: pede confirmação e, se o usuário confirmar, exclui o cliente em edição em
     * segundo plano. Em caso de sucesso, mostra o aviso, volta ao modo cadastro e recarrega a
     * lista. Se o cliente tiver vendas, o erro mostra a mensagem "Este cliente possui vendas e
     * não pode ser excluído.". Sem cliente em edição, não faz nada.
     */
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

    /** Recarrega a lista do banco em segundo plano e a mostra na tabela. */
    private void recarregar() {
        TarefaEmSegundoPlano.executar(
                clienteService::listar,
                modeloTabela::setClientes,
                erro -> Feedback.erro(this, erro));
    }

    /** Remove a marcação de erro (contorno vermelho e mensagem) dos quatro campos. */
    private void limparErros() {
        Validacao.limpar(campoNome);
        Validacao.limpar(campoCpf);
        Validacao.limpar(campoEmail);
        Validacao.limpar(campoTelefone);
    }
}