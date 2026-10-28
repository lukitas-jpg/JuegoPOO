package ar.edu.uade.rpg.modelo.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lo que la party lleva encima. Guarda SOLO consumibles: los equipables no
 * pasan por aca, se ponen directamente en el slot del personaje y lo que estaba
 * puesto se descarta.
 *
 * Es uno solo para los tres personajes y no uno por cabeza. Es mas comodo de
 * jugar y mas facil de defender: las pociones son de la party.
 *
 * Cada unidad es una entrada de la lista. Con topes de 12 como mucho, la lista
 * nunca pasa de unas pocas decenas y recorrerla sale mas barato que mantener
 * dos mapas sincronizados.
 */
public class Inventario implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Consumible> consumibles = new ArrayList<>();

    /** Agrega uno si no se paso del tope de ese objeto. Avisa si entro. */
    public boolean agregar(Consumible consumible) {
        if (contar(consumible.getNombre()) >= consumible.getTopeEnInventario()) {
            return false;
        }
        consumibles.add(consumible);
        return true;
    }

    /** Saca una unidad del objeto que se acaba de usar. Avisa si habia. */
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

    /** Una entrada por objeto distinto, con su cantidad: lo que muestra la pantalla. */
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
