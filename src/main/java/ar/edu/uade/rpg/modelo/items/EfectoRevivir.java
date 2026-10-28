package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Levanta a un caido con un porcentaje de su vida. Es la unica forma de
 * deshacer una muerte, y por eso el objeto que lo lleva es el mas caro del
 * catalogo.
 *
 * Es el espejo exacto de EfectoCuracion: aquel solo funciona sobre alguien
 * vivo y este solo sobre alguien muerto. Las dos reglas viven en
 * puedeAplicarseA() y en ningun lado mas.
 */
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
