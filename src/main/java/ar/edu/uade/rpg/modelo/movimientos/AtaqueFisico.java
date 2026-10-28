package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

/**
 * Ataque que usa el ataque fisico del atacante contra la defensa fisica del
 * objetivo. Son los del Guerrero, el Arquero, el Asesino y el Tanque.
 *
 * La clase no agrega comportamiento, solo fija el tipo de daño. Existe para
 * que en el constructor de cada personaje se lea 'new AtaqueFisico(...)' y se
 * entienda de un vistazo contra que defensa va, sin tener que rastrear un
 * parametro enum ocho lineas mas abajo.
 */
public class AtaqueFisico extends Ataque {

    public AtaqueFisico(String nombre, String descripcion, int costoMana,
            AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo, int potencia) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo,
              potencia, TipoDaño.FISICO);
    }
}
