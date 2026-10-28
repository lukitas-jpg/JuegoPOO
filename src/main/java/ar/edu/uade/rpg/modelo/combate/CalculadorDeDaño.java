package ar.edu.uade.rpg.modelo.combate;

import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

/**
 * Toda la formula de daño del juego, en un solo lugar.
 *
 * Es una Pure Fabrication: no existe un "calculador de daño" en el mundo del
 * juego. Existe porque si la formula viviera en Ataque, en Combatiente o en
 * Combate, cada cambio de balance habria que buscarlo en varias clases y
 * tocarlo en todas. Aca se cambia una constante y cambia el juego entero.
 *
 * Los cinco pasos, en este orden:
 *   1. base       = potencia x ataque efectivo / 10
 *   2. defensa    = base x 50 / (50 + defensa efectiva)
 *   3. defender   = x 50% si el objetivo esta defendiendo
 *   4. variacion  = x un valor al azar entre 90% y 110%
 *   5. critico    = x 150% si la tirada de suerte sale
 * Despues de todo eso, el daño nunca baja de 1.
 *
 * El escudo NO esta aca: absorbe mas tarde, dentro de recibirDaño(), porque es
 * un efecto de estado del objetivo y no parte de la formula.
 *
 * El paso 2 usa una curva y no una resta. Con resta, una defensa alta dejaria
 * el daño en cero y el juego se trabaria; con la curva, cada punto de defensa
 * vale un poco menos que el anterior y el daño siempre pasa algo.
 */
public class CalculadorDeDaño {

    /** Convierte la potencia del movimiento a la escala de las estadisticas. */
    public static final int DIVISOR_DE_POTENCIA = 10;

    /** Cuanta defensa hace falta para reducir el daño a la mitad. */
    public static final int CONSTANTE_DE_DEFENSA = 50;

    public static final int VARIACION_MINIMA = 90;
    public static final int VARIACION_MAXIMA = 110;

    /** Por mas suerte que tenga, nadie critica mas de una vez cada dos golpes. */
    public static final int PROBABILIDAD_CRITICA_MAXIMA = 50;

    public static final int MULTIPLICADOR_CRITICO = 150;

    /** Ningun golpe hace cero: siempre entra algo. */
    public static final int GOLPE_MINIMO = 1;

    private static final int PORCENTAJE_TOTAL = 100;

    private final Random random;

    public CalculadorDeDaño() {
        this(new Random());
    }

    /**
     * Permite inyectar un Random con semilla fija desde los tests, para que un
     * mismo golpe de siempre el mismo numero y las pruebas no fallen una vez
     * cada tantas corridas.
     */
    public CalculadorDeDaño(Random random) {
        this.random = random;
    }

    public ResultadoDaño calcular(Combatiente atacante, Combatiente objetivo,
                                  int potencia, TipoDaño tipo) {
        int daño = potencia * getAtaqueSegunTipo(atacante, tipo) / DIVISOR_DE_POTENCIA;
        daño = aplicarDefensa(daño, objetivo, tipo);
        daño = daño * objetivo.getPorcentajeDeDañoRecibido() / PORCENTAJE_TOTAL;
        daño = aplicarVariacion(daño);

        boolean critico = esCritico(atacante);
        if (critico) {
            daño = daño * MULTIPLICADOR_CRITICO / PORCENTAJE_TOTAL;
        }
        return new ResultadoDaño(Math.max(daño, GOLPE_MINIMO), critico);
    }

    private int getAtaqueSegunTipo(Combatiente atacante, TipoDaño tipo) {
        return tipo == TipoDaño.FISICO
                ? atacante.getAtaqueFisicoEfectivo()
                : atacante.getAtaqueMagicoEfectivo();
    }

    private int aplicarDefensa(int daño, Combatiente objetivo, TipoDaño tipo) {
        int defensa = tipo == TipoDaño.FISICO
                ? objetivo.getDefensaFisicaEfectiva()
                : objetivo.getDefensaMagicaEfectiva();
        return daño * CONSTANTE_DE_DEFENSA / (CONSTANTE_DE_DEFENSA + defensa);
    }

    /**
     * Mueve el resultado entre el 90% y el 110%. Esta para que dos golpes
     * iguales no den siempre el mismo numero, que es lo que hace que una pelea
     * se sienta viva y no una planilla.
     */
    private int aplicarVariacion(int daño) {
        int porcentaje = VARIACION_MINIMA
                + random.nextInt(VARIACION_MAXIMA - VARIACION_MINIMA + 1);
        return daño * porcentaje / PORCENTAJE_TOTAL;
    }

    /**
     * La probabilidad de critico ES la suerte efectiva, leida como porcentaje:
     * el Asesino con 18 de suerte critica el 18% de las veces. Por eso la
     * suerte no necesita formula propia ni otra estadistica que la acompañe.
     */
    private boolean esCritico(Combatiente atacante) {
        int probabilidad = Math.min(atacante.getSuerteEfectiva(), PROBABILIDAD_CRITICA_MAXIMA);
        return random.nextInt(PORCENTAJE_TOTAL) < probabilidad;
    }
}
