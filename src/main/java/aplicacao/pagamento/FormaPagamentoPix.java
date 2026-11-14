package aplicacao.pagamento;

import aplicacao.dto.DadosPix;
import dominio.modelo.PagamentoPix;
import dominio.modelo.Venda;
import dominio.politica.PoliticaDesconto;
import dominio.repositorio.DetalhePagamentoRepository;

import java.time.LocalDate;

/**
 * Forma de pagamento com PIX: cria o {@link PagamentoPix} e grava o detalhe na tabela pagamentopix.
 * O AppConfig entrega {@code DescontoPix}: o PIX tem desconto sobre o preço do veículo.
 */
public class FormaPagamentoPix implements FormaPagamento<DadosPix, PagamentoPix> {

    private final DetalhePagamentoRepository<PagamentoPix> detalheRepository;
    private final PoliticaDesconto politicaDesconto;

    /**
     * @param detalheRepository onde o detalhe do pagamento com PIX é gravado
     * @param politicaDesconto regra aplicada ao preço do veículo para chegar ao valor pago
     */
    public FormaPagamentoPix(DetalhePagamentoRepository<PagamentoPix> detalheRepository,
            PoliticaDesconto politicaDesconto) {
        this.detalheRepository = detalheRepository;
        this.politicaDesconto = politicaDesconto;
    }

    /**
     * Cria o pagamento com PIX usando o preço do veículo e a data de hoje.
     *
     * @param venda venda que está sendo paga
     * @param dados dados digitados para o pagamento com PIX
     * @return o pagamento com o valor final já calculado
     * @throws IllegalArgumentException se algum dado do pagamento com PIX for inválido
     */
    @Override
    public PagamentoPix criarPagamento(Venda venda, DadosPix dados) {
        return new PagamentoPix(venda, venda.getVeiculo().getPreco(), LocalDate.now(),
                dados.chavePix(), dados.tipoChave(), politicaDesconto);
    }

    /**
     * Grava os dados do pagamento com PIX na tabela pagamentopix, ligados ao pagamento.
     *
     * @param pagamento pagamento já gravado na tabela pagamento
     */
    @Override
    public void salvarDetalhe(PagamentoPix pagamento) {
        detalheRepository.salvar(pagamento);
    }
}
