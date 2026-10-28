package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.items.Arma;
import ar.edu.uade.rpg.modelo.items.Armadura;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.SlotEquipo;

/**
 * Un integrante de la party: lo que el jugador controla. Lo que lo distingue de
 * un Enemigo es que progresa (nivel, experiencia) y que lleva equipo.
 *
 * Es abstracta: no se juega con un "Personaje" sino con un Guerrero, un Mago,
 * un Arquero, un Curador, un Asesino o un Tanque. Cada una de esas seis clases
 * aporta dos cosas y nada mas: sus estadisticas base de nivel 1 y cuanto le
 * crece cada estadistica por nivel. Todo el resto del comportamiento (ganar
 * experiencia, subir de nivel, equiparse) esta resuelto aca una sola vez.
 *
 * Eso ultimo es un Template Method: subirNivel() define el orden de los pasos
 * y deja un solo hueco, aplicarCrecimientoPorNivel(), que completa cada clase.
 */
public abstract class Personaje extends Combatiente {

    /** Experiencia que pide cada nivel: 30 x nivel actual. */
    public static final int EXPERIENCIA_BASE_POR_NIVEL = 30;

    public static final int NIVEL_MAXIMO = 20;

    /**
     * Los 4 slots de movimiento se desbloquean a nivel 1, 1, 4 y 8. Los dos
     * primeros vienen de entrada para que la etapa 1 sea jugable; el tercero y
     * la definitiva se ganan. El desbloqueo no se guarda en ningun lado: lo
     * resuelve Movimiento.estaDesbloqueado() comparando contra el nivel.
     */
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

    // ------------------------------------------------------------- progresion

    /**
     * Suma experiencia y sube los niveles que correspondan. Es un while y no un
     * if porque una etapa puede dar de golpe mas de lo que falta para dos
     * niveles, y en ese caso tiene que subir los dos.
     *
     * Quien reparte la experiencia es la Etapa, no el enemigo: los muertos
     * reciben la mitad y eso lo resuelve Party antes de llamar a este metodo.
     *
     * @return cuantos niveles subio, para que el combate lo pueda avisar.
     */
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

    /**
     * Los tres pasos de subir de nivel, siempre en este orden y siempre los
     * mismos para las seis clases. El unico paso que cambia segun la clase es
     * el tercero, y por eso es el que queda abierto.
     */
    private void subirNivel() {
        nivel++;
        experienciaParaSubir = EXPERIENCIA_BASE_POR_NIVEL * nivel;
        aplicarCrecimientoPorNivel();
    }

    /**
     * Cuanto le crece cada estadistica al pasar de nivel. Lo completa cada
     * clase con sus propios numeros. Es protected porque solo tiene sentido
     * llamarlo desde subirNivel(): si fuera publico, cualquiera podria hacer
     * crecer a un personaje sin que haya subido de nivel.
     */
    protected abstract void aplicarCrecimientoPorNivel();

    /**
     * Una linea que describe el rol de la clase, para la pantalla donde el
     * jugador arma la party. Es abstracto porque el texto es lo unico que
     * ninguna clase puede heredar de otra.
     */
    public abstract String getDescripcionClase();

    /**
     * A que clase pertenece. Lo usan los equipables para saber quien puede
     * ponerselos y la tienda para no ofrecer objetos que nadie de la party
     * puede usar.
     */
    public abstract ClaseDePersonaje getClaseDePersonaje();

    // ----------------------------------------------------------------- equipo

    /**
     * Pone el equipable en su slot y devuelve el que estaba puesto, o null si
     * el slot estaba libre. Devolverlo en vez de descartarlo es lo que permite
     * que la vista avise "vas a cambiar X por Y" y que el viejo se pueda tirar
     * o guardar segun lo que elija el jugador.
     *
     * Las bonificaciones del que sale se restan antes de sumar las del que
     * entra, asi no se acumulan las de los dos.
     */
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

    /**
     * Saca lo que haya en el slot, le resta las bonificaciones y lo devuelve.
     * null si el slot ya estaba vacio.
     */
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

    // -------------------------------------------------------------- consultas

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
