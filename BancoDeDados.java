import java.io.*;
public class BancoDeDados {
    public static void main(String[] args) {
        Teste cliente = new Teste("João", 25);
        try {
            FileOutputStream fos = new FileOutputStream("teste.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(cliente);
            oos.close();
        } catch (Exception e) {
            System.out.println("Erro");
        }
        try {
            FileInputStream fis = new FileInputStream("teste.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Teste clienteLido = (Teste) ois.readObject();
            ois.close();
            System.out.println("Nome: " + clienteLido.name);
            System.out.println("Idade: " + clienteLido.age);
        } catch (Exception e) {
            System.out.println("Erro");
        }
    }
    public static class Teste implements Serializable {
        private String name;
        private int age;
        public Teste(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
