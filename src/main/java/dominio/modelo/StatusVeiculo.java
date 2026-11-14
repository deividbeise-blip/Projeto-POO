package dominio.modelo;

/**
 * Situação de um veículo no estoque. As trocas são controladas pelo {@link Veiculo};
 * VENDIDO é final.
 */
public enum StatusVeiculo {
    /** Pode ser vendido. */
    DISPONIVEL,
    /** Já vendido; não volta a outro status. */
    VENDIDO,
    /** Fora de venda até voltar a ficar disponível. */
    EM_MANUTENCAO
}
