package dominio.politica;

public class DescontoPix implements PoliticaDesconto {
    public static final double PERCENTUAL = 5.0;

    @Override
    public double calcular(double valorBase) {
        return valorBase - valorBase * PERCENTUAL / 100;
    }
}
