package ar.edu.uade.rpg.dto;

import java.util.List;

/**
 * La maquina de slots antes de tirar: cuanto oro hay, cuanto cuesta la tirada
 * y las imagenes de los 6 simbolos (la pantalla las hace girar).
 */
public record MaquinaDTO(int numeroEtapa, int oro, int costoDeLaTirada, boolean puedeTirar,
                         List<String> simbolos) {
}
