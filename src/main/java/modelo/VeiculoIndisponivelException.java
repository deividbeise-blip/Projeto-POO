package modelo;

public class VeiculoIndisponivelException extends Exception {
    
    
    public VeiculoIndisponivelException() {
        super("O veículo selecionado não está disponível no estoque.");
    }

   
    public VeiculoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
