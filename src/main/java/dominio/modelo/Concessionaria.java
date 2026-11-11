package dominio.modelo;

public class Concessionaria {
    private Long idConcessionaria;
    private final String nome;
    private final String endereco;

    // Construtor de CADASTRO: valida os dados. Quem grava no banco é o DAO/serviço.
    public Concessionaria(String nome, String endereco) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da concessionária é obrigatório.");
        }
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("Endereço da concessionária é obrigatório.");
        }
        this.nome = nome.trim();
        this.endereco = endereco.trim();
    }

    // Construtor DE CARGA: usado pelo DAO ao ler um registro existente.
    public Concessionaria(Long idConcessionaria, String nome, String endereco) {
        this(nome, endereco);
        this.idConcessionaria = idConcessionaria;
    }

    public Long getIdConcessionaria() {
        return idConcessionaria;
    }

    public void setIdConcessionaria(Long id) {
        if (this.idConcessionaria != null) {
            throw new IllegalStateException("O ID da concessionária já foi definido.");
        }
        this.idConcessionaria = id;
    }

    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }

    @Override
    public String toString() {
        return nome + " - " + endereco;
    }
}