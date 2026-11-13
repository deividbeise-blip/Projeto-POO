package infra.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.repositorio.ConcessionariaRepository;
import infra.conexao.ConnectionFactory;
import infra.excecao.PersistenciaException;

/** Acesso à tabela concessionaria. */
public class ConcessionariaDAO implements ConcessionariaRepository {

    static final String MENSAGEM_DUPLICADA = "Já existe uma concessionária com esse nome ou endereço.";
    static final String MENSAGEM_EM_USO =
            "Esta concessionária possui veículos ou vendedores e não pode ser excluída.";

    private static final String COLUNAS = "id_concessionaria, name_concessionaria, address_concessionaria";

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public ConcessionariaDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    @Override
    public void salvar(Concessionaria concessionaria) {
        long id;
        try {
            id = executor.inserir(
                    "INSERT INTO concessionaria (name_concessionaria, address_concessionaria) VALUES (?, ?)",
                    comando -> {
                        comando.setString(1, concessionaria.getNome());
                        comando.setString(2, concessionaria.getEndereco());
                    },
                    "Erro ao salvar concessionária no banco.");
        } catch (PersistenciaException e) {
            throw traduzirDuplicada(e);
        }
        concessionaria.setIdConcessionaria(id);
    }

    @Override
    public Concessionaria buscarPorId(Long id) {
        return executor.consultarUm(
                "SELECT " + COLUNAS + " FROM concessionaria WHERE id_concessionaria = ?",
                comando -> comando.setLong(1, id),
                ConcessionariaDAO::mapear,
                "Erro ao buscar concessionária no banco.");
    }

    /** Usado pela tela de concessionárias e pelos combos das telas de veículos e vendedores. */
    @Override
    public List<Concessionaria> listar() {
        return executor.consultar(
                "SELECT " + COLUNAS + " FROM concessionaria ORDER BY name_concessionaria",
                ConcessionariaDAO::mapear,
                "Erro ao listar concessionárias.");
    }

    @Override
    public void atualizar(Concessionaria concessionaria) {
        try {
            executor.atualizar(
                    "UPDATE concessionaria SET name_concessionaria = ?, address_concessionaria = ? "
                            + "WHERE id_concessionaria = ?",
                    comando -> {
                        comando.setString(1, concessionaria.getNome());
                        comando.setString(2, concessionaria.getEndereco());
                        comando.setLong(3, concessionaria.getIdConcessionaria());
                    },
                    "Erro ao atualizar concessionária no banco.");
        } catch (PersistenciaException e) {
            throw traduzirDuplicada(e);
        }
    }

    @Override
    public void excluir(Long id) {
        try {
            executor.atualizar(
                    "DELETE FROM concessionaria WHERE id_concessionaria = ?",
                    comando -> comando.setLong(1, id),
                    "Erro ao excluir concessionária no banco.");
        } catch (PersistenciaException e) {
            if (ErrosSql.violaChaveEstrangeira(e)) {
                throw new IllegalStateException(MENSAGEM_EM_USO, e);
            }
            throw e;
        }
    }

    private static RuntimeException traduzirDuplicada(PersistenciaException e) {
        return ErrosSql.violaValorUnico(e) ? new IllegalStateException(MENSAGEM_DUPLICADA, e) : e;
    }

    private static Concessionaria mapear(ResultSet resultado) throws SQLException {
        return new Concessionaria(
                resultado.getLong("id_concessionaria"),
                resultado.getString("name_concessionaria"),
                resultado.getString("address_concessionaria"));
    }
}
