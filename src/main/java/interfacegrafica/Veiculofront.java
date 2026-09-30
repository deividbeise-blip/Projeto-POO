package interfacegrafica;

import dao.ConcessionariaDAO;
import dao.VeiculoDAO;
import excecao.VeiculoIndisponivelException;
import modelo.Concessionaria;
import modelo.StatusVeiculo;
import modelo.Veiculo;
import servico.VeiculoService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VeiculoFront extends JPanel {

    private final VeiculoService veiculoService = new VeiculoService();

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

    public VeiculoFront() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(criarPainelCadastro(), BorderLayout.NORTH);

        JPanel painelInferior = new JPanel(new BorderLayout(10, 10));
        painelInferior.add(criarPainelSelecao(), BorderLayout.NORTH);
        painelInferior.add(criarPainelStatus(), BorderLayout.CENTER);
        add(painelInferior, BorderLayout.CENTER);
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

        List<Concessionaria> concessionarias = new ConcessionariaDAO().listar();
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

        List<Veiculo> veiculos = new VeiculoDAO().listarTodos();
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

    // O veículo cadastrado precisa aparecer na lista de seleção
    private void recarregarVeiculos() {
        comboVeiculos.setModel(new DefaultComboBoxModel<>(
                new VeiculoDAO().listarTodos().toArray(new Veiculo[0])));
    }
}
