package ar.edu.uade.rpg.dto;

import java.util.List;

public record ResultadoDTO(int numeroEtapa, boolean victoria, int experiencia, int oro,
                           boolean runTerminada, boolean runGanada, List<CombatienteDTO> party) {
}
