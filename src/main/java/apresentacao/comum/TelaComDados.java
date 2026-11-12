package apresentacao.comum;

/**
 * Tela que precisa recarregar dados do banco sempre que fica visível. A {@code TelaPrincipal}
 * chama {@link #carregarDados()} fora da thread da tela (pela {@link TarefaEmSegundoPlano}) e
 * depois entrega o resultado a {@link #exibirDados(Object)} na thread da tela.
 *
 * @param <T> tipo que agrupa o que a tela carrega (normalmente um {@code record} da própria tela)
 */
public interface TelaComDados<T> {

    /**
     * Busca os dados nos services. Roda FORA da thread da tela: não mexa em componentes aqui.
     *
     * @return os dados carregados
     * @throws Exception se a busca falhar; o erro é mostrado pelo {@link Feedback}
     */
    T carregarDados() throws Exception;

    /**
     * Mostra os dados nos componentes. Roda na thread da tela.
     *
     * @param dados o que {@link #carregarDados()} devolveu
     */
    void exibirDados(T dados);
}
