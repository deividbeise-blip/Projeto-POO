package dominio.modelo;

public class Cliente {
    private Long id_cliente;
    private final String name_cliente;
    private final String cpf_cliente;
    private final String email_cliente;
    private final String phone_cliente;

    public Cliente(String name_cliente, String cpf_cliente, String email_cliente, String phone_cliente) {
        validarNome(name_cliente);
        validarCpf(cpf_cliente);
        validarEmail(email_cliente);
        validarTelefone(phone_cliente);

        this.name_cliente = name_cliente.trim();
        this.cpf_cliente = cpf_cliente.replaceAll("\\D", "");
        this.email_cliente = email_cliente.trim();
        this.phone_cliente = phone_cliente.replaceAll("\\D", "");
    }

    // ---- Regras de validação (usadas pelo construtor e pela tela, campo a campo) ----

    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
    }

    public static void validarCpf(String cpf) {
        String digitos = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }

    public static void validarTelefone(String telefone) {
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter DDD + número (10 ou 11 dígitos).");
        }
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