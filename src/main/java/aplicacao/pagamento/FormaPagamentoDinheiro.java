package aplicacao.pagamento;

import aplicacao.dto.DadosDinheiro;
import dominio.modelo.PagamentoDinheiro;
import dominio.modelo.Venda;
import dominio.politica.PoliticaDesconto;

import java.time.LocalDate;

/**
 * Forma de pagamento em dinheiro. Existe para mostrar o Aberto/Fechado: foi adicionada
 * só com esta classe, o DTO, o modelo e uma linha no AppConfig, sem alterar o VendaService.
 */
public class FormaPagamentoDinheiro implements FormaPagamento<DadosDinheiro, PagamentoDinheiro> {

    private final PoliticaDesconto politicaDesconto;

    /**
     * @param politicaDesconto regra aplicada ao preço do veículo (o AppConfig entrega SemDesconto)
     */
    public FormaPagamentoDinheiro(PoliticaDesconto politicaDesconto) {
        this.politicaDesconto = politicaDesconto;
    }

    /**
     * Cria o pagamento em dinheiro usando o preço do veículo e a data de hoje.
     *
     * @param venda venda que está sendo paga
     * @param dados sem conteúdo; só identifica a forma escolhida
     * @return o pagamento com o valor final já calculado
     */
    @Override
    public PagamentoDinheiro criarPagamento(Venda venda, DadosDinheiro dados) {
        return new PagamentoDinheiro(venda, venda.getVeiculo().getPreco(), LocalDate.now(), politicaDesconto);
    }

    /**
     * Não grava nada: o banco não tem tabela de detalhe para dinheiro, e a parte comum
     * (forma 'Dinheiro', valor e data) já foi gravada na tabela pagamento pelo PagamentoRepository.
     *
     * @param pagamento pagamento já gravado na tabela pagamento
     */
    @Override
    public void salvarDetalhe(PagamentoDinheiro pagamento) {
        // Intencionalmente vazio: não existe tabela pagamentodinheiro.
    }
}
