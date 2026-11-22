package dominio.modelo;

/**
 * Cliente da concessionária, comprador nas vendas.
 * Os dados são validados e normalizados na criação e não mudam depois;
 * só o ID é definido mais tarde, quando o cliente é gravado no banco.
 */
public class Cliente {
    private Long idCliente;
    private final String nome;
    private final String cpf;
    private final String email;
    private final String telefone;

    /**
     * Cria um cliente ainda sem ID, aplicando as regras de cada campo.
     * CPF e telefone são guardados só com os dígitos; nome e e-mail, sem espaços nas pontas.
     *
     * @param nome nome do cliente
     * @param cpf CPF com ou sem máscara
     * @param email e-mail do cliente
     * @param telefone telefone com DDD, com ou sem máscara
     * @throws IllegalArgumentException se algum campo não passar na validação
     */
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

    /**
     * ID gerado pelo banco.
     *
     * @return o ID, ou null se o cliente ainda não foi gravado
     */
    public Long getIdCliente() {
        return idCliente;
    }

    /**
     * O ID é gerado pelo banco e só pode ser definido uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado pelo banco
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdCliente(Long id) {
        if (this.idCliente != null) {
            throw new IllegalStateException("O ID do cliente já foi definido.");
        }
        this.idCliente = id;
    }

    /** @return o nome já validado, sem espaços nas pontas */
    public String getNome() { return nome; }
    /** @return o CPF só com os 11 dígitos, sem máscara (use {@code ClienteTableModel.formatarCpf} para exibir) */
    public String getCpf() { return cpf; }
    /** @return o e-mail já validado, sem espaços nas pontas */
    public String getEmail() { return email; }
    /** @return o telefone só com os dígitos (DDD + número), sem máscara */
    public String getTelefone() { return telefone; }

    /**
     * Resumo do cliente para depuração e logs.
     *
     * @return nome, CPF, e-mail e telefone separados por " - ", sem máscara
     */
    @Override
    public String toString() {
        return nome + " - " + cpf + " - " + email + " - " + telefone;
    }
}
