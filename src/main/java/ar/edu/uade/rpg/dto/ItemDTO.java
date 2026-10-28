package ar.edu.uade.rpg.dto;

/**
 * Un objeto, para la tienda o el inventario. cantidad es cuantos hay en el
 * inventario; disponible dice si se puede comprar (en la tienda) o usar.
 */
public record ItemDTO(String nombre, String descripcion, String imagen, int precio,
                      String rareza, int cantidad, int tope, boolean disponible) {
}
