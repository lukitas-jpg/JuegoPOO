package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Baja una estadistica un porcentaje durante unos turnos. El porcentaje se
 * guarda en positivo y se devuelve en negativo, asi en el constructor se lee
 * "25" y no "-25", que es mas facil de contrastar contra la tabla de balance.
 * Lo usa Rugido Intimidante.
 *
 * No escribe las estadisticas del combatiente: las INFORMA por
 * getModificadorPorcentual(), y los getters efectivos de Combatiente suman
 * todos los porcentajes activos. Por eso al expirar no hay nada que revertir,
 * que es donde este tipo de efectos suele romperse.
 */
public class DebuffEstadistica extends EfectoEstado {

    private final Estadistica estadistica;
    private final int porcentaje;

    public DebuffEstadistica(Estadistica estadistica, int porcentaje, int duracionEnTurnos) {
        super("Debilitamiento", duracionEnTurnos);
        this.estadistica = estadistica;
        this.porcentaje = porcentaje;
    }

    @Override
    public int getModificadorPorcentual(Estadistica estadistica) {
        return this.estadistica == estadistica ? -porcentaje : 0;
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
        return new DebuffEstadistica(estadistica, porcentaje, duracionEnTurnos);
    }

    public Estadistica getEstadistica() {
        return estadistica;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}
