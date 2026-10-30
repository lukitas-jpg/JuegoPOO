package ar.edu.uade.rpg.modelo.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Inventario implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Consumible> consumibles = new ArrayList<>();

    public boolean agregar(Consumible consumible) {
        if (contar(consumible.getNombre()) >= consumible.getTopeEnInventario()) {
            return false;
        }
        consumibles.add(consumible);
        return true;
    }

    public boolean quitar(Consumible consumible) {
        return consumibles.remove(consumible);
    }

    public int contar(String nombre) {
        int cantidad = 0;
        for (Consumible consumible : consumibles) {
            if (consumible.getNombre().equals(nombre)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public boolean tiene(String nombre) {
        return contar(nombre) > 0;
    }

    public boolean estaLleno(Consumible consumible) {
        return contar(consumible.getNombre()) >= consumible.getTopeEnInventario();
    }

    public Map<String, Integer> getResumen() {
        Map<String, Integer> resumen = new LinkedHashMap<>();
        for (Consumible consumible : consumibles) {
            resumen.merge(consumible.getNombre(), 1, Integer::sum);
        }
        return resumen;
    }

    public List<Consumible> getConsumibles() {
        return List.copyOf(consumibles);
    }

    public boolean estaVacio() {
        return consumibles.isEmpty();
    }
}
