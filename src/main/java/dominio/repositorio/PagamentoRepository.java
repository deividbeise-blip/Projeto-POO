package dominio.repositorio;

import dominio.modelo.Pagamento;

/** Grava a parte comum de qualquer pagamento (tabela pagamento). */
public interface PagamentoRepository {

    void salvar(Pagamento pagamento);
}
