package ar.edu.uade.rpg.modelo.partida;

import java.io.Serializable;

/**
 * El estado de una run: en que etapa va, cuantas completo y como termino.
 *
 * NO guarda el nivel de nadie ni el inventario: guarda una referencia a la
 * Party, y cada dato vive en un solo objeto. Por eso al subir de nivel un
 * personaje no hay que avisarle a la Partida.
 *
 * Es lo unico que se serializa al guardar. La etapa actual no se guarda: se
 * vuelve a generar al cargar, a partir del numero.
 */
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

    /**
     * Cierra la etapa en curso y pasa a la siguiente. Si la que se acaba de
     * cerrar era la 20, la run esta ganada: esa es la unica forma de ganar.
     */
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
