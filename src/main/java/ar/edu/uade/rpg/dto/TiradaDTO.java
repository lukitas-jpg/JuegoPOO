package ar.edu.uade.rpg.dto;

import java.util.List;

/** Lo que salio en los 3 rodillos y cuanto pago. */
public record TiradaDTO(List<String> simbolos, int multiplicador, int oroGanado, boolean fueGratis) {
}
