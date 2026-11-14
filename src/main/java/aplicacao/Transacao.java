package aplicacao;

/**
 * Executa um bloco de operações como uma unidade: ou tudo é gravado, ou nada é.
 * A implementação (JDBC, por exemplo) fica na camada de infraestrutura.
 */
public interface Transacao {

    /**
     * Roda a ação dentro de uma transação. Se ela terminar sem erro, tudo é confirmado;
     * se lançar exceção, tudo é desfeito e a exceção chega a quem chamou.
     * Chamadas aninhadas participam da transação já aberta.
     *
     * @param acao operações de gravação que precisam acontecer juntas
     */
    void executar(Runnable acao);
}
