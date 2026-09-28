package servico;

import dao.VeiculoDAO;
import excecao.VeiculoIndisponivelException;
import modelo.StatusVeiculo;
import modelo.Veiculo;

public class VeiculoService {
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

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
