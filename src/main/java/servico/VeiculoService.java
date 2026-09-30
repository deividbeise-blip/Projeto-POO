package servico;

import dao.VeiculoDAO;
import excecao.VeiculoIndisponivelException;
import modelo.Concessionaria;
import modelo.StatusVeiculo;
import modelo.Veiculo;

public class VeiculoService {
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    /**
     * Cadastra um veículo novo, sempre como DISPONIVEL.
     * A validação de dados (marca, modelo, placa, preço etc.) acontece no
     * construtor de Veiculo; aqui só orquestramos a gravação.
     */
    public Veiculo cadastrar(String marca, String modelo, Integer ano, String placa,
                             Double preco, boolean moto, Concessionaria concessionaria) {
        Veiculo veiculo = new Veiculo(marca, modelo, ano, placa, preco,
                StatusVeiculo.DISPONIVEL, concessionaria, moto, false);
        veiculoDAO.salvar(veiculo);
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
        veiculoDAO.atualizarStatus(veiculo);
    }
}
