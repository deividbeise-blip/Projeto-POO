package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import conexao.ConexaoBanco;
import modelo.PagamentoCartao;

public class PagamentoCartaoDAO {

    // A validade chega como "MM/aa" (ex: "12/30"); a coluna validade_cartao no
    // banco é do tipo DATE, então convertemos para o último dia daquele mês/ano.
    private static final DateTimeFormatter FORMATO_VALIDADE = DateTimeFormatter.ofPattern("MM/yy");

    public void salvar(PagamentoCartao pagamentoCartao) {

        String sql = """
                INSERT INTO pagamentocartao
                (numero_cartao, nomeTitular, validade_cartao, codigo_seguranca, numero_parcelas, valor_parcela, pagamento_cartao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            YearMonth anoMes = YearMonth.parse(pagamentoCartao.getValidade(), FORMATO_VALIDADE);
            LocalDate dataValidade = anoMes.atEndOfMonth();

            comando.setString(1, pagamentoCartao.getNumeroCartao());
            comando.setString(2, pagamentoCartao.getNomeTitular());
            comando.setDate(3, Date.valueOf(dataValidade));
            comando.setString(4, pagamentoCartao.getCvv());
            comando.setInt(5, pagamentoCartao.getNumeroParcelas());
            comando.setDouble(6, pagamentoCartao.getValorParcela());
            comando.setLong(7, pagamentoCartao.getId_pagamento());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    pagamentoCartao.setId_PagamentoCartao(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no cartão no banco.", e);
        }
    }
}