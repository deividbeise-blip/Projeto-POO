package dao;

import conexao.ConexaoBanco;
import modelo.Concessionaria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConcessionariaDAO {
    public void salvar(Concessionaria concessionaria) {

        String sql = """
                INSERT INTO concessionaria
                (name_concessionaria, address_concessionaria)
                VALUES (?, ?)
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, concessionaria.getName_concessionaria());
            comando.setString(2, concessionaria.getAddress_concessionaria());

            comando.executeUpdate();

            // Pega o ID gerado pelo AUTO_INCREMENT
            try (ResultSet resultado = comando.getGeneratedKeys()) {

                if (resultado.next()) {
                    concessionaria.setId_concessionaria(resultado.getLong(1));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar concessionaria no banco.", e);
        }
    }
    public void atualizar(Concessionaria concessionaria, String atributo, String novoValor) {
        String sql = """
                UPDATE concessionaria
                SET ? = ?
                WHERE id_concessionaria = ?
                """;

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, atributo);
            comando.setString(2, novoValor);
            comando.setLong(3, concessionaria.getId_concessionaria());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar concessionaria no banco.", e);
        }
    }
}