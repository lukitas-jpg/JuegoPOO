package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

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
        return new BuffEstadistica(estadistica, porcentaje, duracionEnTurnos);
    }

    public Estadistica getEstadistica() {
        return estadistica;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}
