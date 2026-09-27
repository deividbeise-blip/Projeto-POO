package interfacegrafica;

import dao.VeiculoDAO;
import dao.VendedorDAO;
import excecao.VeiculoIndisponivelException;
import modelo.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ConcessionariaFront extends JPanel {

    private JTextField campoNomeCliente;
    private JTextField campoCpfCliente;
    private JTextField campoEmailCliente;
    private JTextField campoTelefoneCliente;

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

        add(criarPainelDadosCliente(), BorderLayout.NORTH);
        add(criarPainelVendaEPagamento(), BorderLayout.CENTER);
        add(criarPainelBotao(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelDadosCliente() {
        JPanel painel = new JPanel(new GridLayout(4, 2, 5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));

        campoNomeCliente = new JTextField();
        campoCpfCliente = new JTextField();
        campoEmailCliente = new JTextField();
        campoTelefoneCliente = new JTextField();

        painel.add(new JLabel("Nome:"));
        painel.add(campoNomeCliente);
        painel.add(new JLabel("CPF:"));
        painel.add(campoCpfCliente);
        painel.add(new JLabel("E-mail:"));
        painel.add(campoEmailCliente);
        painel.add(new JLabel("Telefone:"));
        painel.add(campoTelefoneCliente);

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

    private void finalizarVenda() {
        try {
            if (!validarCamposCliente()) {
                JOptionPane.showMessageDialog(this,
                        "Preencha todos os dados do cliente.",
                        "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Veiculo veiculoSelecionado = (Veiculo) comboVeiculos.getSelectedItem();
            Vendedor vendedorSelecionado = (Vendedor) comboVendedores.getSelectedItem();

            if (veiculoSelecionado == null || vendedorSelecionado == null) {
                JOptionPane.showMessageDialog(this,
                        "Selecione um veículo e um vendedor.",
                        "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cliente cliente = new Cliente(
                    campoNomeCliente.getText(),
                    campoCpfCliente.getText(),
                    campoEmailCliente.getText(),
                    campoTelefoneCliente.getText()
            );

            String formaPagamento = (String) comboFormaPagamento.getSelectedItem();
            Double valorFinal = veiculoSelecionado.getPreco();

            // Venda é criada antes do pagamento pois Pagamento exige uma Venda no construtor
            Venda venda = new Venda(cliente, vendedorSelecionado, veiculoSelecionado,
                    LocalDate.now(), valorFinal, null);

            Pagamento pagamento;
            if ("PIX".equals(formaPagamento)) {
                if (campoChavePix.getText().isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Informe a chave PIX.", "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                pagamento = new PagamentoPix(venda, "PIX", valorFinal, LocalDate.now(),
                        campoChavePix.getText(), (String) comboTipoChave.getSelectedItem());
            } else {
                if (campoNumeroCartao.getText().isBlank() || campoNomeTitular.getText().isBlank()
                        || campoValidade.getText().isBlank() || campoCvv.getText().isBlank()) {
                    JOptionPane.showMessageDialog(this,
                            "Preencha todos os dados do cartão.", "Dados incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                PagamentoCartao pagamentoCartao = new PagamentoCartao(venda, "Cartão", valorFinal, LocalDate.now(),
                        campoNumeroCartao.getText(), campoNomeTitular.getText(),
                        campoValidade.getText(), campoCvv.getText());

                if (!pagamentoCartao.validarNumeroCartao()) {
                    JOptionPane.showMessageDialog(this,
                            "Número de cartão inválido. Deve conter 16 dígitos.",
                            "Erro de validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int parcelas = (int) spinnerParcelas.getValue();
                pagamentoCartao.calcularParcelas(parcelas);
                pagamento = pagamentoCartao;
            }

            venda.setPagamento(pagamento);

            // Regra de negócio disparada aqui: pode lançar VeiculoIndisponivelException
            venda.finalizarVenda();

            JOptionPane.showMessageDialog(this,
                    "Venda realizada com sucesso!\n" + pagamento.obterReciboDetalhado(),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();

        } catch (VeiculoIndisponivelException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Veículo indisponível", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro inesperado ao finalizar a venda: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCamposCliente() {
        return !campoNomeCliente.getText().isBlank()
                && !campoCpfCliente.getText().isBlank()
                && !campoEmailCliente.getText().isBlank()
                && !campoTelefoneCliente.getText().isBlank();
    }

    private void limparFormulario() {
        campoNomeCliente.setText("");
        campoCpfCliente.setText("");
        campoEmailCliente.setText("");
        campoTelefoneCliente.setText("");
        campoChavePix.setText("");
        campoNumeroCartao.setText("");
        campoNomeTitular.setText("");
        campoValidade.setText("");
        campoCvv.setText("");
        spinnerParcelas.setValue(1);
    }
}