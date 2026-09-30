package modelo;

public class Cliente {
    private Long id_cliente;
    private final String name_cliente;
    private final String cpf_cliente;
    private final String email_cliente;
    private final String phone_cliente;

    public Cliente(String name_cliente, String cpf_cliente, String email_cliente, String phone_cliente) {
        if (name_cliente == null || name_cliente.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        String cpf = cpf_cliente == null ? "" : cpf_cliente.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        if (email_cliente == null || !email_cliente.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        String telefone = phone_cliente == null ? "" : phone_cliente.replaceAll("\\D", "");
        if (telefone.length() < 10 || telefone.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter DDD + número (10 ou 11 dígitos).");
        }

        this.name_cliente = name_cliente.trim();
        this.cpf_cliente = cpf;
        this.email_cliente = email_cliente.trim();
        this.phone_cliente = telefone;
    }

    public Long getId_cliente() {
        return id_cliente;
    }

    /** O ID é gerado pelo banco e só pode ser definido uma vez (pelo DAO após o INSERT). */
    public void setId_cliente(Long id) {
        if (this.id_cliente != null) {
            throw new IllegalStateException("O ID do cliente já foi definido.");
        }
        this.id_cliente = id;
    }

    public String getName_cliente() { return name_cliente; }
    public String getCpf_cliente() { return cpf_cliente; }
    public String getEmail_cliente() { return email_cliente; }
    public String getPhone_cliente() { return phone_cliente; }

    @Override
    public String toString() {
        return name_cliente + " - " + cpf_cliente + " - " + email_cliente + " - " + phone_cliente;
    }
}