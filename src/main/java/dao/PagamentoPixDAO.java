package dao;

import conexao.ConexaoBanco;
import modelo.PagamentoPix;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PagamentoPixDAO {
    public void salvar(PagamentoPix pagamentoPix) {

        String sql = """
                INSERT INTO pagamentopix
                (chave_pix, tipo_chave_pix, pagamento_pix)
                VALUES (?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, pagamentoPix.getChavePix());
            comando.setString(2, pagamentoPix.getTipoChave());
            comando.setLong(3, pagamentoPix.getId_pagamento());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    pagamentoPix.setId_pagamentoPix(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento pix no banco.", e);
        }
    }
    public void atualizar(PagamentoPix pagamentoPix, String atributo, String novoValor) {
        String sql = """
                UPDATE pagamentopix
                SET ? = ?
                WHERE id_pagamentopix = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, atributo);
            comando.setString(2, novoValor);
            comando.setLong(3, pagamentoPix.getId_pagamentoPix());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar pagamento pix no banco.", e);
        }
    }
}