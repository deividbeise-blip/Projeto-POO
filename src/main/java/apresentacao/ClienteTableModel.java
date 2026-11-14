package apresentacao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import dominio.modelo.Cliente;

/**
 * Modelo de dados da {@code JTable} da tela de clientes. Mostra quatro colunas somente leitura:
 * nome, CPF, e-mail e telefone. O CPF e o telefone aparecem formatados, mas o {@link Cliente}
 * guarda só os dígitos.
 * <p>
 * Deve ser usado apenas na thread da tela (Swing).
 *
 * @see ClienteFront
 */
public class ClienteTableModel extends AbstractTableModel {

    /** Títulos das colunas, na ordem em que aparecem na tabela. */
    private static final String[] COLUNAS = {"Nome", "CPF", "E-mail", "Telefone"};

    /** Clientes mostrados no momento; o índice da lista é o índice da linha no modelo. */
    private List<Cliente> clientes = new ArrayList<>();

    /**
     * Substitui a lista inteira e avisa a tabela para se redesenhar. Isso também limpa a seleção
     * da tabela. Chamar na thread da tela.
     *
     * @param novos os clientes a mostrar (a lista é copiada)
     */
    public void setClientes(List<Cliente> novos) {
        this.clientes = new ArrayList<>(novos);
        fireTableDataChanged();
    }

    /**
     * Devolve o cliente de uma linha.
     *
     * @param linha índice da linha no <b>modelo</b> (se a tabela estiver ordenada ou filtrada,
     *              converta antes com {@code JTable.convertRowIndexToModel})
     * @return o cliente daquela linha
     * @throws IndexOutOfBoundsException se a linha não existir
     */
    public Cliente getCliente(int linha) {
        return clientes.get(linha);
    }

    /** @return quantos clientes estão na lista */
    @Override
    public int getRowCount() {
        return clientes.size();
    }

    /** @return o número de colunas (4: nome, CPF, e-mail e telefone) */
    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    /**
     * @param coluna índice da coluna
     * @return o título da coluna
     */
    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    /**
     * Devolve o texto de uma célula. As células não são editáveis.
     *
     * @param linha  índice da linha no modelo
     * @param coluna 0 = nome, 1 = CPF (formatado), 2 = e-mail, 3 = telefone (formatado)
     * @return o texto da célula, ou {@code null} se a coluna não existir
     */
    @Override
    public Object getValueAt(int linha, int coluna) {
        Cliente cliente = clientes.get(linha);
        return switch (coluna) {
            case 0 -> cliente.getName_cliente();
            case 1 -> formatarCpf(cliente.getCpf_cliente());
            case 2 -> cliente.getEmail_cliente();
            case 3 -> formatarTelefone(cliente.getPhone_cliente());
            default -> null;
        };
    }

    /**
     * Formata um CPF para exibição: {@code 12345678901} vira {@code 123.456.789-01}.
     *
     * @param cpf o CPF só com dígitos
     * @return o CPF formatado; se não tiver 11 dígitos, devolve como veio
     */
    public static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    /**
     * Formata um telefone para exibição: {@code 11999998888} vira {@code (11) 99999-8888} e
     * {@code 1133334444} vira {@code (11) 3333-4444}.
     *
     * @param telefone o telefone só com dígitos
     * @return o telefone formatado; se não tiver 10 nem 11 dígitos, devolve como veio
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