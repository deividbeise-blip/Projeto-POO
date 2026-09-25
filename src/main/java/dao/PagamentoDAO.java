package dao;

import conexao.ConexaoBanco;
import modelo.Pagamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class PagamentoDAO {
    public void salvar(Pagamento pagamento) {

        String sql = """
                INSERT INTO pagamento
                (forma_pagamento, valor_pago, data_pagamento, cliente_pagamento, veiculo_pagamento)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, pagamento.getFormaPagamento());
            comando.setDouble(2, pagamento.getValorPago());
            comando.setDate(3, java.sql.Date.valueOf(pagamento.getDataPagamento()));
            comando.setLong(4, pagamento.getVenda().getId_venda());
            comando.setLong(5, pagamento.getVenda().getVeiculo().getId_veiculo());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    pagamento.setId_pagamento(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar pagamento no banco.", e);
        }
    }
}