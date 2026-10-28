package ar.edu.uade.rpg.modelo.items;

import java.util.EnumMap;
import java.util.Map;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Item que se lleva puesto y mejora las estadisticas base del personaje.
 * Cada personaje puede tener un arma y una armadura al mismo tiempo.
 *
 * Las bonificaciones se aplican al equipar y se quitan al desequipar, asi que
 * quedan sumadas en las estadisticas del personaje. Como son valores fijos y
 * no porcentajes, quitarlas devuelve exactamente lo mismo que se habia sumado.
 */
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

    /**
     * Cada equipable es exclusivo de una clase: un baculo en manos del Tanque
     * no se sostiene ni temáticamente ni en los numeros.
     */
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
