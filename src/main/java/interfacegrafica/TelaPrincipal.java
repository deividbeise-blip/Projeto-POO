package interfacegrafica;

import javax.swing.*;
import java.awt.Component;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Concessionária Grupo 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        ClienteFront painelClientes = new ClienteFront();
        VeiculoFront painelVeiculos = new VeiculoFront();
        ConcessionariaFront painelVendas = new ConcessionariaFront();

        abas.addTab("Clientes", painelClientes);
        abas.addTab("Veículos", painelVeiculos);
        abas.addTab("Vendas", painelVendas);

        // Sempre que o usuário troca de aba, recarrega os dados dela a partir
        // do banco, para refletir cadastros e vendas feitos em outras abas
        // (sem isso, cada combo só carregava uma vez, na abertura do programa).
        abas.addChangeListener(e -> {
            Component selecionado = abas.getSelectedComponent();
            if (selecionado instanceof VeiculoFront veiculoFront) {
                veiculoFront.atualizarDados();
            } else if (selecionado instanceof ConcessionariaFront concessionariaFront) {
                concessionariaFront.atualizarDados();
            }
        });

        add(abas);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}
