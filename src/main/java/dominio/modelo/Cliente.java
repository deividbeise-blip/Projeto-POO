package dominio.modelo;

public class Cliente {
    private Long idCliente;
    private final String nome;
    private final String cpf;
    private final String email;
    private final String telefone;

    public Cliente(String nome, String cpf, String email, String telefone) {
        this.nome = validarNome(nome);
        this.cpf = validarCpf(cpf);
        this.email = validarEmail(email);
        this.telefone = validarTelefone(telefone);
    }

    /**
     * Regra do nome do cliente. Pública para a tela validar o campo antes de enviar.
     *
     * @param nome o nome digitado
     * @return o nome sem espaços nas pontas
     * @throws IllegalArgumentException se o nome estiver vazio
     */
    public static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
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
        String cpfNormalizado = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (cpfNormalizado.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
        return cpfNormalizado;
    }

    /**
     * Regra do e-mail.
     *
     * @param email o e-mail digitado
     * @return o e-mail sem espaços nas pontas
     * @throws IllegalArgumentException se o formato for inválido
     */
    public static String validarEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return email.trim();
    }

    /**
     * Regra do telefone. Parênteses, traços e espaços são ignorados.
     *
     * @param telefone o telefone digitado, com ou sem máscara
     * @return só os dígitos (DDD + número)
     * @throws IllegalArgumentException se não tiver 10 ou 11 dígitos
     */
    public static String validarTelefone(String telefone) {
        String telefoneNormalizado = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (telefoneNormalizado.length() < 10 || telefoneNormalizado.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter DDD + número (10 ou 11 dígitos).");
        }
        return telefoneNormalizado;
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
