package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class Regeneracion extends EfectoEstado {
    private final int curacionPorTurno;

    public Regeneracion(int curacionPorTurno, int duracionEnTurnos) {
        super("Regeneracion", duracionEnTurnos);
        this.curacionPorTurno = curacionPorTurno;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        objetivo.curar(curacionPorTurno);
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
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
