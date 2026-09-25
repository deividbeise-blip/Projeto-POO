package interfacegrafica;
import javax.swing.*;


public class TelaPrincipal extends JFrame {

    //Configurações de janela
    public TelaPrincipal() {
        setTitle("Concessionária Grupo 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fechar tela no X
        setLocationRelativeTo(null); //qual posaeção a tela vai aparecer


        //criação das abas
        JTabbedPane abas = new JTabbedPane();

        //painéis
        JPanel painelClientes = new JPanel();
        JPanel painelVendas = new JPanel();
        JPanel painelVeiculos = new JPanel();

        //Conteudo temporario
        painelClientes.add(new JLabel("Cadastro de Clientes"));
        painelVeiculos.add(new JLabel("Cadastro de Veículos"));
        painelVendas.add(new JLabel("Realização de Vendas"));

        //Adicionando os painéis às abas
        abas.addTab("Clientes", painelClientes);
        abas.addTab("Veículos", painelVeiculos);
        abas.addTab("Vendas", painelVendas);

        //Adicionando as abas pra janela
        add(abas);
    }

    //comando para ativar a execução da tela
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });

    }
}
