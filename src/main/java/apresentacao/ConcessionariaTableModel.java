package apresentacao;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import dominio.modelo.Concessionaria;

/** Alimenta a JTable da tela de concessionárias: nome e endereço. */
public class ConcessionariaTableModel extends AbstractTableModel {

    private static final String[] COLUNAS = {"Nome", "Endereço"};

    private List<Concessionaria> concessionarias = new ArrayList<>();

    /**
     * Substitui a lista inteira (chamar na thread da tela).
     *
     * @param novas concessionárias a mostrar
     */
    public void setConcessionarias(List<Concessionaria> novas) {
        this.concessionarias = new ArrayList<>(novas);
        fireTableDataChanged();
    }

    /**
     * Concessionária de uma linha da tabela.
     *
     * @param linha índice da linha no modelo (use {@code convertRowIndexToModel} se houver ordenação/filtro)
     * @return a concessionária daquela linha
     */
    public Concessionaria getConcessionaria(int linha) {
        return concessionarias.get(linha);
    }

    @Override
    public int getRowCount() {
        return concessionarias.size();
    }

    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Concessionaria concessionaria = concessionarias.get(linha);
        return switch (coluna) {
            case 0 -> concessionaria.getNome();
            case 1 -> concessionaria.getEndereco();
            default -> null;
        };
    }
}
