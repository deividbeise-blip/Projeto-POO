package modelo;

public class DescontoPix implements PoliticaDesconto {
    private static final double PERCENTUAL = 5.0;

    @Override
    public double calcular(double valorBase) {
        return valorBase - valorBase * PERCENTUAL / 100;
    }
}
