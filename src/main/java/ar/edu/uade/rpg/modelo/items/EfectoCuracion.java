package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

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
