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
    }
