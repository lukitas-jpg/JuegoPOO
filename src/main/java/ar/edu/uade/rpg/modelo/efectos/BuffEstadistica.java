package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Sube una estadistica un porcentaje durante unos turnos. Lo usan Grito de
 * Guerra (ataque fisico), Bendicion (defensa fisica) y Sombra Agil (velocidad).
 *
 * No escribe las estadisticas del combatiente: las INFORMA por
 * getModificadorPorcentual(), y los getters efectivos de Combatiente suman
 * todos los porcentajes activos. Por eso al expirar no hay nada que revertir,
 * que es donde este tipo de efectos suele romperse.
 */
public class BuffEstadistica extends EfectoEstado {

    private final Estadistica estadistica;
    private final int porcentaje;

    public BuffEstadistica(Estadistica estadistica, int porcentaje, int duracionEnTurnos) {
        super("Mejora", duracionEnTurnos);
        this.estadistica = estadistica;
        this.porcentaje = porcentaje;
    }

    @Override
    public int getModificadorPorcentual(Estadistica estadistica) {
        return this.estadistica == estadistica ? porcentaje : 0;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
        // Nada: el efecto se nota solo con estar activo.
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        // Nada: no actua por turno, modifica mientras dura.
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // Nada que revertir: nunca se escribio la estadistica.
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new BuffEstadistica(estadistica, porcentaje, duracionEnTurnos);
    }

    public Estadistica getEstadistica() {
        return estadistica;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}
