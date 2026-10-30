package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

public class Proteccion extends EfectoEstado {
    private static final int PORCENTAJE_TOTAL = 100;

    private final int reduccion;

    public Proteccion(int reduccion, int duracionEnTurnos) {
        super("Proteccion", duracionEnTurnos);
        this.reduccion = reduccion;
    }

    @Override
    public int absorber(int daño, TipoDaño tipo) {
        return daño * (PORCENTAJE_TOTAL - reduccion) / PORCENTAJE_TOTAL;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
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
        return new Proteccion(reduccion, duracionEnTurnos);
    }

    public int getReduccion() {
        return reduccion;
    }
}
