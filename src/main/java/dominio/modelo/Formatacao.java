package dominio.modelo;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Formatação de valores no padrão brasileiro, usada nos recibos e nas telas.
 * É o único lugar que define como moeda e data aparecem para o usuário.
 */
public final class Formatacao {
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatacao() { }

    /**
     * Formata um valor em reais: 74100.0 -> "R$ 74.100,00".
     *
     * @param valor o valor em reais
     * @return o texto com "R$", separador de milhar e duas casas decimais
     */
    public static String moeda(double valor) {
        // NumberFormat não é thread-safe; criar um por chamada é barato.
        // O Java separa "R$" do número com espaço não separável; troca por espaço comum.
        return NumberFormat.getCurrencyInstance(BRASIL).format(valor).replace(' ', ' ');
    }

    /**
     * Formata uma data: 2026-11-21 -> "21/11/2026".
     *
     * @param data a data
     * @return o texto no formato dd/MM/yyyy
     */
    public static String data(LocalDate data) {
        return DATA.format(data);
    }
}
