package dominio.modelo;

/**
 * Entidade de domínio que representa um cliente da loja.
 * Os dados cadastrais (nome, CPF, e-mail e telefone) são definidos no construtor e não mudam
 * depois: os campos são {@code final}. Só o ID pode ser atribuído, uma única vez, depois que o
 * cliente é gravado no banco. Para "editar" um cliente, cria-se um novo objeto com os dados
 * corrigidos e atribui-se a ele o ID do cliente existente (é o que o
 * {@link aplicacao.servico.ClienteService#atualizar} faz).
 * Os dados são validados e normalizados na construção, então nenhum {@code Cliente} inválido
 * chega a existir:
 *   nome e e-mail ficam sem espaços nas pontas;
 *   CPF e telefone ficam só com dígitos (pontos, traços, parênteses e espaços são removidos).
 * As regras estão expostas como métodos estáticos ({@code validarNome}, {@code validarCpf},
 * {@code validarEmail} e {@code validarTelefone}) para que a tela possa marcar cada campo
 * individualmente.
 *
 * @see aplicacao.servico.ClienteService
 * @see dominio.repositorio.ClienteRepository
 */
public class Cliente {
    /** Identificador gerado pelo banco; {@code null} enquanto o cliente ainda não foi gravado. */
    private Long id_cliente;
    /** Nome completo, sem espaços nas pontas. */
    private final String name_cliente;
    /** CPF só com os 11 dígitos, sem pontuação. */
    private final String cpf_cliente;
    /** E-mail, sem espaços nas pontas. */
    private final String email_cliente;
    /** Telefone só com dígitos: DDD + número (10 ou 11 dígitos). */
    private final String phone_cliente;

    /**
     * Cria um cliente validando e normalizando os dados recebidos.
     * <p>
     * As validações rodam na ordem nome, CPF, e-mail e telefone; a primeira que falhar interrompe
     * a construção.
     *
     * @param name_cliente  nome do cliente; obrigatório
     * @param cpf_cliente   CPF com ou sem pontuação; precisa ter 11 dígitos
     * @param email_cliente e-mail no formato {@code algo@dominio.ext}
     * @param phone_cliente telefone com ou sem pontuação; precisa ter DDD + número (10 ou 11 dígitos)
     * @throws IllegalArgumentException se algum dado for inválido; a mensagem diz qual
     */
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

    /**
     * Valida o nome do cliente.
     *
     * @param nome o nome digitado
     * @throws IllegalArgumentException se o nome for nulo ou estiver em branco
     */
    public static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório.");
        }
    }

    /**
     * Valida o CPF. Só confere a quantidade de dígitos (11); não calcula os dígitos verificadores.
     * Pontos, traços e espaços são ignorados.
     *
     * @param cpf o CPF digitado, com ou sem pontuação
     * @throws IllegalArgumentException se o CPF for nulo ou não tiver exatamente 11 dígitos
     */
    public static void validarCpf(String cpf) {
        String digitos = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos.");
        }
    }

    /**
     * Valida o e-mail. A checagem é simples: precisa ter o formato {@code algo@dominio.ext}, sem
     * espaços e com um único {@code @}.
     *
     * @param email o e-mail digitado
     * @throws IllegalArgumentException se o e-mail for nulo ou não tiver esse formato
     */
    public static void validarEmail(String email) {
        if (email == null || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
    }

    /**
     * Valida o telefone. Parênteses, traços e espaços são ignorados.
     *
     * @param telefone o telefone digitado, com ou sem pontuação
     * @throws IllegalArgumentException se o telefone for nulo ou não tiver 10 ou 11 dígitos (DDD + número)
     */
    public static void validarTelefone(String telefone) {
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitos.length() < 10 || digitos.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter DDD + número (10 ou 11 dígitos).");
        }
    }

    /**
     * Devolve o ID do cliente.
     *
     * @return o ID gerado pelo banco, ou {@code null} se o cliente ainda não foi gravado
     */
    public Long getId_cliente() {
        return id_cliente;
    }

    /**
     * Define o ID do cliente. O ID é gerado pelo banco e só pode ser definido uma vez (pelo DAO
     * após o INSERT, ou pelo serviço ao montar o cliente que será atualizado).
     *
     * @param id o ID do cliente
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setId_cliente(Long id) {
        if (this.id_cliente != null) {
            throw new IllegalStateException("O ID do cliente já foi definido.");
        }
        this.id_cliente = id;
    }

    /** @return o nome do cliente, sem espaços nas pontas */
    public String getName_cliente() { return name_cliente; }

    /** @return o CPF do cliente, só com os 11 dígitos */
    public String getCpf_cliente() { return cpf_cliente; }

    /** @return o e-mail do cliente, sem espaços nas pontas */
    public String getEmail_cliente() { return email_cliente; }

    /** @return o telefone do cliente, só com dígitos (DDD + número) */
    public String getPhone_cliente() { return phone_cliente; }

    /**
     * Texto usado, por exemplo, no combo de clientes da tela de vendas.
     *
     * @return {@code nome - cpf - e-mail - telefone}
     */
    @Override
    public String toString() {
        return name_cliente + " - " + cpf_cliente + " - " + email_cliente + " - " + phone_cliente;
    }
}