package ar.edu.uade.rpg.modelo.partida;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Inventario;

public class Party implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int PORCENTAJE_PARA_CAIDOS = 50;

    private final List<Personaje> miembros;
    private final Inventario inventario;
    private int oro;

    public Party(List<Personaje> miembros) {
        this.miembros = new ArrayList<>(miembros);
        this.inventario = new Inventario();
        this.oro = 0;
    }

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

    public void repartirExperiencia(int cantidad) {
        for (Personaje miembro : miembros) {
            int leToca = miembro.estaVivo()
                    ? cantidad
                    : cantidad * PORCENTAJE_PARA_CAIDOS / 100;
            miembro.ganarExperiencia(leToca);
        }
    }

    public void agregarOro(int cantidad) {
        oro += cantidad;
    }

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

    public void limpiarEfectosTemporales() {
        for (Personaje miembro : miembros) {
            miembro.limpiarEfectosTemporales();
        }
    }

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
