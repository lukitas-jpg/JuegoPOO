package ar.edu.uade.rpg.modelo.efectos;

import java.io.Serializable;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Condicion temporal que afecta a un Combatiente durante una cantidad de
 * turnos: veneno, quemadura, aturdimiento, escudo, mejoras y debilitamientos.
 *
 * La duracion se cuenta en TURNOS PROPIOS del afectado. Como cada uno actua
 * una vez por ronda, en la practica un turno equivale a una ronda.
 *
 * Define tres momentos y cada subclase usa los que necesita:
 *   - alAplicarse:   una sola vez, cuando el efecto entra.
 *   - alTerminarTurno: al final de cada turno del afectado, lo haya jugado
 *                    o lo haya perdido aturdido. El veneno pega aca.
 *   - alExpirar:     cuando el efecto se va.
 *
 * El Veneno solo usa el del medio; el Escudo usa el primero y el ultimo.
 */
public abstract class EfectoEstado implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected int duracionEnTurnos;

    protected EfectoEstado(String nombre, int duracionEnTurnos) {
        this.nombre = nombre;
        this.duracionEnTurnos = duracionEnTurnos;
    }

    // ---------------------------------------------------------- hooks

    public abstract void alAplicarse(Combatiente objetivo);

    public abstract void alTerminarTurno(Combatiente objetivo);

    public abstract void alExpirar(Combatiente objetivo);

    /**
     * true solo para Veneno y Quemadura: son los unicos que se arrastran a la
     * etapa siguiente. Las mejoras, los debilitamientos, los escudos y el
     * aturdimiento se limpian al cerrar la etapa.
     */
    public abstract boolean persisteFueraDeCombate();

    /**
     * Cada aplicacion tiene que usar una copia propia. Si se aplicara siempre
     * la misma instancia, dos enemigos envenenados compartirian el mismo
     * Veneno y al expirar en uno se le iria al otro.
     */
    public abstract EfectoEstado copiar();

    // ------------------------------------- hooks con valor por defecto

    /**
     * Porcentaje que este efecto suma o resta a una estadistica. Devuelve 0
     * salvo en BuffEstadistica y DebuffEstadistica, que lo sobrescriben.
     */
    public int getModificadorPorcentual(Estadistica estadistica) {
        return 0;
    }

    /**
     * Absorbe parte del daño y devuelve el que pasa de largo. Por defecto no
     * absorbe nada; lo sobrescribe el Escudo.
     */
    public int absorber(int daño, TipoDaño tipo) {
        return daño;
    }

    /**
     * true si el efecto le hace perder el turno al afectado. Solo lo
     * sobrescribe Aturdimiento. Se consulta asi, y no comparando el nombre con
     * un texto, para que el dia que haya otro efecto que paralice no haya que
     * tocar a Combatiente.
     */
    public boolean impideActuar() {
        return false;
    }

    // ---------------------------------------------------------- estado

    public void reducirDuracion() {
        duracionEnTurnos--;
    }

    public boolean haExpirado() {
        return duracionEnTurnos <= 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getDuracionEnTurnos() {
        return duracionEnTurnos;
    }
}
