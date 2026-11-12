package apresentacao;

import aplicacao.servico.ClienteService;
import apresentacao.comum.Feedback;
import apresentacao.comum.TarefaEmSegundoPlano;
import apresentacao.comum.TelaComBotaoPrincipal;
import apresentacao.comum.Validacao;
import apresentacao.estilo.Componentes;
import apresentacao.estilo.Tema;
import dominio.modelo.Cliente;

import javax.swing.*;
import java.awt.*;

/**
 * Cadastro de clientes. Serve de exemplo de uso de {@link TarefaEmSegundoPlano},
 * {@link Feedback} e {@link Validacao} para as outras telas.
 */
public class ClienteFront extends JPanel implements TelaComBotaoPrincipal {

    private final ClienteService clienteService;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoTelefone;
    private JButton botaoCadastrar;

    /**
     * Cria a tela de cadastro de clientes.
     *
     * @param clienteService serviço usado para gravar o cliente
     */
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
        botaoCadastrar = Componentes.botaoPrimario("Cadastrar cliente");
        botaoCadastrar.setMnemonic('A'); // Alt+C é do item "Clientes" do menu
        botaoCadastrar.addActionListener(e -> cadastrarCliente());
        painel.add(botaoCadastrar);
        return painel;
    }

    /**
     * O Enter nesta tela cadastra o cliente.
     *
     * @return o botão "Cadastrar cliente"
     */
    @Override
    public JButton botaoPrincipal() {
        return botaoCadastrar;
    }

    private void cadastrarCliente() {
        if (!validarFormulario()) {
            return;
        }
        // Lê os campos aqui, na thread da tela; o lambda abaixo roda em outra thread.
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();

        TarefaEmSegundoPlano.executar(botaoCadastrar,
                () -> clienteService.cadastrar(nome, cpf, email, telefone),
                cliente -> {
                    Feedback.sucesso(this, "Cliente " + cliente.getNome() + " cadastrado com sucesso.");
                    limparFormulario();
                },
                erro -> Feedback.erro(this, erro));
    }

    /**
     * Confere todos os campos com as regras do próprio {@link Cliente} e marca os inválidos de
     * uma vez só. O foco vai para o primeiro campo com erro.
     */
    private boolean validarFormulario() {
        JComponent primeiroInvalido = null;
        if (!Validacao.verificar(campoNome, () -> Cliente.validarNome(campoNome.getText()))) {
            primeiroInvalido = campoNome;
        }
        if (!Validacao.verificar(campoCpf, () -> Cliente.validarCpf(campoCpf.getText()))
                && primeiroInvalido == null) {
            primeiroInvalido = campoCpf;
        }
        if (!Validacao.verificar(campoEmail, () -> Cliente.validarEmail(campoEmail.getText()))
                && primeiroInvalido == null) {
            primeiroInvalido = campoEmail;
        }
        if (!Validacao.verificar(campoTelefone, () -> Cliente.validarTelefone(campoTelefone.getText()))
                && primeiroInvalido == null) {
            primeiroInvalido = campoTelefone;
        }
        if (primeiroInvalido != null) {
            primeiroInvalido.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoCpf.setText("");
        campoEmail.setText("");
        campoTelefone.setText("");
        campoNome.requestFocusInWindow();
    }
}
