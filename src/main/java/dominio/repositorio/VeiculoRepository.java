package dominio.repositorio;

import java.util.List;

import dominio.modelo.Veiculo;

public interface VeiculoRepository {

    /** Grava o veículo e preenche o ID gerado. */
    void salvar(Veiculo veiculo);

    void atualizarStatus(Veiculo veiculo);

    List<Veiculo> listarDisponiveis();

    List<Veiculo> listarTodos();
}
