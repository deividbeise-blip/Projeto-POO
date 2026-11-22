package apresentacao;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

import dominio.modelo.Vendedor;

/**
 * Alimenta a JTable da tela de vendedores: nome, CPF, comissão (%) e concessionária.
 * A comissão sai como {@link Double}, para a tabela ordenar pelo valor; a tela a desenha
 * com {@link #formatarComissao(Double)}.
 */
public class VendedorTableModel extends AbstractTableModel {

    /** Índice da coluna de comissão. */
    public static final int COLUNA_COMISSAO = 2;

    private static final String[] COLUNAS = {"Nome", "CPF", "Comissão (%)", "Concessionária"};
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private List<Vendedor> vendedores = new ArrayList<>();

    /**
     * Substitui a lista inteira (chamar na thread da tela).
     *
     * @param novos vendedores a mostrar
     */
    public void setVendedores(List<Vendedor> novos) {
        this.vendedores = new ArrayList<>(novos);
        fireTableDataChanged();
    }

    /**
     * Vendedor de uma linha da tabela.
     *
     * @param linha índice da linha no modelo (use {@code convertRowIndexToModel} se houver ordenação/filtro)
     * @return o vendedor daquela linha
     */
    public Vendedor getVendedor(int linha) {
        return vendedores.get(linha);
    }

    /** @return quantos vendedores estão carregados no modelo (antes de qualquer filtro) */
    @Override
    public int getRowCount() {
        return vendedores.size();
    }

    /** @return número de colunas da tabela */
    @Override
    public int getColumnCount() {
        return COLUNAS.length;
    }

    /**
     * @param coluna índice da coluna no modelo
     * @return o título exibido no cabeçalho
     */
    @Override
    public String getColumnName(int coluna) {
        return COLUNAS[coluna];
    }

    /**
     * @param coluna índice da coluna no modelo
     * @return {@code Double} para a comissão (ordenação numérica) e {@code String} para as demais
     */
    @Override
    public Class<?> getColumnClass(int coluna) {
        return coluna == COLUNA_COMISSAO ? Double.class : String.class;
    }

    /**
     * Valor da célula. O CPF sai com máscara; a comissão sai como número e a tela a formata.
     *
     * @param linha índice da linha no modelo
     * @param coluna índice da coluna no modelo
     * @return o valor da célula, ou null para coluna desconhecida
     */
    @Override
    public Object getValueAt(int linha, int coluna) {
        Vendedor vendedor = vendedores.get(linha);
        return switch (coluna) {
            case 0 -> vendedor.getNome();
            case 1 -> ClienteTableModel.formatarCpf(vendedor.getCpf());
            case COLUNA_COMISSAO -> vendedor.getComissaoPercentual();
            case 3 -> vendedor.getConcessionaria().getNome();
            default -> null;
        };
    }

    /**
     * Formata a comissão no padrão brasileiro, sem casas decimais sobrando: 2.5 -> "2,5"; 3.0 -> "3".
     *
     * @param comissao o percentual
     * @return o texto, ou vazio se for nulo
     */
    public static String formatarComissao(Double comissao) {
        if (comissao == null) {
            return "";
        }
        // DecimalFormat não é thread-safe; criar um por chamada é barato e só roda na thread da tela.
        return new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(BRASIL)).format(comissao);
    }
}
