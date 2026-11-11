package apresentacao;

import aplicacao.servico.ClienteService;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;

import javax.swing.*;
import java.awt.*;

public class ClienteFront extends JPanel {

    private final ClienteService clienteService;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoTelefone;

    public ClienteFront(ClienteService clienteService) {
        this.clienteService = clienteService;
        setLayout(new BorderLayout(Tema.GRANDE, Tema.GRANDE));
        setBackground(Tema.FUNDO);

        add(criarFormulario(), BorderLayout.CENTER);
        add(criarPainelBotao(), BorderLayout.SOUTH);
    }

    private JPanel criarFormulario() {
        JPanel cartao = Componentes.painelCartao("Cadastro de cliente");
        JPanel formulario = Componentes.painelFormulario();
        campoNome = Componentes.campoTexto();
        campoCpf = Componentes.campoTexto();
        campoEmail = Componentes.campoTexto();
        campoTelefone = Componentes.campoTexto();
        Componentes.adicionarCampo(formulario, 0, "Nome", campoNome);
        Componentes.adicionarCampo(formulario, 1, "CPF", campoCpf);
        Componentes.adicionarCampo(formulario, 2, "E-mail", campoEmail);
        Componentes.adicionarCampo(formulario, 3, "Telefone", campoTelefone);
        JPanel alinhamento = new JPanel(new BorderLayout());
        alinhamento.setOpaque(false);
        alinhamento.add(formulario, BorderLayout.NORTH);
        cartao.add(alinhamento, BorderLayout.CENTER);
        return cartao;
    }

    private JPanel criarPainelBotao() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.setOpaque(false);
        JButton botaoCadastrar = Componentes.botaoPrimario("Cadastrar cliente");
        botaoCadastrar.setMnemonic('C');
        botaoCadastrar.addActionListener(e -> cadastrarCliente());
        painel.add(botaoCadastrar);
        return painel;
    }

    private void cadastrarCliente() {
        try {
            clienteService.cadastrar(
                    campoNome.getText(),
                    campoCpf.getText(),
                    campoEmail.getText(),
                    campoTelefone.getText());

            JOptionPane.showMessageDialog(this,
                    "Cliente cadastrado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
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
