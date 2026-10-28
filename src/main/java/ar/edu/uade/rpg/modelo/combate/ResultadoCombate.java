package ar.edu.uade.rpg.modelo.combate;

import java.util.List;

/**
 * Lo que dejo una pelea: si se gano, cuanta experiencia y cuanto oro paga, y el
 * registro completo de lo que paso.
 *
 * La experiencia y el oro NO los calcula el combate sumando lo que valia cada
 * enemigo: los define la etapa entera. Si vinieran por enemigo, el Nigromante
 * seria una fuente infinita de ambos, porque invoca esqueletos sin parar.
 *
 * Es inmutable: un combate terminado no cambia.
 */
public class ResultadoCombate {

    private final boolean victoria;
    private final int experienciaGanada;
    private final int oroGanado;
    private final List<String> registroCompleto;

    public ResultadoCombate(boolean victoria, int experienciaGanada, int oroGanado,
                            List<String> registroCompleto) {
        this.victoria = victoria;
        this.experienciaGanada = experienciaGanada;
        this.oroGanado = oroGanado;
        this.registroCompleto = List.copyOf(registroCompleto);
    }

    public boolean fueVictoria() {
        return victoria;
    }

    public int getExperienciaGanada() {
        return experienciaGanada;
    }

    public int getOroGanado() {
        return oroGanado;
    }

    public List<String> getRegistroCompleto() {
        return registroCompleto;
    }
}
