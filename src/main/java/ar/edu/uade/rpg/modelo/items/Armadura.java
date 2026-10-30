package ar.edu.uade.rpg.modelo.items;

import java.util.Map;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public class Armadura extends Equipable {
    public Armadura(String nombre, String descripcion, int precio, Rareza rareza,
                    ClaseDePersonaje claseRequerida, Map<Estadistica, Integer> bonificaciones) {
        super(nombre, descripcion, precio, rareza, SlotEquipo.ARMADURA, claseRequerida, bonificaciones);
    }
}
