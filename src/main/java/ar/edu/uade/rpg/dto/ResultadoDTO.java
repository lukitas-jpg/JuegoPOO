package ar.edu.uade.rpg.dto;

import java.util.List;

/**
 * Como termino una etapa de combate. Si la run termino, runGanada dice si fue
 * con la victoria final o con la party entera caida.
 */
public record ResultadoDTO(int numeroEtapa, boolean victoria, int experiencia, int oro,
                           boolean runTerminada, boolean runGanada, List<CombatienteDTO> party) {
}
