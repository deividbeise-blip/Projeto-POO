package dominio.modelo;

import dominio.politica.PoliticaComissao;

public class Vendedor {
    private Long idVendedor;
    private final String nome;
    private final String cpf;
    private final Double comissaoPercentual;
    private final Concessionaria concessionaria;

    // Construtor de CADASTRO (sem id): valida os dados. Quem grava no banco é o DAO/serviço.
    public Vendedor(String nome, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do vendedor é obrigatório.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF do vendedor é obrigatório.");
        }
        if (comissaoPercentual == null || comissaoPercentual <= 0 || comissaoPercentual > 100) {
            throw new IllegalArgumentException("Comissão deve estar entre 0 (exclusivo) e 100.");
        }
        if (concessionaria == null) {
            throw new IllegalArgumentException("O vendedor precisa pertencer a uma concessionária.");
        }
        this.nome = nome.trim();
        this.cpf = cpf.trim();
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
    }

    // Construtor DE CARGA (com id, vindo do banco)
    public Vendedor(Long idVendedor, String nome, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this(nome, cpf, comissaoPercentual, concessionaria);
        this.idVendedor = idVendedor;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Long id) {
        if (this.idVendedor != null) {
            throw new IllegalStateException("O ID do vendedor já foi definido.");
        }
        this.idVendedor = id;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public Double getComissaoPercentual() { return comissaoPercentual; }
    public Concessionaria getConcessionaria() { return concessionaria; }

    /** A regra (carro, moto...) vem de fora: o Vendedor só fornece o seu percentual. */
    public double calcularComissao(double valorVenda, PoliticaComissao politica) {
        return politica.calcular(valorVenda, comissaoPercentual);
    }

    public boolean validarVendedor() {
        return comissaoPercentual > 0;
    }

    @Override
    public String toString() {
        return nome;
    }
}