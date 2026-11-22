package aplicacao.servico;

import java.util.List;

import dominio.excecao.VeiculoIndisponivelException;
import dominio.modelo.Concessionaria;
import dominio.modelo.StatusVeiculo;
import dominio.modelo.Veiculo;
import dominio.repositorio.ConcessionariaRepository;
import dominio.repositorio.VeiculoRepository;

/**
 * Casos de uso de veículos: cadastrar, editar, excluir, trocar status e listar.
 * As regras de dados ficam no construtor de {@link Veiculo}; as regras de "quem pode
 * ser editado/excluído" ficam aqui.
 */
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;
    private final ConcessionariaRepository concessionariaRepository;

    /**
     * @param veiculoRepository        onde os veículos são gravados e lidos
     * @param concessionariaRepository usado para listar as concessionárias do formulário
     */
    public VeiculoService(VeiculoRepository veiculoRepository,
                          ConcessionariaRepository concessionariaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.concessionariaRepository = concessionariaRepository;
    }

    /**
     * Cadastra um veículo novo, sempre como DISPONIVEL.
     * A validação de dados (marca, modelo, placa, preço etc.) acontece no
     * construtor de Veiculo; aqui só orquestramos a gravação.
     *
     * @param marca          marca do veículo
     * @param modelo         modelo do veículo
     * @param ano            ano do veículo
     * @param placa          placa do veículo
     * @param preco          preço de venda
     * @param moto           {@code true} se for moto, {@code false} se for carro
     * @param concessionaria concessionária à qual o veículo pertence
     * @return o veículo gravado, já com ID
     * @throws IllegalArgumentException se algum dado for inválido
     */
    public Veiculo cadastrar(String marca, String modelo, Integer ano, String placa,
                             Double preco, boolean moto, Concessionaria concessionaria) {
        Veiculo veiculo = new Veiculo(marca, modelo, ano, placa, preco,
                StatusVeiculo.DISPONIVEL, concessionaria, moto);
        veiculoRepository.salvar(veiculo);
        return veiculo;
    }

    /**
     * Altera o status respeitando as regras do domínio e persiste o resultado.
     *
     * @param veiculo    veículo carregado do banco
     * @param novoStatus DISPONIVEL ou EM_MANUTENCAO (VENDIDO só pela tela de vendas)
     * @throws VeiculoIndisponivelException se a troca não for permitida (veículo já vendido ou status VENDIDO pedido)
     */
    public void alterarStatus(Veiculo veiculo, StatusVeiculo novoStatus) throws VeiculoIndisponivelException {
        switch (novoStatus) {
            case DISPONIVEL -> veiculo.tornarDisponivel();
            case EM_MANUTENCAO -> veiculo.colocarEmManutencao();
            case VENDIDO -> throw new VeiculoIndisponivelException(
                    "Um veículo só pode ser marcado como vendido pela tela de vendas.");
        }
        veiculoRepository.atualizarStatus(veiculo);
    }

    /** @return todos os veículos, de qualquer status */
    public List<Veiculo> listarTodos() {
        return veiculoRepository.listarTodos();
    }

    /** @return só os veículos com status DISPONIVEL (usado na tela de vendas) */
    public List<Veiculo> listarDisponiveis() {
        return veiculoRepository.listarDisponiveis();
    }

    /** @return as concessionárias cadastradas, para o combo do formulário */
    public List<Concessionaria> listarConcessionarias() {
        return concessionariaRepository.listar();
    }

    /**
     * Edita os dados de um veículo existente (UPDATE), mantendo o status que ele já tem.
     *
     * @param id             ID do veículo a editar
     * @param marca          nova marca
     * @param modelo         novo modelo
     * @param ano            novo ano
     * @param placa          nova placa
     * @param preco          novo preço
     * @param moto           {@code true} se for moto, {@code false} se for carro
     * @param concessionaria concessionária à qual o veículo pertence
     * @return o veículo atualizado
     * @throws IllegalArgumentException se nenhum veículo foi informado, se ele não existe
     *                                  ou se algum dado for inválido
     * @throws IllegalStateException    se o veículo já foi vendido
     */
    public Veiculo atualizar(Long id, String marca, String modelo, Integer ano, String placa,
                             Double preco, boolean moto, Concessionaria concessionaria) {
        Veiculo atual = buscarEditavel(id, "Selecione um veículo para editar.",
                "Veículo vendido não pode ser editado.");
        Veiculo veiculo = new Veiculo(id, marca, modelo, ano, placa, preco,
                atual.getStatus(), concessionaria, moto);
        veiculoRepository.atualizar(veiculo);
        return veiculo;
    }

    /**
     * Exclui um veículo que ainda não foi vendido.
     *
     * @param id ID do veículo a excluir
     * @throws IllegalArgumentException se nenhum veículo foi informado ou se ele não existe
     * @throws IllegalStateException    se o veículo já foi vendido ou se o banco recusar a exclusão
     */
    public void excluir(Long id) {
        buscarEditavel(id, "Selecione um veículo para excluir.",
                "Veículo vendido não pode ser excluído.");
        veiculoRepository.excluir(id);
    }

    /** Busca o veículo e garante que ele existe e não foi vendido. */
    private Veiculo buscarEditavel(Long id, String mensagemSemId, String mensagemVendido) {
        if (id == null) {
            throw new IllegalArgumentException(mensagemSemId);
        }
        Veiculo atual = veiculoRepository.buscarPorId(id);
        if (atual == null) {
            throw new IllegalArgumentException("Veículo não encontrado.");
        }
        if (atual.getStatus() == StatusVeiculo.VENDIDO) {
            throw new IllegalStateException(mensagemVendido);
        }
        return atual;
    }
}
