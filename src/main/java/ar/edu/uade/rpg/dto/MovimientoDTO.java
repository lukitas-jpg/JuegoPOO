package ar.edu.uade.rpg.dto;

public record MovimientoDTO(String nombre, String descripcion, int costoMana,
                            boolean disponible, String motivo, boolean eligeObjetivo) {
}
