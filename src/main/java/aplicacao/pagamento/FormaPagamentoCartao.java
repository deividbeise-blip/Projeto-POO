package aplicacao.pagamento;

import aplicacao.dto.DadosCartao;
import dominio.modelo.PagamentoCartao;
import dominio.modelo.Venda;
import dominio.politica.PoliticaDesconto;
import dominio.repositorio.DetalhePagamentoRepository;

import java.time.LocalDate;

/**
 * Forma de pagamento com cartão: cria o {@link PagamentoCartao} e grava o detalhe na tabela pagamentocartao.
 * O AppConfig entrega {@code SemDesconto}: cartão paga o preço cheio, à vista ou parcelado.
 */
public class FormaPagamentoCartao implements FormaPagamento<DadosCartao, PagamentoCartao> {

    private final DetalhePagamentoRepository<PagamentoCartao> detalheRepository;
    private final PoliticaDesconto politicaDesconto;

    /**
     * @param detalheRepository onde o detalhe do pagamento com cartão é gravado
     * @param politicaDesconto regra aplicada ao preço do veículo para chegar ao valor pago
     */
    public FormaPagamentoCartao(DetalhePagamentoRepository<PagamentoCartao> detalheRepository,
            PoliticaDesconto politicaDesconto) {
        this.detalheRepository = detalheRepository;
        this.politicaDesconto = politicaDesconto;
    }

    /**
     * Cria o pagamento com cartão usando o preço do veículo e a data de hoje.
     *
     * @param venda venda que está sendo paga
     * @param dados dados digitados para o pagamento com cartão
     * @return o pagamento com o valor final já calculado
     * @throws IllegalArgumentException se algum dado do pagamento com cartão for inválido
     */
    @Override
    public PagamentoCartao criarPagamento(Venda venda, DadosCartao dados) {
        return new PagamentoCartao(venda, venda.getVeiculo().getPreco(), LocalDate.now(),
                dados.numeroCartao(), dados.titular(), dados.validade(),
                dados.cvv(), dados.parcelas(), politicaDesconto);
    }

    /**
     * Grava os dados do pagamento com cartão na tabela pagamentocartao, ligados ao pagamento.
     *
     * @param pagamento pagamento já gravado na tabela pagamento
     */
    @Override
    public void salvarDetalhe(PagamentoCartao pagamento) {
        detalheRepository.salvar(pagamento);
    }
}
