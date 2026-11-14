package aplicacao.pagamento;

import aplicacao.dto.DadosPix;
import dominio.modelo.PagamentoPix;
import dominio.modelo.Venda;
import dominio.politica.PoliticaDesconto;
import dominio.repositorio.DetalhePagamentoRepository;

import java.time.LocalDate;

public class FormaPagamentoPix implements FormaPagamento<DadosPix, PagamentoPix> {

    private final DetalhePagamentoRepository<PagamentoPix> detalheRepository;
    private final PoliticaDesconto politicaDesconto;

    public FormaPagamentoPix(DetalhePagamentoRepository<PagamentoPix> detalheRepository,
            PoliticaDesconto politicaDesconto) {
        this.detalheRepository = detalheRepository;
        this.politicaDesconto = politicaDesconto;
    }

    @Override
    public PagamentoPix criarPagamento(Venda venda, DadosPix dados) {
        return new PagamentoPix(venda, venda.getVeiculo().getPreco(), LocalDate.now(),
                dados.chavePix(), dados.tipoChave(), politicaDesconto);
    }

    @Override
    public void salvarDetalhe(PagamentoPix pagamento) {
        detalheRepository.salvar(pagamento);
    }
}
