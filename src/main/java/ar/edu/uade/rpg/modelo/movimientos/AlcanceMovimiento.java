package ar.edu.uade.rpg.modelo.movimientos;

/**
 * A quienes alcanza un movimiento.
 *
 * Es lo que decide si hay que pedirle al jugador que elija un objetivo
 * (OBJETIVO_UNICO y ALIADO) o si el movimiento resuelve solo a quien afecta.
 */
public enum AlcanceMovimiento {
    OBJETIVO_UNICO,
    TODOS_LOS_ENEMIGOS,
    ALIADO,
    UNO_MISMO,
    TODA_LA_PARTY
}
