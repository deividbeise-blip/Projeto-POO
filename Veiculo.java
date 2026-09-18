public class Veiculo {
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private Double preco;
    private String status;
    private Concessionaria concessionaria;

    public Veiculo(String marca, String modelo, Integer ano, String placa, Double preco, String status, Concessionaria concessionaria) {
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.preco = preco;
        this.status = status;
        this.concessionaria = concessionaria;
    }
}
