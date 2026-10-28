package ar.edu.uade.rpg.modelo.movimientos;

/**
 * Algo que un combatiente decide hacer en su turno, empaquetado como objeto.
 *
 * Es el patron Command. En vez de que Combate tenga un switch preguntando "¿que
 * eligio, atacar, defender o usar un item?", cada decision se convierte en una
 * Accion y Combate solo le dice ejecutar(). Para agregar una forma nueva de
 * gastar el turno alcanza con una clase mas; Combate no se toca.
 *
 * Esto es lo que permite que el jugador y los 6 enemigos pasen por el MISMO
 * camino: el jugador arma su Accion desde la pantalla y el enemigo la arma en
 * su elegirAccion(). Combate no distingue entre los dos.
 */
public interface Accion {

    ResultadoAccion ejecutar();

    /** Una linea para el registro del combate, antes de ejecutarla. */
    String getDescripcion();
}
