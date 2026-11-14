package dominio.modelo;

import java.time.LocalDate;

import dominio.excecao.VeiculoIndisponivelException;

/**
 * Veículo (carro ou moto) do estoque de uma concessionária.
 * Os dados nascem validados; o status só muda pelos métodos de transição
 * ({@link #vender()}, {@link #colocarEmManutencao()}, {@link #tornarDisponivel()}).
 */
public class Veiculo {
    /** Ano mais antigo aceito no cadastro. */
    public static final int ANO_MINIMO = 1900;

    private Long idVeiculo; // AUTO_INCREMENT
    private final String marca;
    private final String modelo;
    private final Integer ano;
    private final String placa;
    private final Double preco;
    private StatusVeiculo status;
    private final Concessionaria concessionaria;
    private final Boolean moto;   // true = moto, false = carro

    /**
     * Construtor de cadastro: só cria objetos válidos; quem grava no banco é o DAO/serviço.
     *
     * @param marca marca do veículo
     * @param modelo modelo do veículo
     * @param ano ano de fabricação, de {@link #ANO_MINIMO} até o ano que vem
     * @param placa placa do veículo
     * @param preco preço de venda, em reais, maior que zero
     * @param status situação inicial (normalmente DISPONIVEL)
     * @param concessionaria loja dona do veículo
     * @param moto true para moto, false para carro (define a regra de comissão)
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Veiculo(String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto) {
        validarMarca(marca);
        validarModelo(modelo);
        validarAno(ano);
        validarPlaca(placa);
        validarPreco(preco);
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        if (concessionaria == null) {
            throw new IllegalArgumentException("O veículo precisa pertencer a uma concessionária.");
        }
        if (moto == null) {
            throw new IllegalArgumentException("Informe se o veículo é moto ou carro.");
        }

        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.ano = ano;
        this.placa = placa.trim();
        this.preco = preco;
        this.status = status;
        this.concessionaria = concessionaria;
        this.moto = moto;
    }

    /**
     * Construtor de carga: usado pelo VeiculoDAO ao ler um registro existente. Aplica as mesmas validações.
     *
     * @param idVeiculo ID vindo do banco
     * @param marca marca do veículo
     * @param modelo modelo do veículo
     * @param ano ano de fabricação
     * @param placa placa do veículo
     * @param preco preço de venda, em reais
     * @param status situação gravada no banco
     * @param concessionaria loja dona do veículo
     * @param moto true para moto, false para carro
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Veiculo(Long idVeiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto) {
        this(marca, modelo, ano, placa, preco, status, concessionaria, moto);
        this.idVeiculo = idVeiculo;
    }

    /**
     * Regra da marca. Pública para a tela validar o campo antes de enviar.
     *
     * @param marca a marca digitada
     * @throws IllegalArgumentException se estiver vazia
     */
    public static void validarMarca(String marca) {
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("Marca é obrigatória.");
        }
    }

    /**
     * Regra do modelo.
     *
     * @param modelo o modelo digitado
     * @throws IllegalArgumentException se estiver vazio
     */
    public static void validarModelo(String modelo) {
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("Modelo é obrigatório.");
        }
    }

    /**
     * Regra do ano: de {@link #ANO_MINIMO} até o ano que vem (carros do próximo ano-modelo).
     *
     * @param ano o ano informado
     * @throws IllegalArgumentException se estiver fora desse intervalo
     */
    public static void validarAno(Integer ano) {
        int maximo = LocalDate.now().getYear() + 1;
        if (ano == null || ano < ANO_MINIMO || ano > maximo) {
            throw new IllegalArgumentException("Ano deve estar entre " + ANO_MINIMO + " e " + maximo + ".");
        }
    }

    /**
     * Regra da placa.
     *
     * @param placa a placa digitada
     * @throws IllegalArgumentException se estiver vazia
     */
    public static void validarPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa é obrigatória.");
        }
    }

    /**
     * Regra do preço.
     *
     * @param preco o preço informado
     * @throws IllegalArgumentException se for nulo, zero ou negativo
     */
    public static void validarPreco(Double preco) {
        if (preco == null || preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
    }

    /** @return ID gerado pelo banco, ou null se ainda não foi gravado */
    public Long getIdVeiculo() {
        return idVeiculo;
    }

    /**
     * Define o ID gerado pelo banco; só pode ser chamado uma vez (pelo DAO após o INSERT).
     *
     * @param id o ID gerado
     * @throws IllegalStateException se o ID já tiver sido definido
     */
    public void setIdVeiculo(Long id) {
        if (this.idVeiculo != null) {
            throw new IllegalStateException("O ID do veículo já foi definido.");
        }
        this.idVeiculo = id;
    }

    /** @return a marca, sem espaços nas pontas */
    public String getMarca() { return marca; }
    /** @return o modelo, sem espaços nas pontas */
    public String getModelo() { return modelo; }
    /** @return o ano de fabricação */
    public Integer getAno() { return ano; }
    /** @return a placa, sem espaços nas pontas */
    public String getPlaca() { return placa; }
    /** @return o preço de venda, em reais, sem desconto */
    public Double getPreco() { return preco; }
    /** @return a situação atual; nunca null */
    public StatusVeiculo getStatus() { return status; }
    /** @return a loja dona do veículo; nunca null */
    public Concessionaria getConcessionaria() { return concessionaria; }
    /** @return true se for moto, false se for carro */
    public Boolean isMoto() { return moto; }

    // ---- Transições de status controladas (não existe setStatus público) ----

    /** @return true se o veículo pode ser vendido agora (status DISPONIVEL) */
    public boolean verificarDisponibilidade() {
        return status == StatusVeiculo.DISPONIVEL;
    }

    /**
     * Marca o veículo como VENDIDO.
     *
     * @throws VeiculoIndisponivelException se ele já foi vendido ou está em manutenção
     */
    public void vender() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.VENDIDO) {
            throw new VeiculoIndisponivelException("Veículo já foi vendido e não pode ser vendido novamente.");
        }
        if (status == StatusVeiculo.EM_MANUTENCAO) {
            throw new VeiculoIndisponivelException("Veículo em manutenção não pode ser vendido.");
        }
        this.status = StatusVeiculo.VENDIDO;
    }

    /** Desfaz uma venda (ex.: falha ao gravar no banco). Só faz sentido para veículo vendido. */
    public void cancelarVenda() {
        if (status == StatusVeiculo.VENDIDO) {
            this.status = StatusVeiculo.DISPONIVEL;
        }
    }

    /**
     * Tira o veículo de venda, colocando-o EM_MANUTENCAO.
     *
     * @throws VeiculoIndisponivelException se ele já foi vendido
     */
    public void colocarEmManutencao() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.VENDIDO) {
            throw new VeiculoIndisponivelException("Veículo vendido não pode ir para manutenção.");
        }
        this.status = StatusVeiculo.EM_MANUTENCAO;
    }

    /**
     * Devolve o veículo à venda (status DISPONIVEL).
     *
     * @throws VeiculoIndisponivelException se ele já foi vendido
     */
    public void tornarDisponivel() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.VENDIDO) {
            throw new VeiculoIndisponivelException("Veículo vendido não pode voltar a ficar disponível.");
        }
        this.status = StatusVeiculo.DISPONIVEL;
    }

    /** @return "marca modelo (ano) - Placa X - R$ preço", usado nos combos das telas */
    @Override
    public String toString() {
        return String.format("%s %s (%d) - Placa %s - R$ %.2f", marca, modelo, ano, placa, preco);
    }
}
