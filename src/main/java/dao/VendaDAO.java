package dao;

import conexao.ConexaoBanco;
import modelo.Venda;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

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
            // O pagamento ainda não existe neste momento; é preenchido em atualizarPagamento().
            comando.setNull(5, Types.BIGINT);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    venda.setId_venda(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar venda no banco.", e);
        }
    }

    /** Liga a venda ao pagamento depois que o pagamento já foi gravado. */
    public void atualizarPagamento(Venda venda) {
        String sql = "UPDATE venda SET pagamento_venda = ? WHERE id_venda = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, venda.getPagamento().getId_pagamento());
            comando.setLong(2, venda.getId_venda());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar pagamento da venda no banco.", e);
        }
    }
}
