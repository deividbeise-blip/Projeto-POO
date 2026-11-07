package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.repositorio.ConcessionariaRepository;
import infra.conexao.ConnectionFactory;

public class ConcessionariaDAO implements ConcessionariaRepository {

    private static final String COLUNAS = "id_concessionaria, name_concessionaria, address_concessionaria";

    private final ExecutorSql executor;

    public ConcessionariaDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    public void salvar(Concessionaria concessionaria) {
        long id = executor.inserir(
                "INSERT INTO concessionaria (name_concessionaria, address_concessionaria) VALUES (?, ?)",
                comando -> {
                    comando.setString(1, concessionaria.getName_concessionaria());
                    comando.setString(2, concessionaria.getAddress_concessionaria());
                },
                "Erro ao salvar concessionaria no banco.");
        concessionaria.setId_concessionaria(id);
    }

    @Override
    public Concessionaria buscarPorId(Long id) {
        return executor.consultarUm(
                "SELECT " + COLUNAS + " FROM concessionaria WHERE id_concessionaria = ?",
                comando -> comando.setLong(1, id),
                ConcessionariaDAO::mapear,
                "Erro ao buscar concessionaria no banco.");
    }

    /** Necessário para preencher o combo de concessionárias na tela de cadastro de veículo. */
    @Override
    public List<Concessionaria> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM concessionaria",
                ConcessionariaDAO::mapear,
                "Erro ao listar concessionarias.");
    }

    private static Concessionaria mapear(ResultSet resultado) throws SQLException {
        return new Concessionaria(
                resultado.getLong("id_concessionaria"),
                resultado.getString("name_concessionaria"),
                resultado.getString("address_concessionaria"));
    }
}
