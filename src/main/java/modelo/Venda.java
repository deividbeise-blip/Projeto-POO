package modelo;
import java.time.LocalDate;

import dao.VendaDAO;

public class Venda {
    private Long id_venda;
    private Cliente cliente;
    private Vendedor vendedor;
    private Veiculo veiculo;
    private LocalDate dataVenda;
    private Double valorFinal;
    private Pagamento pagamento;

    public Venda(Cliente cliente, Vendedor vendedor, Veiculo veiculo, LocalDate dataVenda, Double valorFinal, Pagamento pagamento) {
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.veiculo = veiculo;
        this.dataVenda = dataVenda;
        this.valorFinal = valorFinal;
        this.pagamento = pagamento;
        try {
            VendaDAO vendaDAO = new VendaDAO();
            vendaDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar venda no banco. Banco deve estar offline", e);
        }
    }
    public Pagamento getPagamento() {
        return pagamento;
    }
    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
        VendaDAO vendaDAO = new VendaDAO();
        vendaDAO.atualizar(this, "pagamento_venda", String.valueOf(pagamento.getId_pagamento()));
    }
    // obtem o id da venda
    public Long getId_venda() {
        return id_venda;
    }
    public void setId_venda(Long id_venda) {
        this.id_venda = id_venda;
        VendaDAO vendaDAO = new VendaDAO();
        vendaDAO.atualizar(this, "id_venda", String.valueOf(id_venda));
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Vendedor getVendedor() {
        return vendedor;
    }
    public Veiculo getVeiculo() {
        return veiculo;
    }
    public LocalDate getDataVenda() {
        return dataVenda;
    }
    public double getValorFinal() {
        return valorFinal;
    }

    // obtem a comissão do funcionário de acordo com o valor da venda
    public double getcomissao(){
        return vendedor.caucularcomissao(valorFinal, veiculo);
    }

    // modifica a variável para um valor com desconto
    public void  desconto(double percentual){
        valorFinal = valorFinal -  valorFinal * percentual/100;
    }

    //validação da venda
    public boolean validarVenda(){
        return cliente != null
                && veiculo != null
                && vendedor != null
                && dataVenda != null
                && valorFinal > 0
                && veiculo.verificarDisponibilidade()
                && vendedor.validarVendedor();
    }

    // venda de veiculo
    public void finalizarVenda () throws excecao.VeiculoIndisponivelException {
        validarVenda();
        veiculo.vender();
    }
}

