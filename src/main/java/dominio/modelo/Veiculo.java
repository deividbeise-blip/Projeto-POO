package dominio.modelo;

import java.time.LocalDate;

import dominio.excecao.VeiculoIndisponivelException;

public class Veiculo {
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

    // Construtor de CADASTRO: só cria objetos válidos. Quem grava no banco é o DAO/serviço.
    public Veiculo(String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto) {
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("Marca é obrigatória.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("Modelo é obrigatório.");
        }
        if (ano == null || ano < ANO_MINIMO || ano > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("Ano inválido.");
        }
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

    // Construtor DE CARGA: usado pelo VeiculoDAO ao ler um registro existente.
    public Veiculo(Long idVeiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto) {
        this(marca, modelo, ano, placa, preco, status, concessionaria, moto);
        this.idVeiculo = idVeiculo;
    }

    private static void validarPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Placa é obrigatória.");
        }
    }

    private static void validarPreco(Double preco) {
        if (preco == null || preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero.");
        }
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long id) {
        if (this.idVeiculo != null) {
            throw new IllegalStateException("O ID do veículo já foi definido.");
        }
        this.idVeiculo = id;
    }

    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAno() { return ano; }
    public String getPlaca() { return placa; }
    public Double getPreco() { return preco; }
    public StatusVeiculo getStatus() { return status; }
    public Concessionaria getConcessionaria() { return concessionaria; }
    public Boolean isMoto() { return moto; }

    // ---- Transições de status controladas (não existe setStatus público) ----

    public boolean verificarDisponibilidade() {
        return status == StatusVeiculo.DISPONIVEL;
    }

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

    public void colocarEmManutencao() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.VENDIDO) {
            throw new VeiculoIndisponivelException("Veículo vendido não pode ir para manutenção.");
        }
        this.status = StatusVeiculo.EM_MANUTENCAO;
    }

    public void tornarDisponivel() throws VeiculoIndisponivelException {
        if (status == StatusVeiculo.VENDIDO) {
            throw new VeiculoIndisponivelException("Veículo vendido não pode voltar a ficar disponível.");
        }
        this.status = StatusVeiculo.DISPONIVEL;
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d) - Placa %s - R$ %.2f", marca, modelo, ano, placa, preco);
    }
}
