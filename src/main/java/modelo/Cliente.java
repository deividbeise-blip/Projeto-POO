package modelo;
import dao.ClienteDAO;

public class Cliente {
    private Long id_cliente;
    private String name_cliente;
    private String cpf_cliente;
    private String email_cliente;
    private String phone_cliente;

    public Cliente(String name_cliente, String cpf_cliente, String email_cliente, String phone_cliente) {
        this.name_cliente = name_cliente;
        this.cpf_cliente = cpf_cliente;
        this.email_cliente = email_cliente;
        this.phone_cliente = phone_cliente;
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

    public String getCpf_cliente() {
        return cpf_cliente;
    }

    public void setCpf_cliente(String cpf_cliente) {
        this.cpf_cliente = cpf_cliente;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "cpf_cliente", cpf_cliente);
    }

    public String getEmail_cliente() {
        return email_cliente;
    }

    public void setEmail_cliente(String email_cliente) {
        this.email_cliente = email_cliente;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "email_cliente", email_cliente);
    }

    public String getPhone_cliente() {
        return phone_cliente;
    }

    public void setPhone_cliente(String phone_cliente) {
        this.phone_cliente = phone_cliente;
        ClienteDAO clienteDAO = new ClienteDAO();
        clienteDAO.atualizar(this, "phone_cliente", phone_cliente);
    }
}
