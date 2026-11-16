package infra.conexao;

import infra.excecao.PersistenciaException;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 * Cria ou atualiza as tabelas do banco ao iniciar o sistema, usando o Flyway e as migrações em
 * {@code src/main/resources/db/migration} (V1, V2, ...). Usa as mesmas credenciais da
 * {@link ConexaoBanco} (DB_URL, DB_USER e DB_PASSWORD).
 * <ul>
 *   <li>Banco vazio: aplica todas as migrações, a partir da V1.</li>
 *   <li>Banco que já tinha as tabelas antes do Flyway: marca a V1 como já aplicada (baseline)
 *       e roda só da V2 em diante.</li>
 *   <li>Banco em dia: não faz nada.</li>
 * </ul>
 * O histórico do que já foi aplicado fica na tabela {@code flyway_schema_history}.
 */
public class MigradorBanco {

    private static final String LOCAL_MIGRACOES = "classpath:db/migration";
    private static final String VERSAO_BASELINE = "1";

    private final ConexaoBanco conexao;

    /**
     * @param conexao credenciais do banco que será criado ou atualizado
     */
    public MigradorBanco(ConexaoBanco conexao) {
        this.conexao = conexao;
    }

    /**
     * Aplica as migrações pendentes. Pode ser chamado a cada inicialização: o que já foi aplicado
     * não roda de novo.
     *
     * @return quantas migrações foram aplicadas agora (0 se o banco já estava em dia)
     * @throws PersistenciaException se não conseguir conectar ou alguma migração falhar
     */
    public int migrar() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(conexao.url(), conexao.usuario(), conexao.senha())
                    .locations(LOCAL_MIGRACOES)
                    .baselineOnMigrate(true)
                    .baselineVersion(VERSAO_BASELINE)
                    .load();
            return flyway.migrate().migrationsExecuted;
        } catch (FlywayException ex) {
            throw new PersistenciaException(
                    "Falha ao criar ou atualizar as tabelas do banco (migrações Flyway). "
                            + "Confira DB_URL, DB_USER e DB_PASSWORD e se o banco está no ar. Detalhe: "
                            + ex.getMessage(), ex);
        }
    }
}
