package aplicacao.pagamento;

import aplicacao.dto.DadosCartao;
import dominio.modelo.PagamentoCartao;
import dominio.modelo.Venda;
import dominio.repositorio.DetalhePagamentoRepository;

import java.time.LocalDate;

public class FormaPagamentoCartao implements FormaPagamento<DadosCartao, PagamentoCartao> {

    private final DetalhePagamentoRepository<PagamentoCartao> detalheRepository;

    public FormaPagamentoCartao(DetalhePagamentoRepository<PagamentoCartao> detalheRepository) {
        this.detalheRepository = detalheRepository;
    }

    @Override
    public PagamentoCartao criarPagamento(Venda venda, DadosCartao dados) {
        return new PagamentoCartao(venda, venda.getVeiculo().getPreco(), LocalDate.now(),
                dados.numeroCartao(), dados.titular(), dados.validade(),
                dados.cvv(), dados.parcelas());
    }

    @Override
    public void salvarDetalhe(PagamentoCartao pagamento) {
        detalheRepository.salvar(pagamento);
    }
}
