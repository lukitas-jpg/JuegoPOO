package ar.edu.uade.rpg.dto;

import java.util.List;

public record MaquinaDTO(int numeroEtapa, int oro, int costoDeLaTirada, boolean puedeTirar,
                         List<String> simbolos) {
}
