package dominio.modelo;

import dominio.politica.ComissaoCarro;
import dominio.politica.PoliticaComissao;

/** Carro do estoque. O vendedor recebe a comissão cheia ({@link ComissaoCarro}). */
public class Carro extends Veiculo {

    /**
     * Construtor de cadastro (veículo ainda sem ID).
     *
     * @param marca marca do carro
     * @param modelo modelo do carro
     * @param ano ano de fabricação, de {@link Veiculo#ANO_MINIMO} até o ano que vem
     * @param placa placa no padrão brasileiro (ABC1234 ou ABC1D23), com ou sem hífen
     * @param preco preço de venda, em reais, maior que zero
     * @param status situação inicial (normalmente DISPONIVEL)
     * @param concessionaria loja dona do carro
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Carro(String marca, String modelo, Integer ano, String placa, Double preco,
                 StatusVeiculo status, Concessionaria concessionaria) {
        super(marca, modelo, ano, placa, preco, status, concessionaria);
    }

    /**
     * Construtor de carga (registro lido do banco ou em edição).
     *
     * @param idVeiculo ID vindo do banco, ou null se ainda não foi gravado
     * @param marca marca do carro
     * @param modelo modelo do carro
     * @param ano ano de fabricação
     * @param placa placa do carro
     * @param preco preço de venda, em reais
     * @param status situação do carro
     * @param concessionaria loja dona do carro
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Carro(Long idVeiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                 StatusVeiculo status, Concessionaria concessionaria) {
        super(idVeiculo, marca, modelo, ano, placa, preco, status, concessionaria);
    }

    /** @return {@link ComissaoCarro}: o percentual do vendedor sem redução */
    @Override
    public PoliticaComissao politicaComissao() {
        return new ComissaoCarro();
    }

    /** @return "Carro" */
    @Override
    public String getTipo() {
        return "Carro";
    }
}
