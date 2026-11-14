package apresentacao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import aplicacao.servico.VeiculoService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.comum.Validacao;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;

/**
 * Cadastro de veículos: filtros, busca e tabela à esquerda; formulário e ações à direita.
 * Clicar numa linha carrega o veículo no formulário (modo edição); "Novo" volta ao modo cadastro.
 * Veículo vendido fica só para consulta: campos e ações desabilitados.
 */
public class VeiculoFront extends JPanel
        implements TelaComDados<VeiculoFront.Dados>, TelaComBotaoPrincipal {

    private static final String CARRO = "Carro";
    private static final String MOTO = "Moto";
    private static final String TODOS_OS_TIPOS = "Todos os tipos";
    private static final String TODOS_OS_STATUS = "Todos os status";

    /** Opções do filtro de status, na mesma ordem dos textos do combo (null = todos). */
    private static final StatusVeiculo[] FILTRO_STATUS =
            {null, StatusVeiculo.DISPONIVEL, StatusVeiculo.EM_MANUTENCAO, StatusVeiculo.VENDIDO};

    private final VeiculoService veiculoService;
    private final VeiculoTableModel modeloTabela = new VeiculoTableModel();
    private final TableRowSorter<VeiculoTableModel> ordenador = new TableRowSorter<>(modeloTabela);

    // Lista e filtros
    private JTable tabela;
    private JTextField campoBusca;
    private JComboBox<String> comboFiltroStatus;
    private JComboBox<String> comboFiltroTipo;

    // Formulário
    private JTextField campoMarca;
    private JTextField campoModelo;
    private JTextField campoAno;
    private JTextField campoPlaca;
    private JTextField campoPreco;
    private JComboBox<String> comboTipo;
    private JComboBox<Concessionaria> comboConcessionaria;

    // Ações
    private JButton botaoNovo;
    private JButton botaoSalvar;
    private JButton botaoExcluir;
    private JButton botaoManutencao;
    private JButton botaoDisponivel;

    /** null = modo cadastro; preenchido = veículo selecionado na tabela (modo edição). */
    private Veiculo veiculoEmEdicao;

    /**
     * Monta a tela. Os dados chegam pelo {@link #carregarDados()}, chamado pela {@code TelaPrincipal}
     * em segundo plano quando o usuário abre esta aba.
     *
     * @param veiculoService serviço usado para listar, cadastrar, editar, excluir e trocar o status
     */
    public VeiculoFront(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(
                Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA, Tema.MARGEM_TELA));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridy = 0;

        c.gridx = 0;
        c.weightx = 0.65;
        c.insets = new Insets(0, 0, 0, Tema.MEDIO);
        add(criarPainelLista(), c);

        c.gridx = 1;
        c.weightx = 0.35;
        c.insets = new Insets(0, 0, 0, 0);
        add(criarPainelFormulario(), c);

        novo();
    }

    // ---------------------------------------------------------------- dados

    /**
     * O que esta tela busca no banco.
     *
     * @param concessionarias opções do combo de concessionária
     * @param veiculos        linhas da tabela
     */
    record Dados(List<Concessionaria> concessionarias, List<Veiculo> veiculos) { }

    /**
     * Busca concessionárias e veículos. Roda fora da thread da tela: chamado pela
     * {@code TelaPrincipal} ao abrir a aba e por {@link #recarregar(Long)} depois de cada alteração.
     *
     * @return concessionárias e veículos atuais
     */
    @Override
    public Dados carregarDados() {
        return new Dados(veiculoService.listarConcessionarias(), veiculoService.listarTodos());
    }

    /**
     * Mostra os dados buscados: preenche o combo de concessionárias (mantendo a escolhida, se ainda
     * existir) e a tabela. A troca da lista limpa a seleção, então a tela volta ao modo cadastro.
     *
     * @param dados resultado de {@link #carregarDados()}
     */
    @Override
    public void exibirDados(Dados dados) {
        Concessionaria escolhida = (Concessionaria) comboConcessionaria.getSelectedItem();
        comboConcessionaria.setModel(new DefaultComboBoxModel<>(
                dados.concessionarias().toArray(new Concessionaria[0])));
        selecionarConcessionaria(escolhida == null ? null : escolhida.getIdConcessionaria());

        modeloTabela.setVeiculos(dados.veiculos());
        if (veiculoEmEdicao != null) {
            novo();
        }
    }

    /**
     * Recarrega concessionárias e veículos em segundo plano.
     *
     * @param idParaSelecionar veículo a deixar selecionado depois (ex.: o que acabou de trocar de status),
     *                         ou {@code null} para ficar no modo cadastro
     */
    private void recarregar(Long idParaSelecionar) {
        TarefaEmSegundoPlano.executar(
                this::carregarDados,
                dados -> {
                    exibirDados(dados);
                    if (idParaSelecionar != null) {
                        selecionarNaTabela(idParaSelecionar);
                    }
                },
                erro -> Feedback.erro(this, erro));
    }

    // ---------------------------------------------------------------- montagem da tela

    private JPanel criarPainelLista() {
        JPanel cartao = Componentes.painelCartao("Veículos");

        campoBusca = Componentes.campoTexto();
        campoBusca.getAccessibleContext().setAccessibleName("Buscar veículos");
        campoBusca.putClientProperty("JTextField.placeholderText", "Buscar por placa, marca ou modelo");
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        String[] textosStatus = new String[FILTRO_STATUS.length];
        textosStatus[0] = TODOS_OS_STATUS;
        for (int i = 1; i < FILTRO_STATUS.length; i++) {
            textosStatus[i] = VeiculoTableModel.textoStatus(FILTRO_STATUS[i]);
        }
        comboFiltroStatus = Componentes.combo(textosStatus);
        comboFiltroStatus.getAccessibleContext().setAccessibleName("Filtrar por status");
        comboFiltroStatus.addActionListener(e -> aplicarFiltro());

        comboFiltroTipo = Componentes.combo(new String[]{TODOS_OS_TIPOS, CARRO, MOTO});
        comboFiltroTipo.getAccessibleContext().setAccessibleName("Filtrar por tipo");
        comboFiltroTipo.addActionListener(e -> aplicarFiltro());

        JPanel linhaFiltros = new JPanel(new GridLayout(1, 2, Tema.PEQUENO, 0));
        linhaFiltros.setOpaque(false);
        linhaFiltros.add(comboFiltroStatus);
        linhaFiltros.add(comboFiltroTipo);

        JPanel filtros = new JPanel(new BorderLayout(0, Tema.PEQUENO));
        filtros.setOpaque(false);
        filtros.add(campoBusca, BorderLayout.NORTH);
        filtros.add(linhaFiltros, BorderLayout.SOUTH);

        VeiculoCelulaRenderer renderer = new VeiculoCelulaRenderer();
        tabela = new JTable(modeloTabela);
        tabela.getAccessibleContext().setAccessibleName("Lista de veículos");
        tabela.setRowSorter(ordenador);
        tabela.setDefaultRenderer(Double.class, renderer);
        tabela.setDefaultRenderer(StatusVeiculo.class, renderer);
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
        conteudo.add(filtros, BorderLayout.NORTH);
        conteudo.add(new JScrollPane(tabela), BorderLayout.CENTER);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelFormulario() {
        JPanel cartao = Componentes.painelCartao("Dados do veículo");

        campoMarca = Componentes.campoTexto();
        campoModelo = Componentes.campoTexto();
        campoAno = Componentes.campoTexto();
        campoPlaca = Componentes.campoTexto();
        campoPreco = Componentes.campoTexto();
        campoPreco.putClientProperty("JTextField.placeholderText", "Ex.: 85.000,00");
        comboTipo = Componentes.combo(new String[]{CARRO, MOTO});
        comboConcessionaria = Componentes.combo(new Concessionaria[0]);

        JPanel formulario = Componentes.painelFormulario();
        Componentes.adicionarCampo(formulario, 0, "Marca:", campoMarca);
        Componentes.adicionarCampo(formulario, 1, "Modelo:", campoModelo);
        Componentes.adicionarCampo(formulario, 2, "Ano:", campoAno);
        Componentes.adicionarCampo(formulario, 3, "Placa:", campoPlaca);
        Componentes.adicionarCampo(formulario, 4, "Preço (R$):", campoPreco);
        Componentes.adicionarCampo(formulario, 5, "Tipo:", comboTipo);
        Componentes.adicionarCampo(formulario, 6, "Concessionária:", comboConcessionaria);

        JPanel conteudo = new JPanel(new BorderLayout(0, Tema.MEDIO));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(criarPainelBotoes(), BorderLayout.SOUTH);
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelBotoes() {
        botaoNovo = Componentes.botaoSecundario("Novo");
        botaoExcluir = Componentes.botaoPerigo("Excluir");
        botaoSalvar = Componentes.botaoPrimario("Salvar");
        botaoManutencao = Componentes.botaoSecundario("Colocar em manutenção");
        botaoDisponivel = Componentes.botaoSecundario("Tornar disponível");

        // Alt+letra: C, V, E, O e D são do menu lateral; N, S e X seguem a tela de clientes.
        botaoNovo.setMnemonic('N');
        botaoSalvar.setMnemonic('S');
        botaoExcluir.setMnemonic('X');
        botaoManutencao.setMnemonic('M');
        botaoDisponivel.setMnemonic('T');

        botaoNovo.addActionListener(e -> novo());
        botaoSalvar.addActionListener(e -> salvar());
        botaoExcluir.addActionListener(e -> excluir());
        botaoManutencao.addActionListener(e ->
                alterarStatus(botaoManutencao, StatusVeiculo.EM_MANUTENCAO, "Veículo colocado em manutenção."));
        botaoDisponivel.addActionListener(e ->
                alterarStatus(botaoDisponivel, StatusVeiculo.DISPONIVEL, "Veículo disponível novamente."));

        // Linha de cima: troca de status. Linha de baixo: Novo / Excluir / Salvar (como em Clientes).
        JPanel linhaStatus = new JPanel(new GridLayout(1, 2, Tema.PEQUENO, 0));
        linhaStatus.setOpaque(false);
        linhaStatus.add(botaoManutencao);
        linhaStatus.add(botaoDisponivel);

        JPanel linhaPrincipal = new JPanel(new FlowLayout(FlowLayout.RIGHT, Tema.PEQUENO, 0));
        linhaPrincipal.setOpaque(false);
        linhaPrincipal.add(botaoNovo);
        linhaPrincipal.add(botaoExcluir);
        linhaPrincipal.add(botaoSalvar);

        JPanel painel = new JPanel(new GridLayout(2, 1, 0, Tema.PEQUENO));
        painel.setOpaque(false);
        painel.add(linhaStatus);
        painel.add(linhaPrincipal);
        return painel;
    }

    /**
     * O Enter nesta tela salva o veículo (cadastra ou atualiza).
     *
     * @return o botão "Salvar"
     */
    @Override
    public JButton botaoPrincipal() {
        return botaoSalvar;
    }

    // ---------------------------------------------------------------- filtros

    /** Combina status, tipo e busca (placa, marca ou modelo) sobre a lista já carregada. */
    private void aplicarFiltro() {
        StatusVeiculo status = FILTRO_STATUS[comboFiltroStatus.getSelectedIndex()];
        Object tipo = comboFiltroTipo.getSelectedItem();
        Boolean moto = CARRO.equals(tipo) ? Boolean.FALSE : MOTO.equals(tipo) ? Boolean.TRUE : null;
        String texto = campoBusca.getText().trim().toLowerCase();

        if (status == null && moto == null && texto.isEmpty()) {
            ordenador.setRowFilter(null);
            return;
        }
        ordenador.setRowFilter(new RowFilter<VeiculoTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends VeiculoTableModel, ? extends Integer> entrada) {
                Veiculo veiculo = entrada.getModel().getVeiculo(entrada.getIdentifier());
                return (status == null || veiculo.getStatus() == status)
                        && (moto == null || moto.equals(veiculo.isMoto()))
                        && (texto.isEmpty()
                            || veiculo.getPlaca().toLowerCase().contains(texto)
                            || veiculo.getMarca().toLowerCase().contains(texto)
                            || veiculo.getModelo().toLowerCase().contains(texto));
            }
        });
    }

    // ---------------------------------------------------------------- fluxo da tela

    /** Limpa o formulário e volta ao modo cadastro. */
    private void novo() {
        veiculoEmEdicao = null;
        tabela.clearSelection();
        campoMarca.setText("");
        campoModelo.setText("");
        campoAno.setText("");
        campoPlaca.setText("");
        campoPreco.setText("");
        comboTipo.setSelectedItem(CARRO);
        limparErros();
        atualizarHabilitados();
        campoMarca.requestFocusInWindow();
    }

    /** Clique numa linha: carrega o veículo no formulário e entra em modo edição. */
    private void carregarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Veiculo veiculo = modeloTabela.getVeiculo(tabela.convertRowIndexToModel(linha));
        veiculoEmEdicao = veiculo;
        limparErros();
        campoMarca.setText(veiculo.getMarca());
        campoModelo.setText(veiculo.getModelo());
        campoAno.setText(String.valueOf(veiculo.getAno()));
        campoPlaca.setText(veiculo.getPlaca());
        campoPreco.setText(VeiculoTableModel.formatarValor(veiculo.getPreco()));
        comboTipo.setSelectedItem(VeiculoTableModel.textoTipo(veiculo.isMoto()));
        selecionarConcessionaria(veiculo.getConcessionaria().getIdConcessionaria());
        atualizarHabilitados();
    }

    /** Habilita campos e botões conforme o modo (cadastro/edição) e o status do veículo. */
    private void atualizarHabilitados() {
        Veiculo veiculo = veiculoEmEdicao;
        boolean editando = veiculo != null;
        StatusVeiculo status = editando ? veiculo.getStatus() : null;
        boolean vendido = status == StatusVeiculo.VENDIDO;

        for (JComponent campo : new JComponent[]{campoMarca, campoModelo, campoAno, campoPlaca,
                campoPreco, comboTipo, comboConcessionaria}) {
            campo.setEnabled(!vendido);
        }
        botaoNovo.setEnabled(true);
        botaoSalvar.setEnabled(!vendido);
        botaoExcluir.setEnabled(editando && !vendido);
        botaoManutencao.setEnabled(status == StatusVeiculo.DISPONIVEL);
        botaoDisponivel.setEnabled(status == StatusVeiculo.EM_MANUTENCAO);
    }

    private void salvar() {
        boolean valido = Validacao.verificar(campoMarca, () -> Veiculo.validarMarca(campoMarca.getText()));
        valido &= Validacao.verificar(campoModelo, () -> Veiculo.validarModelo(campoModelo.getText()));
        valido &= Validacao.verificar(campoAno, () -> Veiculo.validarAno(converterAno(campoAno.getText())));
        valido &= Validacao.verificar(campoPlaca, () -> Veiculo.validarPlaca(campoPlaca.getText()));
        valido &= Validacao.verificar(campoPreco, () -> Veiculo.validarPreco(converterPreco(campoPreco.getText())));
        valido &= Validacao.verificar(comboConcessionaria, () -> {
            if (comboConcessionaria.getSelectedItem() == null) {
                throw new IllegalArgumentException("Cadastre uma concessionária antes de cadastrar um veículo.");
            }
        });
        if (!valido) {
            return;
        }

        // Lê os campos aqui, na thread da tela: o trabalho em segundo plano não pode mexer em componentes.
        Long id = veiculoEmEdicao == null ? null : veiculoEmEdicao.getIdVeiculo();
        String marca = campoMarca.getText();
        String modelo = campoModelo.getText();
        Integer ano = converterAno(campoAno.getText());
        String placa = campoPlaca.getText();
        Double preco = converterPreco(campoPreco.getText());
        boolean moto = MOTO.equals(comboTipo.getSelectedItem());
        Concessionaria concessionaria = (Concessionaria) comboConcessionaria.getSelectedItem();

        TarefaEmSegundoPlano.executar(botaoSalvar,
                () -> id == null
                        ? veiculoService.cadastrar(marca, modelo, ano, placa, preco, moto, concessionaria)
                        : veiculoService.atualizar(id, marca, modelo, ano, placa, preco, moto, concessionaria),
                veiculo -> {
                    Feedback.sucesso(this, id == null
                            ? "Veículo cadastrado com sucesso."
                            : "Veículo atualizado com sucesso.");
                    novo();
                    recarregar(null);
                },
                erro -> {
                    Feedback.erro(this, erro);
                    atualizarHabilitados();   // a tarefa reabilita o botão; volta à regra
                });
    }

    private void excluir() {
        Veiculo veiculo = veiculoEmEdicao;
        if (veiculo == null) {
            return;
        }
        if (!Feedback.confirmar(this, "Excluir o veículo " + veiculo.getMarca() + " " + veiculo.getModelo()
                + " (placa " + veiculo.getPlaca() + ")?")) {
            return;
        }

        Long id = veiculo.getIdVeiculo();
        TarefaEmSegundoPlano.executar(botaoExcluir,
                () -> {
                    veiculoService.excluir(id);
                    return id;
                },
                excluido -> {
                    Feedback.sucesso(this, "Veículo excluído com sucesso.");
                    novo();
                    recarregar(null);
                },
                erro -> {
                    Feedback.erro(this, erro);
                    atualizarHabilitados();
                });
    }

    /**
     * Troca o status do veículo em edição. Quem decide se a troca é válida é o domínio
     * (Veiculo/VeiculoService); a tela só pede e mostra o resultado.
     */
    private void alterarStatus(JButton origem, StatusVeiculo novoStatus, String mensagemSucesso) {
        Veiculo veiculo = veiculoEmEdicao;
        if (veiculo == null) {
            return;
        }
        Long id = veiculo.getIdVeiculo();

        TarefaEmSegundoPlano.executar(origem,
                () -> {
                    veiculoService.alterarStatus(veiculo, novoStatus);
                    return veiculo;
                },
                atualizado -> {
                    Feedback.sucesso(this, mensagemSucesso);
                    recarregar(id);   // continua com o mesmo veículo selecionado
                },
                erro -> {
                    Feedback.erro(this, erro);
                    recarregar(id);   // descarta qualquer mudança que tenha ficado só na memória
                });
    }

    // ---------------------------------------------------------------- conversões e apoio

    /**
     * Converte o ano digitado.
     *
     * @throws IllegalArgumentException se não for um número inteiro
     */
    private static Integer converterAno(String texto) {
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ano deve ser um número, ex.: 2024.");
        }
    }

    /**
     * Converte o preço digitado no padrão brasileiro. Aceita "85.000,00", "85000,50", "85000"
     * e também "R$ 85.000,00". Sem vírgula, pontos em grupos de três ("85.000") são milhar;
     * fora disso o ponto é tratado como decimal ("85000.50").
     *
     * @throws IllegalArgumentException se não for um número
     */
    private static Double converterPreco(String texto) {
        String limpo = texto.replace("R$", "").replaceAll("\\s", "");
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (limpo.matches("\\d{1,3}(\\.\\d{3})+")) {
            limpo = limpo.replace(".", "");
        }
        try {
            return Double.valueOf(limpo);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Preço inválido. Use, por exemplo, 85.000,00.");
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

    /** Seleciona a linha do veículo (se ele passar pelos filtros atuais), o que o carrega no formulário. */
    private void selecionarNaTabela(Long id) {
        int linhaModelo = modeloTabela.linhaDoVeiculo(id);
        int linhaTela = linhaModelo < 0 ? -1 : tabela.convertRowIndexToView(linhaModelo);
        if (linhaTela >= 0) {
            tabela.setRowSelectionInterval(linhaTela, linhaTela);
            tabela.scrollRectToVisible(tabela.getCellRect(linhaTela, 0, true));
        }
    }

    private void limparErros() {
        for (JComponent campo : new JComponent[]{campoMarca, campoModelo, campoAno, campoPlaca,
                campoPreco, comboConcessionaria}) {
            Validacao.limpar(campo);
        }
    }
}
