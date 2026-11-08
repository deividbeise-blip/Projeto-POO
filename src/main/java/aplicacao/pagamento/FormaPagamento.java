package aplicacao.pagamento;

import aplicacao.dto.DadosPagamento;
import dominio.modelo.Pagamento;
import dominio.modelo.Venda;

/**
 * Uma forma de pagamento sabe montar o seu Pagamento a partir dos dados de entrada
 * e gravar o seu detalhe (tabela própria). Para criar uma forma nova basta uma
 * implementação desta interface + um DTO + uma linha no AppConfig: o VendaService não muda.
 */
public interface FormaPagamento<D extends DadosPagamento, P extends Pagamento> {

    P criarPagamento(Venda venda, D dados);

    void salvarDetalhe(P pagamento);
}
