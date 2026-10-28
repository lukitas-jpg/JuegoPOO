package ar.edu.uade.rpg.dto;

/**
 * Un objeto, para la tienda o el inventario. cantidad es cuantos hay en el
 * inventario; disponible dice si se puede comprar (en la tienda) o usar.
 * imagenDeRespaldo se usa si todavia no hay un dibujo propio para el objeto.
 */
public record ItemDTO(String nombre, String descripcion, String imagen, String imagenDeRespaldo,
                      int precio, String rareza, int cantidad, int tope, boolean disponible) {
}
