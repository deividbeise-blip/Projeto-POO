package dominio.modelo;

public class Cliente {
    private Long idCliente;
    private final String nome;
    private final String cpf;
    private final String email;
    private final String telefone;

    public Cliente(String nome, String cpf, String email, String telefone) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
        String cpfNormalizado = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (cpfNormalizado.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        String telefoneNormalizado = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (telefoneNormalizado.length() < 10 || telefoneNormalizado.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter DDD + número (10 ou 11 dígitos).");
        }

        this.nome = nome.trim();
        this.cpf = cpfNormalizado;
        this.email = email.trim();
        this.telefone = telefoneNormalizado;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    /** O ID é gerado pelo banco e só pode ser definido uma vez (pelo DAO após o INSERT). */
    public void setIdCliente(Long id) {
        if (this.idCliente != null) {
            throw new IllegalStateException("O ID do cliente já foi definido.");
        }
        this.idCliente = id;
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }

    @Override
    public String toString() {
        return nome + " - " + cpf + " - " + email + " - " + telefone;
    }
}