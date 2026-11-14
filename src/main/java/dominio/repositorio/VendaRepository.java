package dominio.repositorio;

import dominio.modelo.Venda;
import dominio.modelo.ResumoVenda;
import java.util.List;

public interface VendaRepository {

    /** Grava a venda (ainda sem pagamento) e preenche o ID gerado. */
    void salvar(Venda venda);

    /** Liga a venda ao pagamento depois que o pagamento já foi gravado. */
    void atualizarPagamento(Venda venda);

    /** Lista vendas concluídas com os dados necessários ao histórico. */
    List<ResumoVenda> listarHistorico();
}
