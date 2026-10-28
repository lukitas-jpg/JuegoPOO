package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Item de un solo uso. A diferencia de los equipables, se guarda en el
 * Inventario y se gasta.
 *
 * No hereda de nadie el efecto: lo TIENE. Eso permite que haya un solo tipo
 * Consumible y que la variedad salga de los datos, igual que pasa con los 24
 * movimientos y sus 4 clases.
 *
 * Cada uno sabe cuantas unidades se pueden llevar encima: esa regla es del
 * objeto, no del Inventario, que no tendria por que conocer los topes de cinco
 * items distintos.
 */
public class Consumible extends Item {

    private final EfectoConsumible efecto;
    private final int topeEnInventario;

    public Consumible(String nombre, String descripcion, int precio, Rareza rareza,
                      EfectoConsumible efecto, int topeEnInventario) {
        super(nombre, descripcion, precio, rareza);
        this.efecto = efecto;
        this.topeEnInventario = topeEnInventario;
    }

    public void usarSobre(Combatiente objetivo) {
        efecto.aplicar(objetivo);
    }

    public boolean puedeUsarseSobre(Combatiente objetivo) {
        return efecto.puedeAplicarseA(objetivo);
    }

    public EfectoConsumible getEfecto() {
        return efecto;
    }

    public int getTopeEnInventario() {
        return topeEnInventario;
    }
}
