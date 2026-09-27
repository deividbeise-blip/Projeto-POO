package modelo;
import dao.VendedorDAO;

public class Vendedor {
    private Long id_vendedor;
    private String name;
    private String cpf;
    private Double comissaoPercentual;
    private Concessionaria concessionaria;

    // Construtor de CADASTRO (sem id, banco gera) — chama salvar()
    public Vendedor(String name, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this.name = name;
        this.cpf = cpf;
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
        try {
            VendedorDAO vendedorDAO = new VendedorDAO();
            vendedorDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar vendedor no banco. Banco deve estar offline", e);
        }
    }

    // Construtor DE CARGA (com id, vindo do banco) — NÃO chama salvar()
    public Vendedor(Long id_vendedor, String name, String cpf, Double comissaoPercentual, Concessionaria concessionaria) {
        this.id_vendedor = id_vendedor;
        this.name = name;
        this.cpf = cpf;
        this.comissaoPercentual = comissaoPercentual;
        this.concessionaria = concessionaria;
    }

    public Long getId_vendedor() {
        return id_vendedor;
    }
    public void setId_vendedor(Long id_vendedor) {
        this.id_vendedor = id_vendedor;
        VendedorDAO vendedorDAO = new VendedorDAO();
        vendedorDAO.atualizar(this, "id_vendedor", String.valueOf(id_vendedor));
    }
    public String getName() {
        return name;
    }
    public String getCpf() {
        return cpf;
    }
    public Double getComissaoPercentual() {
        return comissaoPercentual;
    }
    public void setName(String name) {
        this.name = name;
        VendedorDAO vendedorDAO = new VendedorDAO();
        vendedorDAO.atualizar(this, "name_vendedor", name);
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
        VendedorDAO vendedorDAO = new VendedorDAO();
        vendedorDAO.atualizar(this, "cpf_vendedor", cpf);
    }
    public void setComissaoPercentual(Double comissaoPercentual) {
        this.comissaoPercentual = comissaoPercentual;
        VendedorDAO vendedorDAO = new VendedorDAO();
        vendedorDAO.atualizar(this, "comissao_percentual", String.valueOf(comissaoPercentual));
    }

    public double caucularcomissao(double valorVenda, Veiculo veiculo) {
        double percentual = comissaoPercentual;
        double reducaoComissaoMoto = 3;

        if (veiculo.isMoto() == false) {
            return valorVenda * percentual / 100;
        } else {
            percentual = percentual - reducaoComissaoMoto;
            return valorVenda * percentual / 100;
        }
    }

    public boolean validarVendedor() {
        return name != null && cpf != null && comissaoPercentual > 0;
    }

    public Concessionaria getConcessionaria() {
        return concessionaria;
    }

    public void setConcessionaria(Concessionaria concessionaria) {
        this.concessionaria = concessionaria;
    }

    @Override
    public String toString() {
        return name + " - " + cpf;
    }
}