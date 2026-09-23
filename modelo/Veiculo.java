package modelo;

import excecao.VeiculoIndisponivelException;

public class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private Double preco;
    private StatusVeiculo status;
    private Concessionaria concessionaria;
    private Boolean moto; //Veículo é moto ou carro, true = moto, false = carro
    private Boolean vendido; //Veículo foi vendido ou não, true = vendido, false = não vendido

    public Veiculo(String marca, String modelo, Integer ano, String placa, Double preco, StatusVeiculo status, Concessionaria concessionaria, Boolean moto, Boolean vendido) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.preco = preco;
        this.status = status;
        this.concessionaria = concessionaria;
        this.moto = moto;
        this.vendido = vendido;
    }
    /**
     * Os setter criados são apenas para os atributos que podem ser alterados após a criação do objeto, como placa, preço, status e concessionária.
     * Os outros atributos são considerados imutáveis e não possuem métodos set.
     */

    public Long getId() {
        return id;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public String getPlaca() {
        return placa;
    }

    public Double getPreco() {
        return preco;
    }

    public StatusVeiculo getStatus() {
        return status;
    }

    public Concessionaria getConcessionaria() {
        return concessionaria;
    }

    public Boolean getMoto() {
        return moto;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public void setStatus(StatusVeiculo status) {
        this.status = status;
    }

    public void setConcessionaria(Concessionaria concessionaria) {
        this.concessionaria = concessionaria;
    }

    public Boolean getVendido() {
        return vendido;
    }

    public void setVendido(Boolean vendido) {
        this.vendido = vendido;
    }

    public double calcularValorComDesconto(double percentual) {
        return this.preco - (this.preco * (percentual / 100));
    }

    public boolean verificarDisponibilidade() {
        return status != StatusVeiculo.VENDIDO;
    }

    /**
     * Realiza a venda do veículo, alterando seu estado para VENDIDO.
     * Caso o veículo já esteja vendido, a operação é considerada inválida
     * e uma VeiculoIndisponivelException é lançada, impedindo a alteração de estado.
     */
    public void vender() throws VeiculoIndisponivelException {
        if (!verificarDisponibilidade()) {
            throw new VeiculoIndisponivelException("Veículo já foi vendido e não pode ser vendido novamente.");
        }
        this.status = StatusVeiculo.VENDIDO;
        this.vendido = true;
    }
}
