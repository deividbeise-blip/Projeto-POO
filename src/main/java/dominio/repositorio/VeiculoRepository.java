package dominio.repositorio;

import java.util.List;

import dominio.modelo.Veiculo;

public interface VeiculoRepository {

    /** Grava o veículo e preenche o ID gerado. */
    void salvar(Veiculo veiculo);

    void atualizarStatus(Veiculo veiculo);

    /** Devolve o veículo com esse ID, ou null se não existir. */
    Veiculo buscarPorId(Long id);

    List<Veiculo> listarDisponiveis();

    List<Veiculo> listarTodos();

    void excluir(Long id);

    void atualizar(Veiculo veiculo);
}
