package ar.edu.uade.rpg.dto;

public record ItemDTO(String nombre, String descripcion, String imagen, String imagenDeRespaldo,
                      int precio, String rareza, int cantidad, int tope, boolean disponible) {
}
