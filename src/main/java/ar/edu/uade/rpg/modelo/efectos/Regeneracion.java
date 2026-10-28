package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Cura una cantidad fija al final de cada turno del afectado. Es el
 * espejo del Veneno. Ningun movimiento de las seis clases lo aplica: queda
 * para los consumibles de la tienda.
 */
public class Regeneracion extends EfectoEstado {

    private final int curacionPorTurno;

    public Regeneracion(int curacionPorTurno, int duracionEnTurnos) {
        super("Regeneracion", duracionEnTurnos);
        this.curacionPorTurno = curacionPorTurno;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
        // Cura recien en el turno siguiente.
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        objetivo.curar(curacionPorTurno);
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // Se va sin dejar rastro.
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new Regeneracion(curacionPorTurno, duracionEnTurnos);
    }
}
