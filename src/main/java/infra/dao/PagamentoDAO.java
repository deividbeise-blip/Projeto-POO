package infra.dao;

import java.sql.Date;

import dominio.modelo.Pagamento;
import dominio.modelo.Venda;
import dominio.repositorio.PagamentoRepository;
import infra.conexao.ConnectionFactory;

public class PagamentoDAO implements PagamentoRepository {

    private final ExecutorSql executor;

    public PagamentoDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    @Override
    public void salvar(Pagamento pagamento) {
        Venda venda = pagamento.getVenda();

        long id = executor.inserir(
                """
                INSERT INTO pagamento
                (forma_pagamento, valor_pagamento, data_pagamento, cliente_pagamento, veiculo_pagamento)
                VALUES (?, ?, ?, ?, ?)
                """,
                comando -> {
                    comando.setString(1, paraColunaEnum(pagamento.getFormaPagamento()));
                    comando.setDouble(2, pagamento.getValorPago());
                    comando.setDate(3, Date.valueOf(pagamento.getDataPagamento()));
                    comando.setLong(4, venda.getCliente().getIdCliente());
                    comando.setLong(5, venda.getVeiculo().getIdVeiculo());
                },
                "Erro ao salvar pagamento no banco.");
        pagamento.setIdPagamento(id);
    }

    /**
     * A coluna forma_pagamento é ENUM('Dinheiro','Cartao','Pix'); o domínio usa "PIX" e "Cartão"
     * (é o que a tela mostra). Converte para o valor exato do enum do banco.
     */
    private String paraColunaEnum(String formaPagamento) {
        return switch (formaPagamento.trim().toLowerCase()) {
            case "pix" -> "Pix";
            case "cartão", "cartao" -> "Cartao";
            case "dinheiro" -> "Dinheiro";
            default -> throw new IllegalArgumentException("Forma de pagamento sem equivalente no banco: " + formaPagamento);
        };
    }
}
