package dao;

import conexao.ConexaoBanco;
import modelo.PagamentoCartao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PagamentoCartaoDAO {
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

            comando.setString(1, pagamentoCartao.getNumeroCartao());
            comando.setString(2, pagamentoCartao.getNomeTitular());
            comando.setString(3, pagamentoCartao.getValidade());
            comando.setString(4, pagamentoCartao.getCvv());
            comando.setInt(5, pagamentoCartao.getNumeroParcelas());
            comando.setDouble(6, pagamentoCartao.getValorParcela());
            // Antes gravava o ID da venda; a coluna é do pagamento (igual ao PIX).
            comando.setLong(7, pagamentoCartao.getId_pagamento());

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    pagamentoCartao.setId_PagamentoCartao(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no banco.", e);
        }
    }
}
