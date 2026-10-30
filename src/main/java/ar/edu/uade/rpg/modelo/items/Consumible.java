package ar.edu.uade.rpg.modelo.items;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

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
