package infra.dao;

import java.sql.Date;

import dominio.modelo.Pagamento;
import dominio.modelo.Venda;
import dominio.repositorio.PagamentoRepository;
import infra.conexao.ConnectionFactory;

/**
 * Grava a parte comum de qualquer pagamento na tabela pagamento,
 * convertendo o nome da forma para o ENUM do banco.
 */
public class PagamentoDAO implements PagamentoRepository {

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public PagamentoDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT e coloca no objeto o ID gerado. Cliente e veículo vêm da venda do pagamento.
     *
     * @param pagamento pagamento novo, ainda sem ID, ligado a uma venda já gravada
     * @throws IllegalArgumentException se a forma de pagamento não existir no ENUM do banco
     * @throws infra.excecao.PersistenciaException se o banco recusar a gravação
     */
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
