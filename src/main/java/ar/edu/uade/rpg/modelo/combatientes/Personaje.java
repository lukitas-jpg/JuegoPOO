package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.items.Arma;
import ar.edu.uade.rpg.modelo.items.Armadura;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.SlotEquipo;

public abstract class Personaje extends Combatiente {
    public static final int EXPERIENCIA_BASE_POR_NIVEL = 30;

    public static final int NIVEL_MAXIMO = 20;

    public static final int NIVEL_INICIAL = 1;
    public static final int NIVEL_TERCER_MOVIMIENTO = 4;
    public static final int NIVEL_DEFINITIVA = 8;

    protected int nivel;
    protected int experienciaActual;
    protected int experienciaParaSubir;

    protected Arma armaEquipada;
    protected Armadura armaduraEquipada;

    protected Personaje(String nombre) {
        super(nombre);
        this.nivel = 1;
        this.experienciaActual = 0;
        this.experienciaParaSubir = EXPERIENCIA_BASE_POR_NIVEL;
    }

    public int ganarExperiencia(int cantidad) {
        if (nivel >= NIVEL_MAXIMO) {
            return 0;
        }
        experienciaActual += cantidad;
        int nivelesSubidos = 0;
        while (experienciaActual >= experienciaParaSubir && nivel < NIVEL_MAXIMO) {
            experienciaActual -= experienciaParaSubir;
            subirNivel();
            nivelesSubidos++;
        }
        if (nivel >= NIVEL_MAXIMO) {
            experienciaActual = 0;
        }
        return nivelesSubidos;
    }

    private void subirNivel() {
        nivel++;
        experienciaParaSubir = EXPERIENCIA_BASE_POR_NIVEL * nivel;
        aplicarCrecimientoPorNivel();
    }

    protected abstract void aplicarCrecimientoPorNivel();

    public abstract String getDescripcionClase();

    public abstract ClaseDePersonaje getClaseDePersonaje();

    public Equipable equipar(Equipable equipable) {
        if (!equipable.puedeEquiparlo(this)) {
            throw new IllegalArgumentException(
                    getNombre() + " no puede equiparse " + equipable.getNombre());
        }
        Equipable anterior = desequipar(equipable.getSlot());
        if (equipable.getSlot() == SlotEquipo.ARMA) {
            armaEquipada = (Arma) equipable;
        } else {
            armaduraEquipada = (Armadura) equipable;
        }
        equipable.aplicarBonificaciones(this);
        return anterior;
    }

    public Equipable desequipar(SlotEquipo slot) {
        Equipable actual = getEquipadoEn(slot);
        if (actual == null) {
            return null;
        }
        actual.quitarBonificaciones(this);
        if (slot == SlotEquipo.ARMA) {
            armaEquipada = null;
        } else {
            armaduraEquipada = null;
        }
        return actual;
    }

    public Equipable getEquipadoEn(SlotEquipo slot) {
        return slot == SlotEquipo.ARMA ? armaEquipada : armaduraEquipada;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperienciaActual() {
        return experienciaActual;
    }

    public int getExperienciaParaSubir() {
        return experienciaParaSubir;
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public Armadura getArmaduraEquipada() {
        return armaduraEquipada;
    }
}
