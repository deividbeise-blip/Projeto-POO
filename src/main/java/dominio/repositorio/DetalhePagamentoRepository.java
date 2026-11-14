package dominio.repositorio;

import dominio.modelo.Pagamento;

/**
 * Grava os dados específicos de uma forma de pagamento (PIX, cartão...),
 * depois que a parte comum já foi gravada pelo PagamentoRepository.
 * Cada forma de pagamento que tem tabela própria ganha uma implementação.
 *
 * @param <T> o tipo de pagamento cujo detalhe é gravado
 */
public interface DetalhePagamentoRepository<T extends Pagamento> {

    /**
     * Grava o detalhe e preenche o ID do registro de detalhe.
     *
     * @param pagamento pagamento cuja parte comum já tem ID
     */
    void salvar(T pagamento);
}
