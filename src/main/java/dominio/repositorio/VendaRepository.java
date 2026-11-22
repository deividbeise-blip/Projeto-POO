package dominio.repositorio;

import dominio.modelo.Venda;
import dominio.modelo.ResumoVenda;
import java.util.List;

/** Onde as vendas são gravadas e de onde sai o histórico. */
public interface VendaRepository {

    /**
     * Grava a venda (ainda sem pagamento) e preenche o ID gerado.
     *
     * @param venda venda nova, com cliente, vendedor e veículo já gravados
     */
    void salvar(Venda venda);

    /**
     * Liga a venda ao pagamento depois que o pagamento já foi gravado.
     *
     * @param venda venda com ID e com um pagamento que também já tem ID
     */
    void atualizarPagamento(Venda venda);

    /**
     * Lista vendas concluídas com os dados necessários ao histórico.
     *
     * @return vendas da mais recente para a mais antiga, com a forma de pagamento
     *         já no formato de exibição ("PIX", "Cartão", "Dinheiro")
     */
    List<ResumoVenda> listarHistorico();
}
