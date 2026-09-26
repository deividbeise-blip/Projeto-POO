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
            comando.setInt(5, pagamentoCartao.getnumeroParcelas());
            comando.setDouble(6, pagamentoCartao.getValorParcela());
            comando.setLong(7, pagamentoCartao.getVenda().getId_venda());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    pagamentoCartao.setId_PagamentoCartao(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no banco.", e);
        }
    }
    public void atualizar(PagamentoCartao pagamentoCartao, String atributo, String novoValor) {
        String sql = """
                UPDATE pagamentocartao
                SET ? = ?
                WHERE id_pagamentocartao = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, atributo);
            comando.setString(2, novoValor);
            comando.setLong(3, pagamentoCartao.getId_PagamentoCartao());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar pagamento no banco.", e);
        }
    }
}