package ar.edu.uade.rpg.modelo.combatientes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;

/**
 * La otra rama de Combatiente: lo que el jugador enfrenta.
 *
 * Un Enemigo no sube de nivel ni lleva equipo. Lo que lo define es que DECIDE
 * solo: elegirAccion() es el unico metodo abstracto y es donde vive la
 * personalidad de cada uno. El motor de combate nunca pregunta de que tipo es
 * un enemigo; le pide una Accion y la ejecuta. Esos 11 elegirAccion() distintos
 * son el polimorfismo del trabajo.
 *
 * Tampoco tienen el limite de 4 movimientos de los personajes ni niveles de
 * desbloqueo: el Esqueleto tiene uno solo y el SeñorDeLasSombras tres.
 */
public abstract class Enemigo extends Combatiente {

    /** Cuanto crece por etapa la vida, el mana y las defensas, en porcentaje. */
    public static final int CRECIMIENTO_AGUANTE = 14;

    /** Cuanto crecen por etapa el ataque fisico y el magico, en porcentaje. */
    public static final int CRECIMIENTO_ATAQUE = 10;

    private static final int PORCENTAJE_TOTAL = 100;

    protected final Random random;

    protected Enemigo(String nombre) {
        this(nombre, new Random());
    }

    /** Con Random inyectado, para que los tests no dependan de la suerte. */
    protected Enemigo(String nombre, Random random) {
        super(nombre);
        this.random = random;
        this.manaMaximo = 0;
        this.manaActual = 0;
        this.ataqueMagico = 0;
    }

    /**
     * Que hace este enemigo en su turno. Recibe a quienes puede atacar y a sus
     * companeros, porque algunos (el Acolito, el Guardian) actuan sobre los
     * suyos y no sobre la party.
     */
    public abstract Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                                        CalculadorDeDaño calculador);

    /** Lo pide ObjetivoAleatorio. Como el Random ya es atributo, sale gratis. */
    public Random getRandom() {
        return random;
    }

    protected List<Combatiente> filtrarVivos(List<Combatiente> candidatos) {
        List<Combatiente> vivos = new ArrayList<>();
        for (Combatiente candidato : candidatos) {
            if (candidato.estaVivo()) {
                vivos.add(candidato);
            }
        }
        return vivos;
    }

    /** Atajo para no repetir el new en los once elegirAccion(). */
    protected Accion usar(Movimiento movimiento, List<Combatiente> objetivos,
                          CalculadorDeDaño calculador) {
        return new AccionUsarMovimiento(this, movimiento, objetivos, calculador);
    }

    /**
     * Sube las estadisticas segun lo lejos que este la etapa. Son dos ritmos
     * distintos a proposito: la vida y las defensas crecen al 14% por etapa y
     * los ataques al 10%.
     *
     * El motivo es que la party tampoco crece pareja: de nivel 1 a 11 el ataque
     * del Guerrero se multiplica por 2.67 y su vida solo por 2. Si los enemigos
     * escalaran todo con un unico numero, o se vuelven de papel sobre el final o
     * pegan demasiado. Con dos ritmos las curvas quedan paralelas.
     *
     * La velocidad y la suerte NO escalan. Si escalaran, sobre el final los
     * enemigos actuarian siempre primero y criticarian la mitad de los golpes, y
     * la diferencia entre el Acechador y el Guardian dejaria de significar algo.
     *
     * Los jefes lo sobrescriben para no escalar: sus numeros ya son los finales.
     */
    public void escalarParaEtapa(int numeroEtapa) {
        int aguante = PORCENTAJE_TOTAL + CRECIMIENTO_AGUANTE * (numeroEtapa - 1);
        int ataque = PORCENTAJE_TOTAL + CRECIMIENTO_ATAQUE * (numeroEtapa - 1);

        vidaMaxima = escalar(vidaMaxima, aguante);
        vidaActual = vidaMaxima;
        manaMaximo = escalar(manaMaximo, aguante);
        manaActual = manaMaximo;
        defensaFisica = escalar(defensaFisica, aguante);
        defensaMagica = escalar(defensaMagica, aguante);
        ataqueFisico = escalar(ataqueFisico, ataque);
        ataqueMagico = escalar(ataqueMagico, ataque);
    }

    private static int escalar(int valor, int porcentaje) {
        return valor == 0 ? 0 : Math.max(1, valor * porcentaje / PORCENTAJE_TOTAL);
    }
}
