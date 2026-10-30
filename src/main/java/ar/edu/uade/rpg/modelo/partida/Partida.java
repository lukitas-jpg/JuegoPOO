package ar.edu.uade.rpg.modelo.partida;

import java.io.Serializable;

public class Partida implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int CANTIDAD_DE_ETAPAS = 20;

    private final Party party;
    private int numeroEtapaActual;
    private int etapasCompletadas;
    private EstadoPartida estado;

    public Partida(Party party) {
        this.party = party;
        this.numeroEtapaActual = 1;
        this.etapasCompletadas = 0;
        this.estado = EstadoPartida.EN_CURSO;
    }

    public void avanzarEtapa() {
        etapasCompletadas++;
        if (numeroEtapaActual >= CANTIDAD_DE_ETAPAS) {
            estado = EstadoPartida.GANADA;
            return;
        }
        numeroEtapaActual++;
    }

    public void terminarPorDerrota() {
        estado = EstadoPartida.PERDIDA;
    }

    public boolean estaEnCurso() {
        return estado == EstadoPartida.EN_CURSO;
    }

    public boolean fueGanada() {
        return estado == EstadoPartida.GANADA;
    }

    public boolean fuePerdida() {
        return estado == EstadoPartida.PERDIDA;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public int getEtapaActual() {
        return numeroEtapaActual;
    }

    public int getEtapasCompletadas() {
        return etapasCompletadas;
    }

    public Party getParty() {
        return party;
    }
}
