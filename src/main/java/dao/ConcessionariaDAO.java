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

            comando.setString(1, concessionaria.getName());
            comando.setString(2, concessionaria.getAddress());

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
}