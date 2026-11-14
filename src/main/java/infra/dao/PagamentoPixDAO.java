package infra.dao;

import dominio.modelo.PagamentoPix;
import dominio.repositorio.DetalhePagamentoRepository;
import infra.conexao.ConnectionFactory;

public class PagamentoPixDAO implements DetalhePagamentoRepository<PagamentoPix> {

    private final ExecutorSql executor;

    public PagamentoPixDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

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
