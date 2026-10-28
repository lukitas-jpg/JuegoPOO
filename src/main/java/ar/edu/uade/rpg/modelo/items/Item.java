package ar.edu.uade.rpg.modelo.items;

import java.io.Serializable;

/**
 * Cualquier cosa que se pueda comprar en la tienda. De aca bajan los
 * Consumible (se gastan al usarlos) y los Equipable (arma y armadura).
 */
public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected String descripcion;
    protected int precio;
    protected Rareza rareza;

    protected Item(String nombre, String descripcion, int precio, Rareza rareza) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.rareza = rareza;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public Rareza getRareza() {
        return rareza;
    }
}
