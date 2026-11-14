package dominio.modelo;

/** Loja da rede: tem veículos e vendedores. */
public class Concessionaria {
    /** Tamanho máximo do nome (coluna name_concessionaria). */
    public static final int NOME_MAXIMO = 50;
    /** Tamanho máximo do endereço (coluna address_concessionaria). */
    public static final int ENDERECO_MAXIMO = 100;

    private Long idConcessionaria;
    private final String nome;
    private final String endereco;

    /**
     * Construtor de cadastro: valida os dados; quem grava no banco é o DAO/serviço.
     *
     * @param nome nome da loja
     * @param endereco endereço da loja
     * @throws IllegalArgumentException se nome ou endereço forem inválidos
     */
    public Concessionaria(String nome, String endereco) {
        this.nome = validarNome(nome);
        this.endereco = validarEndereco(endereco);
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
            throw new IllegalArgumentException("Nome da concessionária é obrigatório.");
        }
        if (nome.trim().length() > NOME_MAXIMO) {
            throw new IllegalArgumentException("Nome deve ter no máximo " + NOME_MAXIMO + " caracteres.");
        }
        return nome.trim();
    }

    /**
     * Regra do endereço.
     *
     * @param endereco o endereço digitado
     * @return o endereço sem espaços nas pontas
     * @throws IllegalArgumentException se estiver vazio ou passar de {@link #ENDERECO_MAXIMO} caracteres
     */
    public static String validarEndereco(String endereco) {
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço da concessionária é obrigatório.");
        }
        if (endereco.trim().length() > ENDERECO_MAXIMO) {
            throw new IllegalArgumentException("Endereço deve ter no máximo " + ENDERECO_MAXIMO + " caracteres.");
        }
        return endereco.trim();
    }

    /**
     * Construtor de carga: usado pelo DAO ao ler um registro existente. Aplica as mesmas validações.
     *
     * @param idConcessionaria ID vindo do banco
     * @param nome nome da loja
     * @param endereco endereço da loja
     * @throws IllegalArgumentException se nome ou endereço forem inválidos
     */
    public Concessionaria(Long idConcessionaria, String nome, String endereco) {
        this(nome, endereco);
        this.idConcessionaria = idConcessionaria;
    }

    /** @return ID gerado pelo banco, ou null se ainda não foi gravada */
    public Long getIdConcessionaria() {
        return idConcessionaria;
    }

    /**
     * Define o ID gerado pelo banco; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdConcessionaria(Long id) {
        if (this.idConcessionaria != null) {
            throw new IllegalStateException("O ID da concessionária já foi definido.");
        }
        this.idConcessionaria = id;
    }

    /** @return o nome já validado, sem espaços nas pontas */
    public String getNome() { return nome; }
    /** @return o endereço já validado, sem espaços nas pontas */
    public String getEndereco() { return endereco; }

    /** @return "nome - endereço", usado nos combos das telas */
    @Override
    public String toString() {
        return nome + " - " + endereco;
    }
}