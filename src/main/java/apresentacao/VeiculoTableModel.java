package apresentacao;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

import apresentacao.estilo.Tema;
import dominio.modelo.Formatacao;
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

    /** @return quantos veículos estão carregados no modelo (antes de qualquer filtro) */
    @Override
    public int getRowCount() {
        return veiculos.size();
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
     * Tipo de cada coluna, para a tabela ordenar pelo valor real.
     *
     * @param coluna índice da coluna no modelo
     * @return {@code Integer} para o ano, {@code Double} para o preço, {@code StatusVeiculo} para o status
     *         e {@code String} para as demais
     */
    @Override
    public Class<?> getColumnClass(int coluna) {
        return switch (coluna) {
            case 2 -> Integer.class;
            case COLUNA_PRECO -> Double.class;
            case COLUNA_STATUS -> StatusVeiculo.class;
            default -> String.class;
        };
    }

    /**
     * Valor bruto da célula. Preço e status saem sem formatação; o {@link VeiculoCelulaRenderer} os desenha.
     *
     * @param linha índice da linha no modelo
     * @param coluna índice da coluna no modelo
     * @return o valor da célula, ou null para coluna desconhecida
     */
    @Override
    public Object getValueAt(int linha, int coluna) {
        Veiculo veiculo = veiculos.get(linha);
        return switch (coluna) {
            case 0 -> veiculo.getMarca();
            case 1 -> veiculo.getModelo();
            case 2 -> veiculo.getAno();
            case 3 -> veiculo.getPlaca();
            case COLUNA_PRECO -> veiculo.getPreco();
            case 5 -> veiculo.getTipo();
            case COLUNA_STATUS -> veiculo.getStatus();
            case 7 -> veiculo.getConcessionaria().getNome();
            default -> null;
        };
    }

    /**
     * Formata no padrão brasileiro: 85000.0 -> "R$ 85.000,00" (ver {@link Formatacao#moeda(double)}).
     *
     * @param preco o valor
     * @return o texto com "R$", ou vazio se for nulo
     */
    public static String formatarPreco(Double preco) {
        return preco == null ? "" : Formatacao.moeda(preco);
    }

    /**
     * Formata só o número, sem "R$": 85000.5 -> "85.000,50" (ver {@link Formatacao#valor(double)}).
     * Usado para preencher o campo do formulário.
     *
     * @param preco o valor
     * @return o texto, ou vazio se for nulo
     */
    public static String formatarValor(Double preco) {
        return preco == null ? "" : Formatacao.valor(preco);
    }

    /**
     * Texto de um tipo de veículo para o usuário: só a primeira letra maiúscula
     * ("CARRO" vira "Carro"), igual a {@code Veiculo.getTipo()} da subclasse.
     * Usado nos combos, que recebem os tipos do serviço em maiúsculas.
     *
     * @param tipo nome do tipo, em qualquer caixa (ex.: "CARRO")
     * @return o texto para a tela, ou vazio se for nulo ou vazio
     */
    public static String textoTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "";
        }
        String limpo = tipo.trim();
        return limpo.substring(0, 1).toUpperCase(Locale.ROOT) + limpo.substring(1).toLowerCase(Locale.ROOT);
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
     * @return {@code Tema.sucesso()}, {@code Tema.aviso()} ou {@code Tema.neutro()}
     */
    public static Color corStatus(StatusVeiculo status) {
        return switch (status) {
            case DISPONIVEL -> Tema.sucesso();
            case EM_MANUTENCAO -> Tema.aviso();
            case VENDIDO -> Tema.neutro();
        };
    }
}
