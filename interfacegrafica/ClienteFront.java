package interfacegrafica;

import modelo.Cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteFront extends JPanel {

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoTelefone;

    public ClienteFront() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarPainelBotao(), BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de Cliente"));

        campoNome = new JTextField();
        campoCpf = new JTextField();
        campoEmail = new JTextField();
        campoTelefone = new JTextField();

        painel.add(new JLabel("Nome:"));
        painel.add(campoNome);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpf);
        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmail);
        painel.add(new JLabel("Telefone:"));
        painel.add(campoTelefone);

        return painel;
    }

    private JPanel criarPainelBotao() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoCadastrar = new JButton("Cadastrar Cliente");
        botaoCadastrar.addActionListener(e -> cadastrarCliente());
        painel.add(botaoCadastrar);
        return painel;
    }

    private void cadastrarCliente() {
        if (campoNome.getText().isBlank() || campoCpf.getText().isBlank()
                || campoEmail.getText().isBlank() || campoTelefone.getText().isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos.",
                    "Dados incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            new Cliente(
                    campoNome.getText(),
                    campoCpf.getText(),
                    campoEmail.getText(),
                    campoTelefone.getText()
            );

            JOptionPane.showMessageDialog(this,
                    "Cliente cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoCpf.setText("");
        campoEmail.setText("");
        campoTelefone.setText("");
    }
}