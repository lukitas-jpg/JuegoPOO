package ar.edu.uade.rpg.modelo.movimientos;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;

/**
 * Lo que dejo una accion: cuanto daño hizo, a quienes alcanzo, con quienes
 * fallo y que efectos aplico.
 *
 * No tiene logica propia: existe para que la informacion viaje desde el modelo
 * hacia afuera sin que nadie tenga que espiar el estado interno del combate.
 */
public class ResultadoAccion {

    private String descripcion;
    private int dañoTotal;
    private boolean fueCritico;

    private final List<Combatiente> objetivosAfectados = new ArrayList<>();
    private final List<Combatiente> objetivosFallados = new ArrayList<>();
    private final List<EfectoEstado> efectosAplicados = new ArrayList<>();

    public ResultadoAccion(String descripcion) {
        this.descripcion = descripcion;
        this.dañoTotal = 0;
        this.fueCritico = false;
    }

    // ------------------------------------------- se arma mientras se resuelve

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void sumarDaño(int daño) {
        this.dañoTotal += daño;
    }

    public void marcarCritico() {
        this.fueCritico = true;
    }

    public void agregarObjetivoAfectado(Combatiente objetivo) {
        objetivosAfectados.add(objetivo);
    }

    public void agregarObjetivoFallado(Combatiente objetivo) {
        objetivosFallados.add(objetivo);
    }

    public void agregarEfectoAplicado(EfectoEstado efecto) {
        efectosAplicados.add(efecto);
    }

    // ------------------------------------------------------------- consultas

    public String getDescripcion() {
        return descripcion;
    }

    public int getDañoTotal() {
        return dañoTotal;
    }

    public boolean fueCritico() {
        return fueCritico;
    }

    public boolean huboAlgunFallo() {
        return !objetivosFallados.isEmpty();
    }

    public List<Combatiente> getObjetivosAfectados() {
        return List.copyOf(objetivosAfectados);
    }

    public List<Combatiente> getObjetivosFallados() {
        return List.copyOf(objetivosFallados);
    }

    public List<EfectoEstado> getEfectosAplicados() {
        return List.copyOf(efectosAplicados);
    }
}
