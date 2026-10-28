package ar.edu.uade.rpg.modelo.tienda;

import java.util.List;
import java.util.Random;

/**
 * Tres rodillos, seis simbolos, sorteo uniforme e independiente: 216
 * resultados posibles.
 *
 * Aparece solo en las etapas 9 y 19, antes de la tienda, y se abandona al
 * pasar. La primera tirada de cada maquina es gratis.
 *
 * La SUERTE de los personajes NO interviene. Es azar puro, y esa es una
 * decision de diseño: si la suerte influyera, el Asesino convertiria la maquina
 * en una fuente de oro y dejaria de ser una apuesta.
 *
 * Retorno: 205/216 = 94.9%. La casa se queda con el 5%, pero igual arruina la
 * mitad de las partidas de quien no sabe parar: no es la ventaja de la casa lo
 * que funde, es la varianza.
 */
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

    /** Cuanto cuesta la proxima tirada: 0 si todavia queda la gratis. */
    public int getCostoDeLaProximaTirada() {
        return quedaTiradaGratis ? 0 : APUESTA;
    }

    public boolean puedeTirar(int oroDisponible) {
        return !abandonada && oroDisponible >= getCostoDeLaProximaTirada();
    }

    /**
     * Sortea los tres rodillos. No descuenta ni acredita oro: devuelve cuanto
     * pago y quien lleva la cuenta hace la resta. La maquina no conoce la
     * partida.
     */
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

    /**
     * La tabla de premios entera, en un solo lugar y sin ninguna estructura de
     * datos: tres iguales miran que simbolo es, y si no, se busca si hay
     * exactamente dos.
     */
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

    /** El jugador decide pasar a la tienda. No puede volver. */
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
