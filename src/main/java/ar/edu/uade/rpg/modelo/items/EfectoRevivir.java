package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class EfectoRevivir implements EfectoConsumible {
    private final int porcentajeDeVida;

    public EfectoRevivir(int porcentajeDeVida) {
        this.porcentajeDeVida = porcentajeDeVida;
    }

    @Override
    public void aplicar(Combatiente objetivo) {
        objetivo.revivir(porcentajeDeVida);
    }

    @Override
    public boolean puedeAplicarseA(Combatiente objetivo) {
        return !objetivo.estaVivo();
    }

    @Override
    public String getDescripcion() {
        return "Revive a un caido con el " + porcentajeDeVida + "% de su vida.";
    }
}
