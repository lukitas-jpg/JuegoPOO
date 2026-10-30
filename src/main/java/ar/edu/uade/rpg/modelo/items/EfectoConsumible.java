package ar.edu.uade.rpg.modelo.items;

import java.io.Serializable;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public interface EfectoConsumible extends Serializable {
    void aplicar(Combatiente objetivo);

    boolean puedeAplicarseA(Combatiente objetivo);

    String getDescripcion();
}
