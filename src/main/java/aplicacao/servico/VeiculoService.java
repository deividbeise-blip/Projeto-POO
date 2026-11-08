package aplicacao.servico;

import java.util.List;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;
import dominio.repositorio.ConcessionariaRepository;
import dominio.repositorio.VeiculoRepository;

public class VeiculoService {
    private final VeiculoRepository veiculoRepository;
    private final ConcessionariaRepository concessionariaRepository;

    public VeiculoService(VeiculoRepository veiculoRepository,
                          ConcessionariaRepository concessionariaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.concessionariaRepository = concessionariaRepository;
    }

    /**
     * Cadastra um veículo novo, sempre como DISPONIVEL.
     * A validação de dados (marca, modelo, placa, preço etc.) acontece no
     * construtor de Veiculo; aqui só orquestramos a gravação.
     */
    public Veiculo cadastrar(String marca, String modelo, Integer ano, String placa,
                             Double preco, boolean moto, Concessionaria concessionaria) {
        Veiculo veiculo = new Veiculo(marca, modelo, ano, placa, preco,
                StatusVeiculo.DISPONIVEL, concessionaria, moto, false);
        veiculoRepository.salvar(veiculo);
        return veiculo;
    }

    /** Altera o status respeitando as regras do domínio e persiste o resultado. */
    public void alterarStatus(Veiculo veiculo, StatusVeiculo novoStatus) throws VeiculoIndisponivelException {
        switch (novoStatus) {
            case DISPONIVEL -> veiculo.tornarDisponivel();
            case EM_MANUTENCAO -> veiculo.colocarEmManutencao();
            case VENDIDO -> throw new VeiculoIndisponivelException(
                    "Um veículo só pode ser marcado como vendido pela tela de vendas.");
        }
        veiculoRepository.atualizarStatus(veiculo);
    }

    public List<Veiculo> listarTodos() {
        return veiculoRepository.listarTodos();
    }

    public List<Veiculo> listarDisponiveis() {
        return veiculoRepository.listarDisponiveis();
    }

    public List<Concessionaria> listarConcessionarias() {
        return concessionariaRepository.listar();
    }
}
