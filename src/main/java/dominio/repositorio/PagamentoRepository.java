package dominio.repositorio;

import dominio.modelo.Pagamento;

/** Grava a parte comum de qualquer pagamento (tabela pagamento). */
public interface PagamentoRepository {

    /**
     * Grava forma, valor, data, cliente e veículo do pagamento e preenche o ID gerado.
     * Deve rodar antes do detalhe (PIX, cartão), que referencia esse ID.
     *
     * @param pagamento pagamento ligado a uma venda já gravada
     * @throws IllegalArgumentException se a forma de pagamento não tiver equivalente no banco
     */
    void salvar(Pagamento pagamento);
}
