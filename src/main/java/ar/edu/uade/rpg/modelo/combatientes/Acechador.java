package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoAleatorio;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

/**
 * El mas rapido: casi siempre abre la ronda. Pega tres veces por turno con
 * poco daño cada golpe y REELIGE objetivo en cada uno, asi que reparte el daño
 * por toda la party en vez de concentrarlo.
 *
 * Los tres golpes se resuelven con un solo movimiento de alcance OBJETIVO_UNICO
 * al que se le pasan tres objetivos: Ataque.aplicar() recorre la lista y
 * resuelve un golpe por cada uno. No hace falta ninguna clase nueva.
 */
public final class Acechador extends Enemigo implements ObjetivoAleatorio {

    private final int cantidadGolpes = 3;

    public Acechador() {
        super("Acechador");
        vidaMaxima = 55; vidaActual = 55;
        ataqueFisico = 13; defensaFisica = 7; defensaMagica = 6;
        velocidad = 20; suerte = 15;
        agregarMovimiento(new AtaqueFisico("Tajo Rapido", "Tres cortes en lo que dura un parpadeo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 5));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        List<Combatiente> vivos = filtrarVivos(party);
        List<Combatiente> objetivos = new java.util.ArrayList<>();
        for (int golpe = 0; golpe < cantidadGolpes; golpe++) {
            objetivos.add(elegirObjetivo(vivos));
        }
        return usar(getMovimientos().get(0), objetivos, calculador);
    }

    public int getCantidadGolpes() {
        return cantidadGolpes;
    }
}
