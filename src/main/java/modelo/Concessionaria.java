package modelo;

public class Concessionaria {
    private Long id_concessionaria;
    private final String name_concessionaria;
    private final String address_concessionaria;

    // Construtor de CADASTRO: valida os dados. Quem grava no banco é o DAO/serviço.
    public Concessionaria(String name_concessionaria, String address_concessionaria) {
        if (name_concessionaria == null || name_concessionaria.isBlank()) {
            throw new IllegalArgumentException("Nome da concessionária é obrigatório.");
        }
        if (address_concessionaria == null || address_concessionaria.isBlank()) {
            throw new IllegalArgumentException("Endereço da concessionária é obrigatório.");
        }
        this.name_concessionaria = name_concessionaria.trim();
        this.address_concessionaria = address_concessionaria.trim();
    }

    // Construtor DE CARGA: usado pelo DAO ao ler um registro existente.
    public Concessionaria(Long id_concessionaria, String name_concessionaria, String address_concessionaria) {
        this(name_concessionaria, address_concessionaria);
        this.id_concessionaria = id_concessionaria;
    }

    public Long getId_concessionaria() {
        return id_concessionaria;
    }

    public void setId_concessionaria(Long id) {
        if (this.id_concessionaria != null) {
            throw new IllegalStateException("O ID da concessionária já foi definido.");
        }
        this.id_concessionaria = id;
    }

    public String getName_concessionaria() { return name_concessionaria; }
    public String getAddress_concessionaria() { return address_concessionaria; }
}
