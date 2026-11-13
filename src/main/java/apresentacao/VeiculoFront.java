package apresentacao;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;
import aplicacao.servico.VeiculoService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.TelaComDados;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;


public class VeiculoFront extends JPanel
        implements TelaComDados<VeiculoFront.Dados>, TelaComBotaoPrincipal {

    private final VeiculoService veiculoService;

    // Cadastro
    private JTextField campoMarca;
    private JTextField campoModelo;
    private JTextField campoAno;
    private JTextField campoPlaca;
    private JTextField campoPreco;
    private JComboBox<String> comboMoto;
    private JComboBox<Concessionaria> comboConcessionaria;

    // Consulta / status
    private JComboBox<Veiculo> comboVeiculos;
    private JLabel labelStatusAtual;
    private JComboBox<StatusVeiculo> comboNovoStatus;
    private JButton botaoAtualizar;

    // Botões da T-04
    private JButton botaoNovo;
    private JButton botaoSalvar;
    private JButton botaoExcluir;
    private JButton botaoManutencao;
    private JButton botaoDisponivel;

    /** null = modo cadastro / nada selecionado; preenchido = veículo selecionado (modo edição). */
    private Veiculo veiculoEmEdicao;

    public VeiculoFront(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Os botões precisam existir antes dos painéis abaixo: atualizarPainelStatus() os consulta.
        JPanel painelBotoes = criarPainelBotoes();

        add(criarPainelCadastro(), BorderLayout.NORTH);

        JPanel painelInferior = new JPanel(new BorderLayout(10, 10));
        painelInferior.add(criarPainelSelecao(), BorderLayout.NORTH);
        painelInferior.add(criarPainelStatus(), BorderLayout.CENTER);
        add(painelInferior, BorderLayout.CENTER);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    /** O que esta tela busca no banco ao ficar visível. */
    record Dados(List<Concessionaria> concessionarias, List<Veiculo> veiculos) { }

    /**
     * Busca concessionárias e veículos. Chamado pelo TelaPrincipal, fora da thread da tela,
     * sempre que o usuário troca para esta aba, para refletir mudanças feitas em outras.
     *
     * @return concessionárias e veículos atuais
     */
    @Override
    public Dados carregarDados() {
        return new Dados(veiculoService.listarConcessionarias(), veiculoService.listarTodos());
    }

    /**
     * Recarrega os combos com os dados buscados.
     *
     * @param dados resultado de {@link #carregarDados()}
     */
    @Override
    public void exibirDados(Dados dados) {
        comboConcessionaria.setModel(new DefaultComboBoxModel<>(
                dados.concessionarias().toArray(new Concessionaria[0])));
        comboVeiculos.setModel(new DefaultComboBoxModel<>(dados.veiculos().toArray(new Veiculo[0])));
        atualizarPainelStatus();
    }

    // ---------------------- Botões (Novo, Salvar, Excluir, status) ----------------------

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
                alterarStatus(botaoManutencao, StatusVeiculo.EM_MANUTENCAO,
                        "Veículo colocado em manutenção."));
        botaoDisponivel.addActionListener(e ->
                alterarStatus(botaoDisponivel, StatusVeiculo.DISPONIVEL,
                        "Veículo disponível novamente."));

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

        atualizarBotoes();
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

    /** Habilita cada botão só quando a ação é permitida para o veículo em edição. */
    private void atualizarBotoes() {
        Veiculo veiculo = veiculoEmEdicao;
        boolean editando = veiculo != null;
        boolean vendido = editando && veiculo.getStatus() == StatusVeiculo.VENDIDO;

        botaoNovo.setEnabled(true);
        botaoSalvar.setEnabled(!vendido);                  // vendido: bloqueado para edição
        botaoExcluir.setEnabled(editando && !vendido);     // só existe o que já foi salvo
        botaoManutencao.setEnabled(editando && veiculo.getStatus() == StatusVeiculo.DISPONIVEL);
        botaoDisponivel.setEnabled(editando && veiculo.getStatus() == StatusVeiculo.EM_MANUTENCAO);
    }

    /**
     * Troca o status do veículo em edição. Quem decide se a troca é válida é o domínio
     * (Veiculo/VeiculoService); a tela só pergunta e mostra o resultado.
     */
    private void alterarStatus(JButton origem, StatusVeiculo novoStatus, String mensagemSucesso) {
        Veiculo veiculo = veiculoEmEdicao;
        if (veiculo == null) {
            return;
        }

        TarefaEmSegundoPlano.executar(origem,
                () -> {
                    veiculoService.alterarStatus(veiculo, novoStatus);
                    return veiculo;
                },
                atualizado -> {
                    Feedback.sucesso(this, mensagemSucesso);
                    atualizarPainelStatus();   // atualiza o rótulo e, de quebra, os botões
                },
                erro -> {
                    Feedback.erro(this, erro);
                    atualizarBotoes();         // a tarefa reabilita o botão; volta à regra
                });
    }

    /** Volta ao modo cadastro: limpa o formulário e a seleção. */
    private void novo() {
        limparFormularioCadastro();
        comboVeiculos.setSelectedItem(null);   // dispara atualizarPainelStatus()
        veiculoEmEdicao = null;
        atualizarBotoes();
    }

    private void salvar() {
        // TODO (parte do formulário/backend): validar, cadastrar ou atualizar
    }

    private void excluir() {
        // TODO (parte do formulário/backend): confirmar e excluir
    }

    // ---------------------- Cadastro de veículo ----------------------

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel(new GridLayout(6, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Veículo"));

        campoMarca = new JTextField();
        campoModelo = new JTextField();
        campoAno = new JTextField();
        campoPlaca = new JTextField();
        campoPreco = new JTextField();
        comboMoto = new JComboBox<>(new String[]{"Não", "Sim"});

        List<Concessionaria> concessionarias = veiculoService.listarConcessionarias();
        comboConcessionaria = new JComboBox<>(concessionarias.toArray(new Concessionaria[0]));

        painel.add(new JLabel("Marca:"));
        painel.add(campoMarca);
        painel.add(new JLabel("Modelo:"));
        painel.add(campoModelo);
        painel.add(new JLabel("Ano:"));
        painel.add(campoAno);
        painel.add(new JLabel("Placa:"));
        painel.add(campoPlaca);
        painel.add(new JLabel("Preço:"));
        painel.add(campoPreco);
        painel.add(new JLabel("É moto?"));
        painel.add(comboMoto);

        JPanel painelCompleto = new JPanel(new BorderLayout(8, 8));
        painelCompleto.add(painel, BorderLayout.CENTER);

        JPanel linhaConcessionariaEBotao = new JPanel(new BorderLayout(8, 8));
        JPanel linhaConcessionaria = new JPanel(new FlowLayout(FlowLayout.LEFT));
        linhaConcessionaria.add(new JLabel("Concessionária:"));
        linhaConcessionaria.add(comboConcessionaria);
        linhaConcessionariaEBotao.add(linhaConcessionaria, BorderLayout.WEST);

        JButton botaoCadastrar = new JButton("Cadastrar Veículo");
        botaoCadastrar.addActionListener(e -> cadastrarVeiculo());
        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotao.add(botaoCadastrar);
        linhaConcessionariaEBotao.add(painelBotao, BorderLayout.EAST);

        painelCompleto.add(linhaConcessionariaEBotao, BorderLayout.SOUTH);
        painelCompleto.setBorder(BorderFactory.createTitledBorder("Cadastro de Veículo"));

        return painelCompleto;
    }

    private void cadastrarVeiculo() {
        try {
            Concessionaria concessionaria = (Concessionaria) comboConcessionaria.getSelectedItem();
            if (concessionaria == null) {
                JOptionPane.showMessageDialog(this,
                        "Cadastre uma concessionária antes de cadastrar um veículo.",
                        "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer ano = parseInteiro(campoAno.getText(), "Ano");
            Double preco = parseDouble(campoPreco.getText(), "Preço");
            boolean moto = "Sim".equals(comboMoto.getSelectedItem());

            veiculoService.cadastrar(
                    campoMarca.getText(),
                    campoModelo.getText(),
                    ano,
                    campoPlaca.getText(),
                    preco,
                    moto,
                    concessionaria);

            JOptionPane.showMessageDialog(this,
                    "Veículo cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormularioCadastro();
            recarregarVeiculos();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar veículo: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer parseInteiro(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número inteiro válido.");
        }
    }

    private Double parseDouble(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número válido.");
        }
    }

    private void limparFormularioCadastro() {
        campoMarca.setText("");
        campoModelo.setText("");
        campoAno.setText("");
        campoPlaca.setText("");
        campoPreco.setText("");
        comboMoto.setSelectedIndex(0);
    }

    // ---------------------- Consulta / atualização de status ----------------------

    private JPanel criarPainelSelecao() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Selecionar Veículo"));

        List<Veiculo> veiculos = veiculoService.listarTodos();
        comboVeiculos = new JComboBox<>(veiculos.toArray(new Veiculo[0]));
        comboVeiculos.addActionListener(e -> atualizarPainelStatus());

        painel.add(new JLabel("Veículo:"));
        painel.add(comboVeiculos);

        return painel;
    }

    private JPanel criarPainelStatus() {
        JPanel painel = new JPanel(new GridLayout(3, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Status do Veículo"));

        labelStatusAtual = new JLabel("-");

        // VENDIDO não aparece aqui: a venda é feita na aba "Vendas", e o próprio
        // modelo (Veiculo) impede que um veículo vendido volte a ficar disponível.
        comboNovoStatus = new JComboBox<>(new StatusVeiculo[]{
                StatusVeiculo.DISPONIVEL, StatusVeiculo.EM_MANUTENCAO
        });

        botaoAtualizar = new JButton("Atualizar Status");
        botaoAtualizar.addActionListener(e -> atualizarStatus());

        painel.add(new JLabel("Status atual:"));
        painel.add(labelStatusAtual);
        painel.add(new JLabel("Novo status:"));
        painel.add(comboNovoStatus);
        painel.add(new JLabel());
        painel.add(botaoAtualizar);

        atualizarPainelStatus();

        return painel;
    }

    private void atualizarPainelStatus() {
        Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();

        // O veículo selecionado no combo é o veículo "em edição" dos botões.
        veiculoEmEdicao = veiculo;
        atualizarBotoes();

        if (veiculo == null) {
            labelStatusAtual.setText("-");
            comboNovoStatus.setEnabled(false);
            botaoAtualizar.setEnabled(false);
            return;
        }

        labelStatusAtual.setText(veiculo.getStatus().name());

        boolean vendido = veiculo.getStatus() == StatusVeiculo.VENDIDO;
        comboNovoStatus.setEnabled(!vendido);
        botaoAtualizar.setEnabled(!vendido);
    }

    private void atualizarStatus() {
        Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
        StatusVeiculo novoStatus = (StatusVeiculo) comboNovoStatus.getSelectedItem();

        if (veiculo == null || novoStatus == null) {
            return;
        }

        try {
            veiculoService.alterarStatus(veiculo, novoStatus);
            atualizarPainelStatus();

            JOptionPane.showMessageDialog(this,
                    "Status atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (VeiculoIndisponivelException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Operação não permitida", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar status: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // O veículo cadastrado/atualizado precisa refletir na lista de seleção
    private void recarregarVeiculos() {
        comboVeiculos.setModel(new DefaultComboBoxModel<>(
                veiculoService.listarTodos().toArray(new Veiculo[0])));
        atualizarPainelStatus();
    }
}
