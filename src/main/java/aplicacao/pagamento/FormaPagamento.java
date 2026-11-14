package aplicacao.pagamento;

import aplicacao.dto.DadosPagamento;
import dominio.modelo.Pagamento;
import dominio.modelo.Venda;

/**
 * Uma forma de pagamento sabe montar o seu Pagamento a partir dos dados de entrada
 * e gravar o seu detalhe (tabela própria). Para criar uma forma nova basta uma
 * implementação desta interface + um DTO + uma linha no AppConfig: o VendaService não muda.
 *
 * @param <D> tipo dos dados de entrada aceitos por esta forma
 * @param <P> tipo de pagamento que ela cria
 */
public interface FormaPagamento<D extends DadosPagamento, P extends Pagamento> {

    /**
     * Monta o pagamento da venda, já com o valor final calculado pela política de desconto.
     * Não grava nada.
     *
     * @param venda venda que está sendo paga (o valor base é o preço do veículo)
     * @param dados dados digitados para esta forma de pagamento
     * @return o pagamento pronto para ser registrado na venda
     * @throws IllegalArgumentException se os dados forem inválidos
     */
    P criarPagamento(Venda venda, D dados);

    /**
     * Grava os dados próprios desta forma (tabela de detalhe). É chamado dentro da transação
     * da venda, depois que a parte comum do pagamento já foi gravada e tem ID.
     *
     * @param pagamento pagamento já gravado na tabela pagamento
     */
    void salvarDetalhe(P pagamento);
}
