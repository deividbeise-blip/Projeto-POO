package apresentacao;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.excecao.VendaInvalidaException;
import dominio.modelo.Cliente;
import dominio.modelo.Pagamento;
import dominio.modelo.PagamentoCartao;
import dominio.modelo.Veiculo;
import dominio.modelo.Vendedor;
import aplicacao.dto.DadosCartao;
import aplicacao.dto.DadosPagamento;
import aplicacao.dto.DadosPix;
import aplicacao.dto.DadosVenda;
import aplicacao.servico.ClienteService;
import aplicacao.servico.VeiculoService;
import aplicacao.servico.VendaService;
import apresentacao.comum.TelaComDados;

public class ConcessionariaFront extends JPanel implements TelaComDados<ConcessionariaFront.Dados> {

    private final VendaService vendaService;
    private final ClienteService clienteService;
    private final VeiculoService veiculoService;

    private JComboBox<Cliente> comboClientes;

    private JComboBox<Veiculo> comboVeiculos;
    private JComboBox<Vendedor> comboVendedores;
    private JComboBox<String> comboFormaPagamento;

    // Campos PIX
    private JPanel painelPix;
    private JTextField campoChavePix;
    private JComboBox<String> comboTipoChave;

    // Campos Cartão
    private JPanel painelCartao;
    private JTextField campoNumeroCartao;
    private JTextField campoNomeTitular;
    private JTextField campoValidade;
    private JTextField campoCvv;
    private JSpinner spinnerParcelas;

    private JPanel painelFormasDinamico;

