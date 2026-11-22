package infra.conexao;

import aplicacao.Transacao;
import infra.excecao.PersistenciaException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Transação JDBC. Também é a ConnectionFactory entregue aos DAOs: enquanto uma transação
 * está aberta nesta thread, todos os DAOs recebem a MESMA conexão (com close() ignorado,
 * para o try-with-resources dos DAOs não fechar a conexão no meio da transação).
 * Fora de uma transação, cada chamada pega uma conexão nova, como antes.
 *
 * executar(): abre a conexão, desliga o auto-commit, roda a ação,
 * commit se deu certo, rollback se deu erro (e relança o erro).
 */
public class TransacaoJdbc implements Transacao, ConnectionFactory {

    private final ConnectionFactory origem;
    private final ThreadLocal<Connection> atual = new ThreadLocal<>();

    /**
     * @param origem fábrica que abre as conexões reais
     * @throws NullPointerException se a fábrica for nula
     */
    public TransacaoJdbc(ConnectionFactory origem) {
        this.origem = Objects.requireNonNull(origem, "ConnectionFactory é obrigatória.");
    }

    /**
     * Dentro de uma transação desta thread, devolve a conexão dela (com close() ignorado);
     * fora, devolve uma conexão nova da origem.
     *
     * @return conexão para o DAO usar e fechar normalmente
     * @throws SQLException se não for possível abrir a conexão
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection emTransacao = atual.get();
        if (emTransacao == null) {
            return origem.getConnection();
        }
        return semFechar(emTransacao);
    }

    /**
     * Roda a ação numa transação: commit se terminar bem, rollback e relançamento se falhar.
     * Se já houver transação aberta nesta thread, a ação apenas participa dela.
     *
     * @param acao operações de gravação a executar juntas
     * @throws PersistenciaException se o banco falhar ao abrir, confirmar ou controlar a transação
     */
    @Override
    public void executar(Runnable acao) {
        if (atual.get() != null) {
            acao.run(); // já existe transação nesta thread: participa dela
            return;
        }

        try (Connection conexao = origem.getConnection()) {
            conexao.setAutoCommit(false);
            atual.set(conexao);
            try {
                acao.run();
                conexao.commit();
            } catch (SQLException e) {
                reverter(conexao, e);
                throw new PersistenciaException("Erro ao confirmar a transação.", e);
            } catch (RuntimeException | Error e) {
                reverter(conexao, e);
                throw e;
            } finally {
                atual.remove();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao controlar a transação.", e);
        }
    }

    private void reverter(Connection conexao, Throwable causa) {
        try {
            conexao.rollback();
        } catch (SQLException erroNoRollback) {
            causa.addSuppressed(erroNoRollback);
        }
    }

    private static Connection semFechar(Connection real) {
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{Connection.class},
                (proxy, metodo, args) -> {
                    if (metodo.getName().equals("close")) {
                        return null;
                    }
                    try {
                        return metodo.invoke(real, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }
}
