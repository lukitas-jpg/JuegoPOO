package ar.edu.uade.rpg.modelo.efectos;

import java.io.Serializable;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public abstract class EfectoEstado implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nombre;
    protected int duracionEnTurnos;

    protected EfectoEstado(String nombre, int duracionEnTurnos) {
        this.nombre = nombre;
        this.duracionEnTurnos = duracionEnTurnos;
    }

    public abstract void alAplicarse(Combatiente objetivo);

    public abstract void alTerminarTurno(Combatiente objetivo);

    public abstract void alExpirar(Combatiente objetivo);

    public abstract boolean persisteFueraDeCombate();

    public abstract EfectoEstado copiar();

    public int getModificadorPorcentual(Estadistica estadistica) {
        return 0;
    }

    public int absorber(int daño, TipoDaño tipo) {
        return daño;
    }

    public boolean impideActuar() {
        return false;
    }

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
