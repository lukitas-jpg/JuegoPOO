package ar.edu.uade.rpg.dto;

import java.util.List;

public record CombatienteDTO(String nombre, String imagen, boolean esPersonaje,
                             int nivel, int experiencia, int experienciaParaSubir,
                             int vidaActual, int vidaMaxima, int manaActual, int manaMaximo,
                             boolean vivo, boolean defendiendo, boolean tieneElTurno,
                             List<EfectoDTO> efectos, String arma, String armadura) {
}
