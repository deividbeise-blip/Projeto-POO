package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import conexao.ConexaoBanco;
import modelo.Concessionaria;

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
        String sql = "UPDATE concessionaria SET " + atributo + " = ? WHERE id_concessionaria = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoValor);
            comando.setLong(2, concessionaria.getId_concessionaria());

            comando.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar concessionaria no banco.", e);
        }
    }

    public Concessionaria buscarPorId(Long id) {
        String sql = "SELECT * FROM concessionaria WHERE id_concessionaria = ?";

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setLong(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return new Concessionaria(
                            resultado.getLong("id_concessionaria"),
                            resultado.getString("name_concessionaria"),
                            resultado.getString("address_concessionaria")
                    );
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar concessionaria no banco.", e);
        }

        return null;
    }

    /** Necessário para preencher o combo de concessionárias na tela de cadastro de veículo. */
    public List<Concessionaria> listar() {
        String sql = "SELECT * FROM concessionaria";
        List<Concessionaria> lista = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                lista.add(new Concessionaria(
                        resultado.getLong("id_concessionaria"),
                        resultado.getString("name_concessionaria"),
                        resultado.getString("address_concessionaria")
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar concessionarias.", e);
        }

        return lista;
    }
}
