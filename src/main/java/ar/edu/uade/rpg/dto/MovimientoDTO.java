package ar.edu.uade.rpg.dto;

/**
 * Un movimiento del que tiene el turno. Si no esta disponible, motivo dice por
 * que (bloqueado, en cooldown o sin mana) para mostrarlo en el boton.
 */
public record MovimientoDTO(String nombre, String descripcion, int costoMana,
                            boolean disponible, String motivo, boolean eligeObjetivo) {
}
