package ar.edu.uade.rpg.modelo.combatientes;

/**
 * Los dos tipos de daño del juego, al estilo Pokemon.
 *
 * El daño FISICO enfrenta el ataque fisico del atacante contra la defensa
 * fisica del objetivo, y el MAGICO hace lo mismo con los atributos magicos.
 * Esa separacion es la que le da identidad a cada clase: un Tanque con mucha
 * defensa fisica sigue siendo vulnerable a un Hechicero.
 */
public enum TipoDaño {
    FISICO,
    MAGICO
}
