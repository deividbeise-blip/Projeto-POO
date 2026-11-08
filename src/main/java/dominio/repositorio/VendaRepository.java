package dominio.repositorio;

import dominio.modelo.Venda;

public interface VendaRepository {

    /** Grava a venda (ainda sem pagamento) e preenche o ID gerado. */
    void salvar(Venda venda);

    /** Liga a venda ao pagamento depois que o pagamento já foi gravado. */
    void atualizarPagamento(Venda venda);
}
