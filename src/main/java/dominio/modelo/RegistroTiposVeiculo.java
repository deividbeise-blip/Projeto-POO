package dominio.modelo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tipos de veículo aceitos pelo sistema e como criar cada um. É o único lugar que liga
 * o nome de um tipo (ex.: "CARRO") à sua classe: serviço, DAO e tela consultam este registro
 * em vez de usar if/switch. Para um tipo novo, basta registrá-lo no {@code AppConfig}.
 * As chaves são guardadas em maiúsculas e a busca ignora maiúsculas/minúsculas
 * ("Carro", "carro" e "CARRO" são o mesmo tipo).
 */
public class RegistroTiposVeiculo {
    // LinkedHashMap: mantém a ordem de registro, que é a ordem mostrada nos combos da tela.
    private final Map<String, CriadorVeiculo> criadores = new LinkedHashMap<>();

    /**
     * Registra um tipo de veículo.
     *
     * @param tipo nome do tipo (ex.: "CARRO"); deve ser igual a {@link Veiculo#getTipo()} em maiúsculas
     * @param criador como criar o veículo desse tipo (ex.: {@code Carro::new})
     * @return este registro, para encadear chamadas
     * @throws IllegalArgumentException se o tipo for vazio, o criador for nulo ou o tipo já estiver registrado
     */
    public RegistroTiposVeiculo registrar(String tipo, CriadorVeiculo criador) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("O nome do tipo de veículo é obrigatório.");
        }
        if (criador == null) {
            throw new IllegalArgumentException("Informe como criar o tipo de veículo " + tipo + ".");
        }
        String chave = normalizar(tipo);
        if (criadores.containsKey(chave)) {
            throw new IllegalArgumentException("Tipo de veículo já registrado: " + chave + ".");
        }
        criadores.put(chave, criador);
        return this;
    }

    /**
     * Cria um veículo do tipo informado.
     *
     * @param tipo nome do tipo (ex.: "CARRO" ou "Carro")
     * @param idVeiculo ID vindo do banco, ou null para um veículo ainda não gravado
     * @param marca marca do veículo
     * @param modelo modelo do veículo
     * @param ano ano de fabricação
     * @param placa placa do veículo, com ou sem hífen
     * @param preco preço de venda, em reais
     * @param status situação do veículo
     * @param concessionaria loja dona do veículo
     * @return o veículo do tipo pedido
     * @throws IllegalArgumentException se o tipo for vazio ou desconhecido, ou se algum dado for inválido
     */
    public Veiculo criar(String tipo, Long idVeiculo, String marca, String modelo, Integer ano, String placa,
                         Double preco, StatusVeiculo status, Concessionaria concessionaria) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo de veículo é obrigatório.");
        }
        CriadorVeiculo criador = criadores.get(normalizar(tipo));
        if (criador == null) {
            throw new IllegalArgumentException("Tipo de veículo desconhecido: " + tipo
                    + ". Tipos aceitos: " + String.join(", ", criadores.keySet()) + ".");
        }
        return criador.criar(idVeiculo, marca, modelo, ano, placa, preco, status, concessionaria);
    }

    /**
     * @return os tipos registrados (ex.: ["CARRO", "MOTO"]), em maiúsculas e na ordem de registro;
     *         a lista é uma cópia, alterá-la não muda o registro
     */
    public List<String> tiposDisponiveis() {
        return new ArrayList<>(criadores.keySet());
    }

    private static String normalizar(String tipo) {
        return tipo.trim().toUpperCase(Locale.ROOT);
    }
}
