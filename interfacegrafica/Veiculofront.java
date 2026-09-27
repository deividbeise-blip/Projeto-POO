package interfacegrafica;

import dao.VeiculoDAO;
import modelo.StatusVeiculo;
import modelo.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VeiculoFront extends JPanel {

    private JComboBox<Veiculo> comboVeiculos;
    private JLabel labelStatusAtual;
    private JComboBox<StatusVeiculo> comboNovoStatus;
    private JButton botaoAtualizar;

    public VeiculoFront() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(criarPainelSelecao(), BorderLayout.NORTH);
        add(criarPainelStatus(), BorderLayout.CENTER);
    }

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

        // Um carro já vendido nunca pode voltar a ficar disponível por aqui:
        // a regra de negócio (VeiculoIndisponivelException) garante que ele
        // não pode ser vendido de novo, e essa tela não pode furar essa regra
        // liberando o status manualmente.
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
            veiculo.setStatus(novoStatus);
            labelStatusAtual.setText(veiculo.getStatus().name());

            JOptionPane.showMessageDialog(this,
                    "Status atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao atualizar status: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}