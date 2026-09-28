package modelo;

import excecao.VeiculoIndisponivelException;

import java.time.LocalDate;

public class Veiculo {
    private Long id_veiculo; // AUTO_INCREMENT
    private final String marca;
    private final String modelo;
    private final Integer ano;
    private String placa;
    private Double preco;
    private StatusVeiculo status;
    private final Concessionaria concessionaria;
    private final Boolean moto;   // true = moto, false = carro
    private Boolean vendido;      // sempre acompanha o status (true quando VENDIDO)

    // Construtor de CADASTRO: só cria objetos válidos. Quem grava no banco é o DAO/serviço.
    public Veiculo(String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto, Boolean vendido) {
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("Marca é obrigatória.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("Modelo é obrigatório.");
        }
        if (ano == null || ano < 1900 || ano > LocalDate.now().getYear() + 1) {
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
        this.vendido = (status == StatusVeiculo.VENDIDO) || Boolean.TRUE.equals(vendido);
    }

    // Construtor DE CARGA: usado pelo VeiculoDAO ao ler um registro existente.
    public Veiculo(Long id_veiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                   StatusVeiculo status, Concessionaria concessionaria, Boolean moto, Boolean vendido) {
        this(marca, modelo, ano, placa, preco, status, concessionaria, moto, vendido);
        this.id_veiculo = id_veiculo;
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

    public Long getId_veiculo() {
        return id_veiculo;
    }

    public void setId_veiculo(Long id) {
        if (this.id_veiculo != null) {
            throw new IllegalStateException("O ID do veículo já foi definido.");
        }
        this.id_veiculo = id;
    }

    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Integer getAno() { return ano; }
    public String getPlaca() { return placa; }
    public Double getPreco() { return preco; }
    public StatusVeiculo getStatus() { return status; }
    public Concessionaria getConcessionaria() { return concessionaria; }
    public Boolean isMoto() { return moto; }
    public Boolean getVendido() { return vendido; }

    // Dados que podem mudar depois de criado, sempre validados (persistência fica no serviço)
    public void setPlaca(String placa) {
        validarPlaca(placa);
        this.placa = placa.trim();
    }

    public void setPreco(Double preco) {
        validarPreco(preco);
        this.preco = preco;
    }

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
        this.vendido = true;
    }

    /** Desfaz uma venda (ex.: falha ao gravar no banco). Só faz sentido para veículo vendido. */
    public void cancelarVenda() {
        if (status == StatusVeiculo.VENDIDO) {
            this.status = StatusVeiculo.DISPONIVEL;
            this.vendido = false;
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

    public double calcularValorComDesconto(double percentual) {
        if (percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100.");
        }
        return this.preco - (this.preco * (percentual / 100));
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " (" + ano + ") - Placa " + placa;
    }
}
