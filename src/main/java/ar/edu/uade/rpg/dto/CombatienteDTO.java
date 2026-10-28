package ar.edu.uade.rpg.dto;

import java.util.List;

/**
 * Un personaje o un enemigo. Nivel y experiencia solo tienen sentido para los
 * personajes: en los enemigos vienen en 0 y esPersonaje() es false.
 */
public record CombatienteDTO(String nombre, String imagen, boolean esPersonaje,
                             int nivel, int experiencia, int experienciaParaSubir,
                             int vidaActual, int vidaMaxima, int manaActual, int manaMaximo,
                             boolean vivo, boolean defendiendo, boolean tieneElTurno,
                             List<EfectoDTO> efectos, String arma, String armadura) {
}
