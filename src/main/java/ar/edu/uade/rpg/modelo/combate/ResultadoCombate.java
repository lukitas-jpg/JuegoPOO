package ar.edu.uade.rpg.modelo.combate;

import java.util.List;

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
