package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasPeligroso;
import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * MINIJEFE de la etapa 10. Fisico puro, mucha vida y un golpe que aturde.
 * Apunta siempre al que mas daño hace de la party.
 *
 * Tiene resistencia 50 a los efectos de estado, asi que un aturdimiento del 40%
 * le entra al 20%. No es un metodo esJefe() ni un instanceof: es un numero que
 * cualquier combatiente puede tener, y mañana se le puede poner 25 o 70 sin
 * tocar una linea de HabilidadDeEstado.
 *
 * La Mirada Petrificante esta marcada como definitiva, no porque lo sea, sino
 * para quedarse con su cooldown de 4 turnos sin inventar un contador aparte.
 */
public final class Verdugo extends Enemigo implements ObjetivoMasPeligroso {

    public static final int RESISTENCIA_DE_JEFE = 50;

    public Verdugo() {
        super("Verdugo");
        vidaMaxima = 380; vidaActual = 380;
        ataqueFisico = 50; defensaFisica = 38; defensaMagica = 26;
        velocidad = 24; suerte = 14;
        resistenciaAEstados = RESISTENCIA_DE_JEFE;
        agregarMovimiento(new AtaqueFisico("Hachazo Brutal", "Un hachazo que parte el aire.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 15));
        agregarMovimiento(new HabilidadDeEstado("Mirada Petrificante",
                "Clava la mirada y le saca el turno al objetivo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, true, new Aturdimiento(1), 50));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        Combatiente objetivo = elegirObjetivo(filtrarVivos(party));
        int movimiento = getMovimientos().get(1).estaEnCooldown() ? 0 : 1;
        return usar(getMovimientos().get(movimiento), List.of(objetivo), calculador);
    }

    /** Los jefes no escalan: estos numeros ya son los de su etapa. */
    @Override
    public void escalarParaEtapa(int numeroEtapa) {
        // a proposito, no hace nada
    }
}
