package interfacegrafica;
import javax.swing.*;


public class TelaPrincipal extends JFrame {

    //Configurações de janela
    public TelaPrincipal() {
        setTitle("Concessionária Grupo 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fechar tela no X
        setLocationRelativeTo(null); //qual posaeção a tela vai aparecer
    }

    //criação das abas
    JTabbedPane abas = new JTabbedPane();

    //painéis
    JPanel painelClientes =  new JPanel();
    JPanel painelVendas = new JPanel();
    JPanel painelVeiculos = new JPanel();

    //Conteudo temporario
        painelClientes.add(new JLabel("Cadastro de Clientes"));
        painelVeiculos.add(new JLabel("Cadastro de Veículos"));
        painelVendas.add(new JLabel("Realização de Vendas"));

}
