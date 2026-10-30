package ar.edu.uade.rpg.modelo.items;

import java.util.Map;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public class Arma extends Equipable {
    private final TipoDaño tipoDañoQueMejora;

    public Arma(String nombre, String descripcion, int precio, Rareza rareza,
                ClaseDePersonaje claseRequerida, TipoDaño tipoDañoQueMejora,
                Map<Estadistica, Integer> bonificaciones) {
        super(nombre, descripcion, precio, rareza, SlotEquipo.ARMA, claseRequerida, bonificaciones);
        this.tipoDañoQueMejora = tipoDañoQueMejora;
    }

    public TipoDaño getTipoDañoQueMejora() {
        return tipoDañoQueMejora;
    }
}
