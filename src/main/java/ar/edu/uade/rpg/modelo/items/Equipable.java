package ar.edu.uade.rpg.modelo.items;

import java.util.EnumMap;
import java.util.Map;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public abstract class Equipable extends Item {
    protected SlotEquipo slot;
    protected ClaseDePersonaje claseRequerida;
    protected Map<Estadistica, Integer> bonificaciones;

    protected Equipable(String nombre, String descripcion, int precio, Rareza rareza,
                        SlotEquipo slot, ClaseDePersonaje claseRequerida,
                        Map<Estadistica, Integer> bonificaciones) {
        super(nombre, descripcion, precio, rareza);
        this.slot = slot;
        this.claseRequerida = claseRequerida;
        this.bonificaciones = new EnumMap<>(bonificaciones);
    }

    public boolean puedeEquiparlo(Personaje personaje) {
        return personaje.getClaseDePersonaje() == claseRequerida;
    }

    public void aplicarBonificaciones(Personaje personaje) {
        for (Map.Entry<Estadistica, Integer> bonificacion : bonificaciones.entrySet()) {
            personaje.sumarAEstadistica(bonificacion.getKey(), bonificacion.getValue());
        }
    }

    public void quitarBonificaciones(Personaje personaje) {
        for (Map.Entry<Estadistica, Integer> bonificacion : bonificaciones.entrySet()) {
            personaje.sumarAEstadistica(bonificacion.getKey(), -bonificacion.getValue());
        }
    }

    public Map<Estadistica, Integer> getBonificaciones() {
        return Map.copyOf(bonificaciones);
    }

    public SlotEquipo getSlot() {
        return slot;
    }

    public ClaseDePersonaje getClaseRequerida() {
        return claseRequerida;
    }
}
