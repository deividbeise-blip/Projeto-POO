package modelo;

public class SemDesconto implements PoliticaDesconto {
    @Override
    public double calcular(double valorBase) {
        return valorBase;
    }
}
