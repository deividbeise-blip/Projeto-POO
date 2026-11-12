package apresentacao.comum;

import javax.swing.JButton;

/**
 * Tela que tem uma ação principal (ex.: "Cadastrar"). Quando ela fica visível, a
 * {@code TelaPrincipal} torna esse botão o padrão da janela, então Enter o aciona.
 */
public interface TelaComBotaoPrincipal {

    /**
     * Botão acionado pelo Enter enquanto esta tela está visível.
     *
     * @return o botão principal da tela
     */
    JButton botaoPrincipal();
}
