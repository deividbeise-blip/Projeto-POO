package infra.dao;

import java.sql.Date;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import dominio.modelo.PagamentoCartao;
import dominio.repositorio.DetalhePagamentoRepository;
import infra.conexao.ConnectionFactory;

public class PagamentoCartaoDAO implements DetalhePagamentoRepository<PagamentoCartao> {

    // A validade chega como "MM/aa" (ex: "12/30"); a coluna validade_cartao no
    // banco é do tipo DATE, então convertemos para o último dia daquele mês/ano.
    private static final DateTimeFormatter FORMATO_VALIDADE = DateTimeFormatter.ofPattern("MM/yy");

    private final ExecutorSql executor;

    public PagamentoCartaoDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    @Override
    public void salvar(PagamentoCartao pagamentoCartao) {
        Date validade = Date.valueOf(
                YearMonth.parse(pagamentoCartao.getValidade(), FORMATO_VALIDADE).atEndOfMonth());

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
                    comando.setLong(7, pagamentoCartao.getId_pagamento());
                },
                "Erro ao salvar pagamento no cartão no banco.");
        pagamentoCartao.setId_PagamentoCartao(id);
    }
}
