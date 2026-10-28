package ar.edu.uade.rpg.modelo.partida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Inventario;

/**
 * Los tres personajes del jugador, el inventario y el oro.
 *
 * El inventario es UNO para los tres y no uno por cabeza: las pociones son de
 * la party. Lo mismo el oro.
 *
 * La relacion con Personaje es de COMPOSICION: las instancias se crean al
 * confirmar la party y dejan de existir cuando termina la run. No viven fuera
 * de ella.
 */
public class Party implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lo que recibe un caido: la mitad. Sigue aprendiendo, pero menos. */
    private static final int PORCENTAJE_PARA_CAIDOS = 50;

    private final List<Personaje> miembros;
    private final Inventario inventario;
    private int oro;

    public Party(List<Personaje> miembros) {
        this.miembros = new ArrayList<>(miembros);
        this.inventario = new Inventario();
        this.oro = 0;
    }

    // ------------------------------------------------------------- miembros

    /** Si no queda nadie en pie, la run se termino. */
    public boolean estaDerrotada() {
        return getMiembrosVivos().isEmpty();
    }

    public List<Personaje> getMiembrosVivos() {
        List<Personaje> vivos = new ArrayList<>();
        for (Personaje miembro : miembros) {
            if (miembro.estaVivo()) {
                vivos.add(miembro);
            }
        }
        return vivos;
    }

    public List<Personaje> getMiembrosCaidos() {
        List<Personaje> caidos = new ArrayList<>();
        for (Personaje miembro : miembros) {
            if (!miembro.estaVivo()) {
                caidos.add(miembro);
            }
        }
        return caidos;
    }

    public boolean tieneCaidos() {
        return !getMiembrosCaidos().isEmpty();
    }

    public List<Personaje> getMiembros() {
        return List.copyOf(miembros);
    }

    // ---------------------------------------------------------- progresion

    /**
     * Reparte la experiencia de la etapa. Cada uno decide solo si le alcanza
     * para subir de nivel: la Party no sabe ni le importa cuanto necesita cada
     * uno.
     */
    public void repartirExperiencia(int cantidad) {
        for (Personaje miembro : miembros) {
            int leToca = miembro.estaVivo()
                    ? cantidad
                    : cantidad * PORCENTAJE_PARA_CAIDOS / 100;
            miembro.ganarExperiencia(leToca);
        }
    }

    // ----------------------------------------------------------------- oro

    public void agregarOro(int cantidad) {
        oro += cantidad;
    }

    /** Descuenta si alcanza. Avisa si se pudo pagar. */
    public boolean gastarOro(int cantidad) {
        if (cantidad > oro) {
            return false;
        }
        oro -= cantidad;
        return true;
    }

    public boolean puedePagar(int cantidad) {
        return oro >= cantidad;
    }

    public int getOro() {
        return oro;
    }

    // ------------------------------------------------------------- efectos

    /**
     * Al cerrar una etapa se limpian las mejoras, los escudos y el
     * aturdimiento. El veneno se queda: es el unico que se arrastra.
     */
    public void limpiarEfectosTemporales() {
        for (Personaje miembro : miembros) {
            miembro.limpiarEfectosTemporales();
        }
    }

    /** True si alguien entra a la etapa siguiente envenenado o quemado. */
    public boolean tieneAlgunEfectoPersistente() {
        for (Personaje miembro : miembros) {
            if (!miembro.getEfectosActivos().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public Inventario getInventario() {
        return inventario;
    }
}
