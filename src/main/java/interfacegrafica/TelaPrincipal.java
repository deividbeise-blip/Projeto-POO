package interfacegrafica;
import javax.swing.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Concessionária Grupo 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        JPanel painelClientes = new ClienteFront();
        JPanel painelVeiculos = new VeiculoFront();
        JPanel painelVendas = new ConcessionariaFront();

        abas.addTab("Clientes", painelClientes);
        abas.addTab("Veículos", painelVeiculos);
        abas.addTab("Vendas", painelVendas);

        add(abas);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}