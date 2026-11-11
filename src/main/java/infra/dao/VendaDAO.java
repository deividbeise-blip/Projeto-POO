package infra.dao;

import java.sql.Date;
import java.sql.Types;

import dominio.modelo.Venda;
import dominio.repositorio.VendaRepository;
import infra.conexao.ConnectionFactory;

public class VendaDAO implements VendaRepository {

    private final ExecutorSql executor;

    public VendaDAO(ConnectionFactory connectionFactory) {
        this.executor = new ExecutorSql(connectionFactory);
    }

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

    /** Liga a venda ao pagamento depois que o pagamento já foi gravado. */
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
}
