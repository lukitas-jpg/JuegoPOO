package ar.edu.uade.rpg.dto;

/** Un efecto activo sobre un combatiente: veneno, escudo, aturdimiento... */
public record EfectoDTO(String nombre, String imagen, int turnosRestantes) {
}
