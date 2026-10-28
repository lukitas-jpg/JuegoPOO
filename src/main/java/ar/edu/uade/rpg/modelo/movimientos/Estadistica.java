package ar.edu.uade.rpg.modelo.movimientos;

/**
 * Las ocho estadisticas que describen a cualquier Combatiente.
 *
 * Existe para que los efectos de estado y el equipamiento puedan decir sobre
 * cual estadistica actuan sin necesitar un metodo distinto por cada una.
 */
public enum Estadistica {
    VIDA,
    MANA,
    ATAQUE_FISICO,
    DEFENSA_FISICA,
    ATAQUE_MAGICO,
    DEFENSA_MAGICA,
    VELOCIDAD,
    SUERTE
}
