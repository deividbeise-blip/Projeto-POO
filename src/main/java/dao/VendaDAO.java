package dao;

import conexao.ConexaoBanco;
import modelo.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class VendaDAO {
    public void salvar(Venda venda) {

        String sql = """
                INSERT INTO venda
                (data_venda, cliente_venda, veiculo_venda, vendedor_venda, pagamento_venda)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setDate(1, java.sql.Date.valueOf(venda.getDataVenda()));
            comando.setLong(2, venda.getCliente().getId_cliente());
            comando.setLong(3, venda.getVeiculo().getId_veiculo());
            comando.setLong(4, venda.getVendedor().getId_vendedor());
            comando.setDouble(5, venda.getPagamento().getId_pagamento());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    venda.setId_venda(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar venda no banco.", e);
        }
    }
    public void atualizar(Venda venda, String atributo, String novoValor) {
        String sql = """
                UPDATE venda
                SET ? = ?
                WHERE id_venda = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, atributo);
            comando.setString(2, novoValor);
            comando.setLong(3, venda.getId_venda());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar venda no banco.", e);
        }
    }
}