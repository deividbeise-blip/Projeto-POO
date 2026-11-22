package dominio.repositorio;

import java.util.List;

import dominio.modelo.Veiculo;

/** Onde os veículos são gravados e lidos. */
public interface VeiculoRepository {

    /**
     * Grava o veículo e preenche o ID gerado.
     *
     * @param veiculo veículo novo, ainda sem ID
     */
    void salvar(Veiculo veiculo);

    /**
     * Grava só o status atual do veículo (usado ao vender e nas trocas de status).
     *
     * @param veiculo veículo com ID e o status novo
     */
    void atualizarStatus(Veiculo veiculo);

    /**
     * Devolve o veículo com esse ID, ou null se não existir.
     *
     * @param id ID do veículo
     * @return o veículo encontrado, ou null
     */
    Veiculo buscarPorId(Long id);

    /**
     * Devolve os veículos com status DISPONIVEL (os que podem ser vendidos).
     *
     * @return lista de veículos disponíveis; vazia se não houver nenhum
     */
    List<Veiculo> listarDisponiveis();

    /**
     * Devolve todos os veículos, de qualquer status.
     *
     * @return lista de veículos; vazia se não houver nenhum
     */
    List<Veiculo> listarTodos();

    /**
     * Exclui o veículo.
     *
     * @param id ID do veículo a excluir
     * @throws IllegalStateException se o banco recusar por haver venda ligada ao veículo
     */
    void excluir(Long id);

    /**
     * Grava todos os dados do veículo (UPDATE pelo ID).
     *
     * @param veiculo veículo com o ID já definido e os dados novos
     */
    void atualizar(Veiculo veiculo);
}
