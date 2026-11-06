package aplicacao;

/**
 * Executa um bloco de operações como uma unidade: ou tudo é gravado, ou nada é.
 * A implementação (JDBC, por exemplo) fica na camada de infraestrutura.
 */
public interface Transacao {

    void executar(Runnable acao);
}
