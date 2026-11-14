package apresentacao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import dominio.modelo.Cliente;

/** Alimenta a JTable da tela de clientes: nome, CPF, e-mail e telefone. */
public class ClienteTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {"Nome", "CPF", "E-mail", "Telefone"};

    private List<Cliente> clientes = new ArrayList<>();

    /**
     * Substitui a lista inteira (chamar na thread da tela).
     * Guarda uma cópia, então mudar a lista original depois não afeta a tabela.
     *
     * @param novos clientes a exibir
     */
    public void setClientes(List<Cliente> novos) {
        this.clientes = new ArrayList<>(novos);
        fireTableDataChanged();
    }

    /**
     * Cliente de uma linha da tabela.
     *
     * @param linha índice da linha no modelo (use {@code convertRowIndexToModel} se houver ordenação/filtro)
     * @return o cliente daquela linha
     * @throws IndexOutOfBoundsException se a linha não existir no modelo
     */
    public Cliente getCliente(int linha) {
        return clientes.get(linha);
    }

    /** @return quantos clientes estão carregados no modelo (antes de qualquer filtro) */
    @Override
    public int getRowCount() {
        return clientes.size();
    }

    /** @return sempre 4: nome, CPF, e-mail e telefone */
    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    /**
     * @param coluna índice da coluna (0 a 3)
     * @return o título exibido no cabeçalho
     */
    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    /**
     * Valor exibido na célula. CPF e telefone saem com máscara; o modelo guarda só os dígitos.
     *
     * @param linha índice da linha no modelo
     * @param coluna índice da coluna (0 a 3)
     * @return o texto da célula, ou null para coluna desconhecida
     */
    @Override
    public Object getValueAt(int linha, int coluna) {
        Cliente cliente = clientes.get(linha);
        return switch (coluna) {
            case 0 -> cliente.getNome();
            case 1 -> formatarCpf(cliente.getCpf());
            case 2 -> cliente.getEmail();
            case 3 -> formatarTelefone(cliente.getTelefone());
            default -> null;
        };
    }

    /**
     * 12345678901 -> 123.456.789-01 (se não tiver 11 dígitos, devolve como está).
     *
     * @param cpf CPF só com dígitos
     * @return o CPF com máscara, ou o próprio valor (inclusive null) se não tiver 11 caracteres
     */
    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    /**
     * 11999998888 -> (11) 99999-8888; 1133334444 -> (11) 3333-4444.
     *
     * @param telefone telefone só com dígitos (DDD + número)
     * @return o telefone com máscara, ou o próprio valor (inclusive null) se não tiver 10 ou 11 caracteres
     */
    public static String formatarTelefone(String telefone) {
        if (telefone == null) {
            return null;
        }
        if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + "-" + telefone.substring(7);
        }
        if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + "-" + telefone.substring(6);
        }
        return telefone;
    }
}