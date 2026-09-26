package modelo;
import dao.ClienteDAO;

public class Cliente {
    private Long id_cliente;
    private String name_cliente;
    private String cpf;
    private String email;
    private String phone;

    public Cliente(String name_cliente, String cpf, String email, String phone) {
        this.name_cliente = name_cliente;
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

    public Long getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Long id) {
        this.id_cliente = id;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "id_cliente", String.valueOf(id));
    }

    public String getName_cliente() {
        return name_cliente;
    }

    public void setName_cliente(String name_cliente) {
        this.name_cliente = name_cliente;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "name_cliente", name_cliente);

    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "cpf", cpf);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "email", email);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "phone", phone);
    }
}
