package ar.edu.uade.rpg.modelo.movimientos;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;

/**
 * Lo que dejo una accion: cuanto daño hizo, a quienes alcanzo, con quienes
 * fallo y que efectos aplico.
 *
 * Existe para que la informacion viaje desde el modelo hacia afuera sin que
 * nadie tenga que espiar el estado interno del combate. Como guarda cada golpe,
 * tambien es quien sabe escribir la linea del registro con el daño neto
 * (Information Expert).
 */
public class ResultadoAccion {

    /**
     * Un golpe que entro: a quien le pego y cuanta vida le saco de verdad, ya
     * descontados la defensa y el escudo.
     */
    public record Golpe(Combatiente objetivo, int daño, boolean fueCritico) { }

    private String descripcion;
    private int dañoTotal;
    private boolean fueCritico;

    private final List<Golpe> golpes = new ArrayList<>();

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

    /** Lo llama el Ataque por cada objetivo, con el daño que devolvio recibirDaño(). */
    public void registrarGolpe(Combatiente objetivo, int daño, boolean critico) {
        golpes.add(new Golpe(objetivo, daño, critico));
        dañoTotal += daño;
        if (critico) {
            fueCritico = true;
        }
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

    /**
     * La linea que va al registro del combate: lo que se hizo y, si hubo
     * golpes, cuanto daño neto recibio cada objetivo. Con mas de un golpe se
     * agrega el total, asi un Torbellino o los tres golpes del Acechador se
     * leen de una.
     */
    public String getDescripcion() {
        StringBuilder texto = new StringBuilder(descripcion);
        for (Golpe golpe : golpes) {
            texto.append(' ').append(golpe.objetivo().getNombre())
                 .append(" recibe ").append(golpe.daño()).append(" de daño");
            if (golpe.fueCritico()) {
                texto.append(" (crítico)");
            }
            texto.append('.');
        }
        if (golpes.size() > 1) {
            texto.append(" Daño neto total: ").append(dañoTotal).append('.');
        }
        return texto.toString();
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

    public List<Golpe> getGolpes() {
        return List.copyOf(golpes);
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
