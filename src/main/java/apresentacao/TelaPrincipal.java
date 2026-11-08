package apresentacao;

import config.AppConfig;

import javax.swing.*;
import java.awt.Component;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal(AppConfig config) {
        setTitle("Concessionária Grupo 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        ClienteFront painelClientes = new ClienteFront(config.clienteService());
        VeiculoFront painelVeiculos = new VeiculoFront(
                config.veiculoService(),
                config.veiculoRepository(),
                config.concessionariaRepository());
        ConcessionariaFront painelVendas = new ConcessionariaFront(
                config.vendaService(),
                config.clienteRepository(),
                config.veiculoRepository(),
                config.vendedorRepository());

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
            TelaPrincipal tela = new TelaPrincipal(new AppConfig());
            tela.setVisible(true);
        });
    }
}
