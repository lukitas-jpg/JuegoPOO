package ar.edu.uade.rpg.modelo.items;

import java.io.Serializable;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Lo que hace un consumible al usarse. Es una interfaz y no un enum con un
 * switch adentro: agregar un consumible nuevo es agregar una clase, sin tocar
 * ninguna de las que ya existen.
 *
 * puedeAplicarseA() existe porque las reglas son opuestas segun el efecto: el
 * Polvo de Renacer solo sirve sobre un caido y la Pocion de Vida solo sobre
 * alguien vivo. Sin este metodo, la pantalla tendria que preguntar de que tipo
 * es el item para saber a quien ofrecerselo.
 */
public interface EfectoConsumible extends Serializable {

    void aplicar(Combatiente objetivo);

    boolean puedeAplicarseA(Combatiente objetivo);

    String getDescripcion();
}
