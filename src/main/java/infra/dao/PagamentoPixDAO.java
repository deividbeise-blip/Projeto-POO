package infra.dao;

import dominio.modelo.PagamentoPix;
import dominio.repositorio.DetalhePagamentoRepository;
import infra.conexao.ConnectionFactory;

/** Grava o detalhe do pagamento com PIX na tabela pagamentopix. */
public class PagamentoPixDAO implements DetalhePagamentoRepository<PagamentoPix> {

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public PagamentoPixDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT da chave e do tipo de chave e coloca no objeto o ID gerado.
     *
     * @param pagamentoPix pagamento cuja parte comum já foi gravada (precisa ter ID)
     * @throws infra.excecao.PersistenciaException se o banco recusar a gravação
     */
    @Override
    public void salvar(PagamentoPix pagamentoPix) {
        long id = executor.inserir(
                "INSERT INTO pagamentopix (chave_pix, tipo_chave_pix, pagamento_pix) VALUES (?, ?, ?)",
                comando -> {
                    comando.setString(1, pagamentoPix.getChavePix());
                    comando.setString(2, pagamentoPix.getTipoChave());
                    comando.setLong(3, pagamentoPix.getIdPagamento());
                },
                "Erro ao salvar pagamento pix no banco.");
        pagamentoPix.setIdPagamentoPix(id);
    }
}
