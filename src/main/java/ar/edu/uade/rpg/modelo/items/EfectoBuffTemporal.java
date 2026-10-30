package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public class EfectoBuffTemporal implements EfectoConsumible {
    private final int porcentaje;
    private final int duracionEnTurnos;

    public EfectoBuffTemporal(int porcentaje, int duracionEnTurnos) {
        this.porcentaje = porcentaje;
        this.duracionEnTurnos = duracionEnTurnos;
    }

    @Override
    public void aplicar(Combatiente objetivo) {
        Estadistica cual = objetivo.getAtaqueMagicoEfectivo() > objetivo.getAtaqueFisicoEfectivo()
                ? Estadistica.ATAQUE_MAGICO
                : Estadistica.ATAQUE_FISICO;
        objetivo.aplicarEfecto(new BuffEstadistica(cual, porcentaje, duracionEnTurnos));
    }

    @Override
    public boolean puedeAplicarseA(Combatiente objetivo) {
        return objetivo.estaVivo();
    }

    @Override
    public String getDescripcion() {
        return "Sube un " + porcentaje + "% el ataque durante "
                + duracionEnTurnos + " turnos.";
    }
}
