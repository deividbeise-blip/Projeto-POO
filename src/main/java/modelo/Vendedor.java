package modelo;

public class Vendedor {
    private static final double REDUCAO_COMISSAO_MOTO = 3;

    private Long id_vendedor;
    private final String name;
    private final String cpf;
    private final Double comissaoPercentual;
    private final Concessionaria concessionaria;

    // Construtor de CADASTRO (sem id): valida os dados. Quem grava no banco é o DAO/serviço.
    public Vendedor(String name, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        if (name == null || name.isBlank()) {
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
        this.name = name.trim();
        this.cpf = cpf.trim();
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
    }

    // Construtor DE CARGA (com id, vindo do banco)
    public Vendedor(Long id_vendedor, String name, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this(name, cpf, comissaoPercentual, concessionaria);
        this.id_vendedor = id_vendedor;
    }

    public Long getId_vendedor() {
        return id_vendedor;
    }

    public void setId_vendedor(Long id) {
        if (this.id_vendedor != null) {
            throw new IllegalStateException("O ID do vendedor já foi definido.");
        }
        this.id_vendedor = id;
    }

    public String getName() { return name; }
    public String getCpf() { return cpf; }
    public Double getComissaoPercentual() { return comissaoPercentual; }
    public Concessionaria getConcessionaria() { return concessionaria; }

    public double caucularcomissao(double valorVenda, Veiculo veiculo) {
        double percentual = comissaoPercentual;
        if (veiculo.isMoto()) {
            percentual = Math.max(0, percentual - REDUCAO_COMISSAO_MOTO);
        }
        return valorVenda * percentual / 100;
    }

    public boolean validarVendedor() {
        return name != null && cpf != null && comissaoPercentual > 0;
    }

    @Override
    public String toString() {
        return name;
    }
}