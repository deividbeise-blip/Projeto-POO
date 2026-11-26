package dominio.modelo;

/**
 * Sabe criar um tipo concreto de {@link Veiculo} a partir dos dados do cadastro ou do banco.
 * Os construtores de carga das subclasses já têm essa forma, então basta registrar
 * {@code Carro::new}, {@code Moto::new}... no {@link RegistroTiposVeiculo}.
 */
@FunctionalInterface
public interface CriadorVeiculo {
    /**
     * Cria o veículo, aplicando as validações de {@link Veiculo}.
     *
     * @param idVeiculo ID vindo do banco, ou null para um veículo ainda não gravado
     * @param marca marca do veículo
     * @param modelo modelo do veículo
     * @param ano ano de fabricação
     * @param placa placa do veículo, com ou sem hífen
     * @param preco preço de venda, em reais
     * @param status situação do veículo
     * @param concessionaria loja dona do veículo
     * @return o veículo do tipo concreto deste criador
     * @throws IllegalArgumentException se algum dado for inválido ou nulo
     */
    Veiculo criar(Long idVeiculo, String marca, String modelo, Integer ano, String placa, Double preco,
                  StatusVeiculo status, Concessionaria concessionaria);
}
