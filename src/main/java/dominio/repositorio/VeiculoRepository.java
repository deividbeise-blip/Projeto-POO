package dominio.repositorio;

import java.util.List;

import dominio.modelo.Veiculo;

public interface VeiculoRepository {

    /** Grava o veículo e preenche o ID gerado. */
    void salvar(Veiculo veiculo);

    /** Grava só o status atual do veículo. */
    void atualizarStatus(Veiculo veiculo);

    /** Devolve o veículo com esse ID, ou null se não existir. */
    Veiculo buscarPorId(Long id);

    /** Devolve os veículos com status DISPONIVEL. */
    List<Veiculo> listarDisponiveis();

    /** Devolve todos os veículos, de qualquer status. */
    List<Veiculo> listarTodos();

    /**
     * Exclui o veículo.
     *
     * @throws IllegalStateException se o banco recusar por haver venda ligada ao veículo
     */
    void excluir(Long id);

    /** Grava todos os dados do veículo (UPDATE pelo ID). */
    void atualizar(Veiculo veiculo);
}
