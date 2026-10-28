package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Cura un PORCENTAJE de la vida maxima, no una cantidad fija.
 *
 * Esa decision es la que mantiene la pocion vigente toda la partida: con un
 * valor fijo de 60 curaria la mitad de la vida de un Guerrero de nivel 1 y
 * apenas una cuarta parte de la de nivel 11.
 */
public class EfectoCuracion implements EfectoConsumible {

    private static final int PORCENTAJE_TOTAL = 100;

    private final int porcentajeDeVida;

    public EfectoCuracion(int porcentajeDeVida) {
        this.porcentajeDeVida = porcentajeDeVida;
    }

    @Override
    public void aplicar(Combatiente objetivo) {
        objetivo.curar(objetivo.getVidaMaxima() * porcentajeDeVida / PORCENTAJE_TOTAL);
    }

    @Override
    public boolean puedeAplicarseA(Combatiente objetivo) {
        return objetivo.estaVivo();
    }

    @Override
    public String getDescripcion() {
        return "Cura el " + porcentajeDeVida + "% de la vida maxima.";
    }
}
