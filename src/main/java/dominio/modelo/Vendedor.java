package dominio.modelo;

import dominio.politica.PoliticaComissao;

/** Vendedor de uma concessionária, com o percentual de comissão que recebe por venda. */
public class Vendedor {
    /** Tamanho máximo do nome (coluna name_vendedor). */
    public static final int NOME_MAXIMO = 50;

    private Long idVendedor;
    private final String nome;
    private final String cpf;
    private final Double comissaoPercentual;
    private final Concessionaria concessionaria;

    // Construtor de CADASTRO (sem id): valida os dados. Quem grava no banco é o DAO/serviço.
    public Vendedor(String nome, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this.nome = validarNome(nome);
        this.cpf = validarCpf(cpf);
        validarComissao(comissaoPercentual);
        if (concessionaria == null) {
            throw new IllegalArgumentException("O vendedor precisa pertencer a uma concessionária.");
        }
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
    }

    /**
     * Regra do nome. Pública para a tela validar o campo antes de enviar.
     *
     * @param nome o nome digitado
     * @return o nome sem espaços nas pontas
     * @throws IllegalArgumentException se estiver vazio ou passar de {@link #NOME_MAXIMO} caracteres
     */
    public static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do vendedor é obrigatório.");
        }
        if (nome.trim().length() > NOME_MAXIMO) {
            throw new IllegalArgumentException("Nome deve ter no máximo " + NOME_MAXIMO + " caracteres.");
        }
        return nome.trim();
    }

    /**
     * Regra do CPF. Pontos, traços e espaços são ignorados.
     *
     * @param cpf o CPF digitado, com ou sem máscara
     * @return só os 11 dígitos do CPF
     * @throws IllegalArgumentException se não houver exatamente 11 dígitos
     */
    public static String validarCpf(String cpf) {
        String digitos = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        return digitos;
    }

    /**
     * Regra da comissão: percentual maior que 0 e no máximo 100.
     *
     * @param comissaoPercentual o percentual, ex.: 2.5 para 2,5%
     * @throws IllegalArgumentException se estiver fora desse intervalo
     */
    public static void validarComissao(Double comissaoPercentual) {
        if (comissaoPercentual == null || comissaoPercentual <= 0 || comissaoPercentual > 100) {
            throw new IllegalArgumentException("Comissão deve ser maior que 0 e no máximo 100.");
        }
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