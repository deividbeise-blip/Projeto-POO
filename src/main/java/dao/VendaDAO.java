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
}