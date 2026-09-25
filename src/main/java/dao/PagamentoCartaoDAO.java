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
                (numero_cartao, validade_cartao, codigo_seguranca, numero_parcelas, valor_parcela, pagamento_cartao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, pagamentoCartao.getNumeroCartao());
            comando.setString(2, pagamentoCartao.getValidade());
            comando.setString(3, pagamentoCartao.getCvv());
            comando.setInt(4, pagamentoCartao.getnumeroParcelas());
            comando.setDouble(5, pagamentoCartao.getValorParcela());
            comando.setDouble(6, pagamentoCartao.getPagamentoCartao());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    cliente.setId(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cliente no banco.", e);
        }
    }
}