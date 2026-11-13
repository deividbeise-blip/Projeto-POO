package aplicacao.servico;

import java.util.List;

import dominio.modelo.Concessionaria;
import dominio.modelo.Vendedor;
import dominio.repositorio.ConcessionariaRepository;
import dominio.repositorio.VendedorRepository;

/** Casos de uso de vendedores: listar, cadastrar, atualizar e excluir. */
public class VendedorService {
    private final VendedorRepository vendedorRepository;
    private final ConcessionariaRepository concessionariaRepository;

    /**
     * @param vendedorRepository       onde os vendedores são gravados e lidos
     * @param concessionariaRepository usado para listar as concessionárias do formulário
     */
    public VendedorService(VendedorRepository vendedorRepository,
                           ConcessionariaRepository concessionariaRepository) {
        this.vendedorRepository = vendedorRepository;
        this.concessionariaRepository = concessionariaRepository;
    }

    /** @return todos os vendedores, em ordem de nome */
    public List<Vendedor> listar() {
        return vendedorRepository.listar();
    }

    /** @return as concessionárias cadastradas, para o combo do formulário */
    public List<Concessionaria> listarConcessionarias() {
        return concessionariaRepository.listar();
    }

    /**
     * Valida (construtor de Vendedor) e grava um vendedor novo.
     *
     * @param nome               nome do vendedor
     * @param cpf                CPF, com ou sem máscara
     * @param comissaoPercentual percentual de comissão (maior que 0, até 100)
     * @param concessionaria     concessionária onde ele trabalha
     * @return o vendedor gravado, já com ID
     * @throws IllegalArgumentException se algum dado for inválido
     * @throws IllegalStateException    se já existir outro com o mesmo nome ou CPF
     */
    public Vendedor cadastrar(String nome, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        Vendedor vendedor = new Vendedor(nome, cpf, comissaoPercentual, concessionaria);
        vendedorRepository.salvar(vendedor);
        return vendedor;
    }

    /**
     * Valida e grava os novos dados de um vendedor existente.
     *
     * @param id                 ID do vendedor
     * @param nome               novo nome
     * @param cpf                novo CPF, com ou sem máscara
     * @param comissaoPercentual novo percentual de comissão
     * @param concessionaria     nova concessionária
     * @return o vendedor atualizado
     * @throws IllegalArgumentException se nenhum foi informado, se ele não existe ou se algum dado for inválido
     * @throws IllegalStateException    se já existir outro com o mesmo nome ou CPF
     */
    public Vendedor atualizar(Long id, String nome, String cpf, Double comissaoPercentual,
                              Concessionaria concessionaria) {
        buscarExistente(id, "Selecione um vendedor para editar.");
        Vendedor vendedor = new Vendedor(id, nome, cpf, comissaoPercentual, concessionaria);
        vendedorRepository.atualizar(vendedor);
        return vendedor;
    }

    /**
     * Exclui um vendedor sem vendas.
     *
     * @param id ID do vendedor
     * @throws IllegalArgumentException se nenhum foi informado ou se ele não existe
     * @throws IllegalStateException    se ele tiver vendas
     */
    public void excluir(Long id) {
        buscarExistente(id, "Selecione um vendedor para excluir.");
        vendedorRepository.excluir(id);
    }

    private void buscarExistente(Long id, String mensagemSemId) {
        if (id == null) {
            throw new IllegalArgumentException(mensagemSemId);
        }
        if (vendedorRepository.buscarPorId(id) == null) {
            throw new IllegalArgumentException("Vendedor não encontrado.");
        }
    }
}
