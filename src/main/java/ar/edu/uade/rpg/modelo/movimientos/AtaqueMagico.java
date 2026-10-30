package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

public class AtaqueMagico extends Ataque {
    public AtaqueMagico(String nombre, String descripcion, int costoMana,
            AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo, int potencia) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo,
              potencia, TipoDaño.MAGICO);
    }
}
