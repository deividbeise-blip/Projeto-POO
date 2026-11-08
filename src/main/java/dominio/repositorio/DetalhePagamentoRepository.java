package dominio.repositorio;

import dominio.modelo.Pagamento;

/**
 * Grava os dados específicos de uma forma de pagamento (PIX, cartão...),
 * depois que a parte comum já foi gravada pelo PagamentoRepository.
 */
public interface DetalhePagamentoRepository<T extends Pagamento> {

    void salvar(T pagamento);
}