    public ConcessionariaFront(VendaService vendaService,
                               ClienteService clienteService,
                               VeiculoService veiculoService) {
        this.vendaService = vendaService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelCliente(), BorderLayout.NORTH);
        add(criarPainelVendaEPagamento(), BorderLayout.CENTER);
        add(criarPainelBotao(), BorderLayout.SOUTH);
    }

    /** O que esta tela busca no banco ao ficar visível. */
    record Dados(List<Cliente> clientes, List<Vendedor> vendedores, List<Veiculo> veiculosDisponiveis) { }

    /**
     * Busca clientes, vendedores e veículos disponíveis. Chamado pelo TelaPrincipal, fora da
     * thread da tela, sempre que o usuário troca para esta aba, para refletir cadastros feitos
     * em outras abas e mudanças de status (ex.: veículo colocado em manutenção).
     *
     * @return clientes, vendedores e veículos disponíveis atuais
     */
    @Override
    public Dados carregarDados() {
        return new Dados(clienteService.listar(), vendaService.listarVendedores(),
                veiculoService.listarDisponiveis());
    }

    /**
     * Recarrega os combos com os dados buscados.
     *
     * @param dados resultado de {@link #carregarDados()}
     */
    @Override
    public void exibirDados(Dados dados) {
        comboClientes.setModel(new DefaultComboBoxModel<>(dados.clientes().toArray(new Cliente[0])));
        comboVendedores.setModel(new DefaultComboBoxModel<>(dados.vendedores().toArray(new Vendedor[0])));
        comboVeiculos.setModel(new DefaultComboBoxModel<>(dados.veiculosDisponiveis().toArray(new Veiculo[0])));
    }

    private JPanel criarPainelCliente() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Cliente"));

        List<Cliente> clientes = clienteService.listar();
        comboClientes = new JComboBox<>(clientes.toArray(new Cliente[0]));

        painel.add(new JLabel("Cliente:"));
        painel.add(comboClientes);

        return painel;
    }

    private JPanel criarPainelVendaEPagamento() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        // Veículo e vendedor
        JPanel painelSelecao = new JPanel(new GridLayout(2, 2, 5, 5));
        painelSelecao.setBorder(BorderFactory.createTitledBorder("Veículo e Vendedor"));

        List<Veiculo> veiculosDisponiveis = veiculoService.listarDisponiveis();
        comboVeiculos = new JComboBox<>(veiculosDisponiveis.toArray(new Veiculo[0]));

        List<Vendedor> vendedores = vendaService.listarVendedores();
        comboVendedores = new JComboBox<>(vendedores.toArray(new Vendedor[0]));

        painelSelecao.add(new JLabel("Veículo:"));
        painelSelecao.add(comboVeiculos);
        painelSelecao.add(new JLabel("Vendedor:"));
        painelSelecao.add(comboVendedores);

        // Forma de pagamento
        JPanel painelPagamento = new JPanel(new BorderLayout(5, 5));
        painelPagamento.setBorder(BorderFactory.createTitledBorder("Pagamento"));

        JPanel linhaForma = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboFormaPagamento = new JComboBox<>(new String[]{"PIX", "Cartão"});
        linhaForma.add(new JLabel("Forma de pagamento:"));
        linhaForma.add(comboFormaPagamento);

        painelFormasDinamico = new JPanel(new CardLayout());
        painelFormasDinamico.add(criarPainelPix(), "PIX");
        painelFormasDinamico.add(criarPainelCartao(), "Cartão");

        comboFormaPagamento.addActionListener(e -> {
            CardLayout cl = (CardLayout) painelFormasDinamico.getLayout();
            cl.show(painelFormasDinamico, (String) comboFormaPagamento.getSelectedItem());
        });

        painelPagamento.add(linhaForma, BorderLayout.NORTH);
        painelPagamento.add(painelFormasDinamico, BorderLayout.CENTER);

        painel.add(painelSelecao);
        painel.add(painelPagamento);

        return painel;
    }

    private JPanel criarPainelPix() {
        painelPix = new JPanel(new GridLayout(2, 2, 5, 5));
        campoChavePix = new JTextField();
        // Os valores precisam bater exatamente com o ENUM da coluna tipo_chave_pix
        // no banco: 'CPF', 'Email', 'Telefone' (sem "CNPJ" e sem "Aleatória", que
        // o banco não aceita, e "Email" sem hífen).
        comboTipoChave = new JComboBox<>(new String[]{"CPF", "Email", "Telefone"});

        painelPix.add(new JLabel("Chave PIX:"));
        painelPix.add(campoChavePix);
        painelPix.add(new JLabel("Tipo de chave:"));
        painelPix.add(comboTipoChave);

        return painelPix;
    }

    private JPanel criarPainelCartao() {
        painelCartao = new JPanel(new GridLayout(5, 2, 5, 5));
        campoNumeroCartao = new JTextField();
        campoNomeTitular = new JTextField();
        campoValidade = new JTextField();
        campoCvv = new JTextField();
        spinnerParcelas = new JSpinner(new SpinnerNumberModel(1, 1, PagamentoCartao.MAX_PARCELAS, 1));

        painelCartao.add(new JLabel("Número do cartão:"));
        painelCartao.add(campoNumeroCartao);
        painelCartao.add(new JLabel("Nome do titular:"));
        painelCartao.add(campoNomeTitular);
        painelCartao.add(new JLabel("Validade (MM/AA):"));
        painelCartao.add(campoValidade);
        painelCartao.add(new JLabel("CVV:"));
        painelCartao.add(campoCvv);
        painelCartao.add(new JLabel("Parcelas:"));
        painelCartao.add(spinnerParcelas);

        return painelCartao;
    }

    private JPanel criarPainelBotao() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoFinalizar = new JButton("Finalizar Venda");
        botaoFinalizar.addActionListener(e -> finalizarVenda());
        painel.add(botaoFinalizar);
        return painel;
    }

    /**
     * A tela só coleta os dados e mostra o resultado.
     * Validação e regras de negócio ficam no modelo e no VendaService.
     */
    private void finalizarVenda() {
        try {
            Cliente cliente = (Cliente) comboClientes.getSelectedItem();
            Veiculo veiculo = (Veiculo) comboVeiculos.getSelectedItem();
            Vendedor vendedor = (Vendedor) comboVendedores.getSelectedItem();

            if (cliente == null || veiculo == null || vendedor == null) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um cliente, um veículo e um vendedor.",
                        "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DadosPagamento dadosPagamento;
            if ("PIX".equals(comboFormaPagamento.getSelectedItem())) {
                dadosPagamento = new DadosPix(
                        campoChavePix.getText(), (String) comboTipoChave.getSelectedItem());
            } else {
                dadosPagamento = new DadosCartao(
                        campoNumeroCartao.getText(), campoNomeTitular.getText(),
                        campoValidade.getText(), campoCvv.getText(),
                        (int) spinnerParcelas.getValue());
            }

            DadosVenda dados = new DadosVenda(cliente.getIdCliente(), vendedor.getIdVendedor(),
                    veiculo.getIdVeiculo(), dadosPagamento);
            Pagamento pagamento = vendaService.vender(dados);

            JOptionPane.showMessageDialog(this,
                    "Venda realizada com sucesso!\n" + pagamento.getVenda().obterRecibo(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();
            recarregarVeiculos();

        } catch (VeiculoIndisponivelException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Veículo indisponível", JOptionPane.ERROR_MESSAGE);
        } catch (VendaInvalidaException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Venda inválida", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao finalizar a venda: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // O veículo vendido precisa sair da lista de disponíveis, e a lista precisa
    // sempre refletir status atualizados (ex.: um que foi para manutenção).
    private void recarregarVeiculos() {
        comboVeiculos.setModel(new DefaultComboBoxModel<>(
                veiculoService.listarDisponiveis().toArray(new Veiculo[0])));
    }

    private void limparFormulario() {
        campoChavePix.setText("");
        campoNumeroCartao.setText("");
        campoNomeTitular.setText("");
        campoValidade.setText("");
        campoCvv.setText("");
        spinnerParcelas.setValue(1);
    }
}