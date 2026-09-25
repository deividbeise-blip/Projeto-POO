package modelo;
import dao.ClienteDAO;

public class Cliente {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String phone;

    public Cliente(String name, String cpf, String email, String phone) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        try {
            ClienteDAO clienteDAO = new ClienteDAO();
            clienteDAO.salvar(this);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar cliente no banco. Banco deve estar offline", e);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
