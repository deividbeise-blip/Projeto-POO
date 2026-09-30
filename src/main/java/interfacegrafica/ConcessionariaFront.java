package interfacegrafica;

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

import dao.ClienteDAO;
import dao.VeiculoDAO;
import dao.VendedorDAO;
import excecao.VeiculoIndisponivelException;
import excecao.VendaInvalidaException;
import modelo.Cliente;
import modelo.Pagamento;
import modelo.Veiculo;
import modelo.Vendedor;
import servico.VendaService;

public class ConcessionariaFront extends JPanel {

    private final VendaService vendaService = new VendaService();

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

    public ConcessionariaFront() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelCliente(), BorderLayout.NORTH);
        add(criarPainelVendaEPagamento(), BorderLayout.CENTER);
        add(criarPainelBotao(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelCliente() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Cliente"));

        List<Cliente> clientes = new ClienteDAO().listar();
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

        List<Veiculo> veiculosDisponiveis = new VeiculoDAO().listarDisponiveis();
        comboVeiculos = new JComboBox<>(veiculosDisponiveis.toArray(new Veiculo[0]));

        List<Vendedor> vendedores = new VendedorDAO().listar();
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
        comboTipoChave = new JComboBox<>(new String[]{"CPF", "CNPJ", "E-mail", "Telefone", "Aleatória"});

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
        spinnerParcelas = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));

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

            Pagamento pagamento;
            if ("PIX".equals(comboFormaPagamento.getSelectedItem())) {
                pagamento = vendaService.venderComPix(cliente, vendedor, veiculo,
                        campoChavePix.getText(), (String) comboTipoChave.getSelectedItem());
            } else {
                pagamento = vendaService.venderComCartao(cliente, vendedor, veiculo,
                        campoNumeroCartao.getText(), campoNomeTitular.getText(),
                        campoValidade.getText(), campoCvv.getText(),
                        (int) spinnerParcelas.getValue());
            }

            JOptionPane.showMessageDialog(this,
                    "Venda realizada com sucesso!\n" + pagamento.obterReciboDetalhado(),
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

    // O veículo vendido precisa sair da lista de disponíveis
    private void recarregarVeiculos() {
        comboVeiculos.setModel(new DefaultComboBoxModel<>(
                new VeiculoDAO().listarDisponiveis().toArray(new Veiculo[0])));
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
