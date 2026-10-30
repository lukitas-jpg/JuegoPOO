package ar.edu.uade.rpg.modelo.tienda;

import java.util.List;
import java.util.Random;

public class MaquinaDeSlots {
    public static final int APUESTA = 50;
    private static final int CANTIDAD_DE_RODILLOS = 3;

    private final Random random;
    private boolean quedaTiradaGratis;
    private boolean abandonada;

    public MaquinaDeSlots() {
        this(new Random());
    }

    public MaquinaDeSlots(Random random) {
        this.random = random;
        this.quedaTiradaGratis = true;
        this.abandonada = false;
    }

    public int getCostoDeLaProximaTirada() {
        return quedaTiradaGratis ? 0 : APUESTA;
    }

    public boolean puedeTirar(int oroDisponible) {
        return !abandonada && oroDisponible >= getCostoDeLaProximaTirada();
    }

    public ResultadoTirada tirar() {
        boolean gratis = quedaTiradaGratis;
        quedaTiradaGratis = false;

        Simbolo[] valores = Simbolo.values();
        List<Simbolo> salieron = List.of(
                valores[random.nextInt(valores.length)],
                valores[random.nextInt(valores.length)],
                valores[random.nextInt(valores.length)]);

        int multiplicador = calcularMultiplicador(salieron);
        return new ResultadoTirada(salieron, multiplicador, multiplicador * APUESTA, gratis);
    }

    public static int calcularMultiplicador(List<Simbolo> salieron) {
        Simbolo a = salieron.get(0);
        Simbolo b = salieron.get(1);
        Simbolo c = salieron.get(2);

        if (a == b && b == c) {
            return switch (a) {
                case LIMON -> 0;
                case DIAMANTE -> 15;
                default -> 10;
            };
        }

        Simbolo repetido = null;
        if (a == b || a == c) {
            repetido = a;
        } else if (b == c) {
            repetido = b;
        }
        if (repetido == null) {
            return 0;
        }
        return switch (repetido) {
            case DIAMANTE -> 4;
            case MONEDA, RUBI -> 2;
            case CEREZA, TREBOL -> 1;
            default -> 0;
        };
    }

    public void abandonar() {
        abandonada = true;
    }

    public boolean estaAbandonada() {
        return abandonada;
    }

    public boolean quedaTiradaGratis() {
        return quedaTiradaGratis;
    }

    public int getCantidadDeRodillos() {
        return CANTIDAD_DE_RODILLOS;
    }
}
