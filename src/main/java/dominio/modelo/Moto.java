package dominio.modelo;

import dominio.politica.ComissaoMoto;
import dominio.politica.PoliticaComissao;

/** Moto do estoque. A comissão do vendedor é reduzida ({@link ComissaoMoto}). */
public class Moto extends Veiculo {

    /**
     * Construtor de cadastro (veículo ainda sem ID).
     *
     * @param marca marca da moto
     * @param modelo modelo da moto
     * @param ano ano de fabricação, de {@link Veiculo#ANO_MINIMO} até o ano que vem
     * @param placa placa no padrão brasileiro (ABC1234 ou ABC1D23), com ou sem hífen
     * @param preco preço de venda, em reais, maior que zero
     * @param status situação inicial (normalmente DISPONIVEL)
     * @param concessionaria loja dona da moto
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Moto(String marca, String modelo, Integer ano, String placa, Double preco,
                StatusVeiculo status, Concessionaria concessionaria) {
        super(marca, modelo, ano, placa, preco, status, concessionaria);
    }

    /**
     * Construtor de carga (registro lido do banco ou em edição).
     *
     * @param idVeiculo ID vindo do banco, ou null se ainda não foi gravado
     * @param marca marca da moto
     * @param modelo modelo da moto
     * @param ano ano de fabricação
     * @param placa placa da moto
     * @param preco preço de venda, em reais
     * @param status situação da moto
     * @param concessionaria loja dona da moto
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    public Moto(Long idVeiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                StatusVeiculo status, Concessionaria concessionaria) {
        super(idVeiculo, marca, modelo, ano, placa, preco, status, concessionaria);
    }

    /** @return {@link ComissaoMoto}: o percentual do vendedor com redução */
    @Override
    public PoliticaComissao politicaComissao() {
        return new ComissaoMoto();
    }

    /** @return "Moto" */
    @Override
    public String getTipo() {
        return "Moto";
    }
}
