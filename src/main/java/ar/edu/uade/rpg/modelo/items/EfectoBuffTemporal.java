package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Sube un porcentaje el ataque durante unos turnos, reutilizando el mismo
 * BuffEstadistica que usan Grito de Guerra y Bendicion.
 *
 * Mejora el ataque mas alto del personaje, fisico o magico. Asi un solo objeto
 * sirve para los seis: la Piedra de Afilar le sube el fisico al Guerrero y el
 * magico al Mago, sin preguntar de que clase es ninguno.
 */
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
