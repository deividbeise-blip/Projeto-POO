package modelo;
import dao.ConcessionariaDAO;

public class Concessionaria {
    private Long id_concessionaria;
    private String name_concessionaria;
    private String address_concessionaria;

    public Concessionaria(String name_concessionaria, String address_concessionaria) {
        this.name_concessionaria = name_concessionaria;
        this.address_concessionaria = address_concessionaria;
        try {
            ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();
            concessionariaDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar concessionária no banco. Banco deve estar offline", e);
        }
    }

    public Long getId_concessionaria() {
        return id_concessionaria;
    }

    public void setId_concessionaria(Long id_concessionaria) {
        this.id_concessionaria = id_concessionaria;
        ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();
        concessionariaDAO.atualizar(this, "id_concessionaria", String.valueOf(id_concessionaria));
    }

    public String getName_concessionaria() {
        return name_concessionaria;
    }

    public void setName_concessionaria(String name_concessionaria) {
        this.name_concessionaria = name_concessionaria;
        ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();
        concessionariaDAO.atualizar(this, "name_concessionaria", name_concessionaria);
    }

    public String getAddress_concessionaria() {
        return address_concessionaria;
    }

    public void setAddress_concessionaria(String address_concessionaria) {
        this.address_concessionaria = address_concessionaria;
        ConcessionariaDAO concessionariaDAO = new ConcessionariaDAO();
        concessionariaDAO.atualizar(this, "address_concessionaria", address_concessionaria);
    }
}
