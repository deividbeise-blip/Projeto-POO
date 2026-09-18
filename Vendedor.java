public class Vendedor {
    private Long id;
    private String name;
    private String cpf;
    private Double comissaoPercentual;

    public Vendedor(String name, String cpf, Double comissaoPercentual) {
        this.name = name;
        this.cpf = cpf;
        this.comissaoPercentual = comissaoPercentual;
    }
}
