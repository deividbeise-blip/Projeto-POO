package aplicacao.pagamento;

import aplicacao.dto.DadosPix;
import dominio.modelo.PagamentoPix;
import dominio.modelo.Venda;
import dominio.repositorio.DetalhePagamentoRepository;

import java.time.LocalDate;

public class FormaPagamentoPix implements FormaPagamento<DadosPix, PagamentoPix> {

    private final DetalhePagamentoRepository<PagamentoPix> detalheRepository;

    public FormaPagamentoPix(DetalhePagamentoRepository<PagamentoPix> detalheRepository) {
        this.detalheRepository = detalheRepository;
    }

    @Override
    public PagamentoPix criarPagamento(Venda venda, DadosPix dados) {
        return new PagamentoPix(venda, venda.getVeiculo().getPreco(), LocalDate.now(),
                dados.chavePix(), dados.tipoChave());
    }

    @Override
    public void salvarDetalhe(PagamentoPix pagamento) {
        detalheRepository.salvar(pagamento);
    }
}
