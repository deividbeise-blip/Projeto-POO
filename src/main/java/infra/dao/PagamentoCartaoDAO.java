package infra.dao;

import java.sql.Date;
import java.time.YearMonth;

import dominio.modelo.PagamentoCartao;
import dominio.repositorio.DetalhePagamentoRepository;
import infra.conexao.ConnectionFactory;

/** Grava o detalhe do pagamento com cartão na tabela pagamentocartao. */
public class PagamentoCartaoDAO implements DetalhePagamentoRepository<PagamentoCartao> {

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public PagamentoCartaoDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT do detalhe e coloca no objeto o ID gerado. A validade "MM/aa" vira
     * o último dia daquele mês, porque a coluna validade_cartao é DATE.
     *
     * @param pagamentoCartao pagamento cuja parte comum já foi gravada (precisa ter ID)
     * @throws infra.excecao.PersistenciaException se o banco recusar a gravação
     */
    @Override
    public void salvar(PagamentoCartao pagamentoCartao) {
        // A validade chega como "MM/aa" (ex: "12/30"); a coluna validade_cartao no
        // banco é do tipo DATE, então convertemos para o último dia daquele mês/ano.
        Date validade = Date.valueOf(YearMonth.parse(
                pagamentoCartao.getValidade(), PagamentoCartao.FORMATO_VALIDADE).atEndOfMonth());

        long id = executor.inserir(
                """
                INSERT INTO pagamentocartao
                (numero_cartao, nomeTitular, validade_cartao, codigo_seguranca, numero_parcelas, valor_parcela, pagamento_cartao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                comando -> {
                    comando.setString(1, pagamentoCartao.getNumeroCartao());
                    comando.setString(2, pagamentoCartao.getNomeTitular());
                    comando.setDate(3, validade);
                    comando.setString(4, pagamentoCartao.getCvv());
                    comando.setInt(5, pagamentoCartao.getNumeroParcelas());
                    comando.setDouble(6, pagamentoCartao.getValorParcela());
                    comando.setLong(7, pagamentoCartao.getIdPagamento());
                },
                "Erro ao salvar pagamento no cartão no banco.");
        pagamentoCartao.setIdPagamentoCartao(id);
    }
}
