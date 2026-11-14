package apresentacao;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import apresentacao.estilo.Tema;
import dominio.modelo.StatusVeiculo;

/**
 * Desenha as células especiais da tabela de veículos: o preço em reais ("R$ 85.000,00",
 * alinhado à direita) e o status com texto e cor próprios (verde, amarelo ou cinza).
 * Registre com {@code tabela.setDefaultRenderer(Double.class, ...)} e
 * {@code tabela.setDefaultRenderer(StatusVeiculo.class, ...)}.
 */
public class VeiculoCelulaRenderer extends DefaultTableCellRenderer {

    /**
     * Formata a célula conforme o tipo do valor: {@code Double} vira preço em reais alinhado à direita;
     * {@code StatusVeiculo} vira texto em negrito com a cor do status (exceto na linha selecionada).
     * Outros valores são desenhados normalmente.
     *
     * @param tabela a tabela que está sendo desenhada
     * @param valor valor da célula, vindo do {@link VeiculoTableModel}
     * @param selecionada se a linha está selecionada
     * @param comFoco se a célula está com o foco
     * @param linha índice da linha na visão
     * @param coluna índice da coluna na visão
     * @return o rótulo configurado para desenhar a célula
     */
    @Override
    public Component getTableCellRendererComponent(JTable tabela, Object valor, boolean selecionada,
                                                   boolean comFoco, int linha, int coluna) {
        JLabel celula = (JLabel) super.getTableCellRendererComponent(
                tabela, valor, selecionada, comFoco, linha, coluna);
        celula.setHorizontalAlignment(SwingConstants.LEADING);
        celula.setFont(tabela.getFont());

        if (valor instanceof Double preco) {
            celula.setText(VeiculoTableModel.formatarPreco(preco));
            celula.setHorizontalAlignment(SwingConstants.TRAILING);
        } else if (valor instanceof StatusVeiculo status) {
            celula.setText(VeiculoTableModel.textoStatus(status));
            celula.setFont(Tema.BOTAO);
            // Selecionada, a linha tem fundo escuro: mantém a cor de seleção para o texto continuar legível.
            if (!selecionada) {
                celula.setForeground(VeiculoTableModel.corStatus(status));
            }
        }
        return celula;
    }
}
