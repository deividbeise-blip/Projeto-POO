package infra.dao;

import java.sql.Date;
import java.sql.Types;
import java.util.List;

import dominio.modelo.ResumoVenda;
import dominio.modelo.Venda;
import dominio.repositorio.VendaRepository;
import infra.conexao.ConnectionFactory;

/** Acesso à tabela venda: grava a venda, liga o pagamento e monta o histórico. */
public class VendaDAO implements VendaRepository {

    private final ExecutorSql executor;

    /** @param connectionFactory fábrica de conexões (a mesma da transação em andamento) */
    public VendaDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

    /**
     * Faz o INSERT com pagamento_venda NULL (o pagamento ainda não existe) e coloca o ID na venda.
     *
     * @param venda venda nova, com cliente, veículo e vendedor já gravados
     * @throws infra.excecao.PersistenciaException se o banco recusar a gravação
     */
    @Override
    public void salvar(Venda venda) {
        long id = executor.inserir(
                """
                INSERT INTO venda
                (data_venda, cliente_venda, veiculo_venda, vendedor_venda, pagamento_venda)
                VALUES (?, ?, ?, ?, ?)
                """,
                comando -> {
                    comando.setDate(1, Date.valueOf(venda.getDataVenda()));
                    comando.setLong(2, venda.getCliente().getIdCliente());
                    comando.setLong(3, venda.getVeiculo().getIdVeiculo());
                    comando.setLong(4, venda.getVendedor().getIdVendedor());
                    // O pagamento ainda não existe neste momento; é preenchido em atualizarPagamento().
                    comando.setNull(5, Types.BIGINT);
                },
                "Erro ao salvar venda no banco.");
        venda.setIdVenda(id);
    }

    /**
     * Liga a venda ao pagamento depois que o pagamento já foi gravado.
     *
     * @param venda venda com ID e com pagamento já gravado
     * @throws infra.excecao.PersistenciaException se a atualização falhar
     */
    @Override
    public void atualizarPagamento(Venda venda) {
        executor.atualizar(
                "UPDATE venda SET pagamento_venda = ? WHERE id_venda = ?",
                comando -> {
                    comando.setLong(1, venda.getPagamento().getIdPagamento());
                    comando.setLong(2, venda.getIdVenda());
                },
                "Erro ao atualizar pagamento da venda no banco.");
    }

    /**
     * Uma consulta com JOIN em cliente, veículo, vendedor e pagamento, da venda mais recente
     * para a mais antiga. A forma de pagamento já sai com o nome de exibição ("PIX", "Cartão", "Dinheiro").
     *
     * @return as vendas com pagamento ligado; lista vazia se não houver nenhuma
     * @throws infra.excecao.PersistenciaException se a consulta falhar
     */
    @Override
    public List<ResumoVenda> listarHistorico() {
        return executor.consultar(
                """
                SELECT v.data_venda, c.name_cliente, ve.marca_veiculo, ve.modelo_veiculo,
                       vend.name_vendedor, p.forma_pagamento, p.valor_pagamento
                FROM venda v
                JOIN cliente c ON c.id_cliente = v.cliente_venda
                JOIN veiculo ve ON ve.id_veiculo = v.veiculo_venda
                JOIN vendedor vend ON vend.id_vendedor = v.vendedor_venda
                JOIN pagamento p ON p.id_pagamento = v.pagamento_venda
                ORDER BY v.data_venda DESC, v.id_venda DESC
                """,
                resultado -> new ResumoVenda(
                        resultado.getDate("data_venda").toLocalDate(),
                        resultado.getString("name_cliente"),
                        resultado.getString("marca_veiculo") + " " + resultado.getString("modelo_veiculo"),
                        resultado.getString("name_vendedor"),
                        paraNomeExibicao(resultado.getString("forma_pagamento")),
                        resultado.getBigDecimal("valor_pagamento")),
                "Erro ao listar histórico de vendas.");
    }

    /**
     * A coluna forma_pagamento é ENUM('Dinheiro','Cartao','Pix'); o domínio e a tela usam
     * "PIX", "Cartão" e "Dinheiro". Faz o caminho inverso do PagamentoDAO.
     */
    private static String paraNomeExibicao(String valorColuna) {
        return switch (valorColuna) {
            case "Pix" -> "PIX";
            case "Cartao" -> "Cartão";
            case "Dinheiro" -> "Dinheiro";
            default -> valorColuna;
        };
    }
}
