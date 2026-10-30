package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class EfectoRestaurarMana implements EfectoConsumible {
    private static final int PORCENTAJE_TOTAL = 100;

    private final int porcentajeDeMana;

    public EfectoRestaurarMana(int porcentajeDeMana) {
        this.porcentajeDeMana = porcentajeDeMana;
    }

    @Override
    public void aplicar(Combatiente objetivo) {
        objetivo.recuperarMana(objetivo.getManaMaximo() * porcentajeDeMana / PORCENTAJE_TOTAL);
    }

    @Override
    public boolean puedeAplicarseA(Combatiente objetivo) {
        return objetivo.estaVivo();
    }

    @Override
    public String getDescripcion() {
        return "Restaura el " + porcentajeDeMana + "% del mana maximo.";
    }
}
