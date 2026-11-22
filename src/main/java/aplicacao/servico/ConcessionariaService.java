package aplicacao.servico;

import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.repositorio.ConcessionariaRepository;

/** Casos de uso de concessionárias: listar, cadastrar, atualizar e excluir. */
public class ConcessionariaService {
    private final ConcessionariaRepository concessionariaRepository;

    /** @param concessionariaRepository onde as concessionárias são gravadas e lidas */
    public ConcessionariaService(ConcessionariaRepository concessionariaRepository) {
        this.concessionariaRepository = concessionariaRepository;
    }

    /** @return todas as concessionárias, em ordem de nome */
    public List<Concessionaria> listar() {
        return concessionariaRepository.listar();
    }

    /**
     * Valida (construtor de Concessionaria) e grava uma concessionária nova.
     *
     * @param nome     nome da loja
     * @param endereco endereço da loja
     * @return a concessionária gravada, já com ID
     * @throws IllegalArgumentException se algum dado for inválido
     * @throws IllegalStateException    se já existir outra com o mesmo nome ou endereço
     */
    public Concessionaria cadastrar(String nome, String endereco) {
        Concessionaria concessionaria = new Concessionaria(nome, endereco);
        concessionariaRepository.salvar(concessionaria);
        return concessionaria;
    }

    /**
     * Valida e grava os novos dados de uma concessionária existente.
     *
     * @param id       ID da concessionária
     * @param nome     novo nome
     * @param endereco novo endereço
     * @return a concessionária atualizada
     * @throws IllegalArgumentException se nenhuma foi informada, se ela não existe ou se algum dado for inválido
     * @throws IllegalStateException    se já existir outra com o mesmo nome ou endereço
     */
    public Concessionaria atualizar(Long id, String nome, String endereco) {
        buscarExistente(id, "Selecione uma concessionária para editar.");
        Concessionaria concessionaria = new Concessionaria(id, nome, endereco);
        concessionariaRepository.atualizar(concessionaria);
        return concessionaria;
    }

    /**
     * Exclui uma concessionária sem veículos nem vendedores.
     *
     * @param id ID da concessionária
     * @throws IllegalArgumentException se nenhuma foi informada ou se ela não existe
     * @throws IllegalStateException    se ela ainda tiver veículos ou vendedores
     */
    public void excluir(Long id) {
        buscarExistente(id, "Selecione uma concessionária para excluir.");
        concessionariaRepository.excluir(id);
    }

    private void buscarExistente(Long id, String mensagemSemId) {
        if (id == null) {
            throw new IllegalArgumentException(mensagemSemId);
        }
        if (concessionariaRepository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Concessionária não encontrada.");
        }
    }
}
