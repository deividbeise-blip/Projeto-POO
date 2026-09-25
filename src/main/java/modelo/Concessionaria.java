package modelo;
import dao.ConcessionariaDAO;

public class Concessionaria {
    private Long id_concessionaria;
    private String name;
    private String address;

    public Concessionaria(String name, String address) {
        this.name = name;
        this.address = address;
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
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
