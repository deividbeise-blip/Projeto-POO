package apresentacao;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

import apresentacao.estilo.Tema;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;

/**
 * Alimenta a JTable da tela de veículos: marca, modelo, ano, placa, preço, tipo, status e concessionária.
 * O preço sai como {@link Double} e o status como {@link StatusVeiculo}, para a tabela ordenar
 * pelo valor real; quem desenha o texto formatado e a cor é o {@link VeiculoCelulaRenderer}.
 */
public class VeiculoTableModel extends AbstractTableModel {

    /** Índice da coluna de preço. */
    public static final int COLUNA_PRECO = 4;
    /** Índice da coluna de status. */
    public static final int COLUNA_STATUS = 6;

    private static final String[] COLUNAS =
            {"Marca", "Modelo", "Ano", "Placa", "Preço", "Tipo", "Status", "Concessionária"};

    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    private List<Veiculo> veiculos = new ArrayList<>();

    /**
     * Substitui a lista inteira (chamar na thread da tela).
     *
     * @param novos veículos a mostrar
     */
    public void setVeiculos(List<Veiculo> novos) {
        this.veiculos = new ArrayList<>(novos);
        fireTableDataChanged();
    }

    /**
     * Veículo de uma linha da tabela.
     *
     * @param linha índice da linha no modelo (use {@code convertRowIndexToModel} se houver ordenação/filtro)
     * @return o veículo daquela linha
     */
    public Veiculo getVeiculo(int linha) {
        return veiculos.get(linha);
    }

    /**
     * Procura a linha do veículo com esse ID.
     *
     * @param id ID do veículo
     * @return índice da linha no modelo, ou -1 se não estiver na lista
     */
    public int linhaDoVeiculo(Long id) {
        for (int i = 0; i < veiculos.size(); i++) {
            if (veiculos.get(i).getIdVeiculo().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getRowCount() {
        return veiculos.size();
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
    public Class<?> getColumnClass(int coluna) {
        return switch (coluna) {
            case 2 -> Integer.class;
            case COLUNA_PRECO -> Double.class;
            case COLUNA_STATUS -> StatusVeiculo.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Veiculo veiculo = veiculos.get(linha);
        return switch (coluna) {
            case 0 -> veiculo.getMarca();
            case 1 -> veiculo.getModelo();
            case 2 -> veiculo.getAno();
            case 3 -> veiculo.getPlaca();
            case COLUNA_PRECO -> veiculo.getPreco();
            case 5 -> textoTipo(veiculo.isMoto());
            case COLUNA_STATUS -> veiculo.getStatus();
            case 7 -> veiculo.getConcessionaria().getNome();
            default -> null;
        };
    }

    /**
     * Formata no padrão brasileiro: 85000.0 -> "R$ 85.000,00".
     *
     * @param preco o valor
     * @return o texto com "R$", ou vazio se for nulo
     */
    public static String formatarPreco(Double preco) {
        return preco == null ? "" : "R$ " + formatarValor(preco);
    }

    /**
     * Formata só o número, sem "R$": 85000.5 -> "85.000,50". Usado para preencher o campo do formulário.
     *
     * @param preco o valor
     * @return o texto, ou vazio se for nulo
     */
    public static String formatarValor(Double preco) {
        if (preco == null) {
            return "";
        }
        // DecimalFormat não é thread-safe; criar um por chamada é barato e só roda na thread da tela.
        return new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(BRASIL)).format(preco);
    }

    /**
     * Texto do tipo do veículo.
     *
     * @param moto valor de {@code Veiculo.isMoto()}
     * @return "Moto" ou "Carro"
     */
    public static String textoTipo(Boolean moto) {
        return Boolean.TRUE.equals(moto) ? "Moto" : "Carro";
    }

    /**
     * Texto do status para o usuário.
     *
     * @param status o status do veículo
     * @return "Disponível", "Em manutenção" ou "Vendido"
     */
    public static String textoStatus(StatusVeiculo status) {
        return switch (status) {
            case DISPONIVEL -> "Disponível";
            case EM_MANUTENCAO -> "Em manutenção";
            case VENDIDO -> "Vendido";
        };
    }

    /**
     * Cor do status na tabela.
     *
     * @param status o status do veículo
     * @return {@code Tema.SUCESSO}, {@code Tema.AVISO} ou {@code Tema.NEUTRO}
     */
    public static Color corStatus(StatusVeiculo status) {
        return switch (status) {
            case DISPONIVEL -> Tema.SUCESSO;
            case EM_MANUTENCAO -> Tema.AVISO;
            case VENDIDO -> Tema.NEUTRO;
        };
    }
}
