package ar.edu.uade.rpg.dto;

/** Una de las 6 clases, tal como se muestra en la pantalla de seleccion. */
public record ClaseDTO(String nombre, String imagen, String descripcion,
                       int vida, int mana, int velocidad) {
}
