package ar.edu.uade.rpg.modelo.combate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Decide quien actua y en que orden.
 *
 * Es una Pure Fabrication: un "gestor de turnos" no existe en el mundo del
 * juego. Existe para que ni Combate ni Combatiente carguen con la logica de
 * ordenamiento, que no es de ninguno de los dos.
 *
 * El orden se calcula AL INICIO DE CADA RONDA, no una vez por combate. Por eso
 * un buffo de velocidad como Sombra Agil se nota recien en la ronda siguiente.
 *
 * Los empates se resuelven al azar, no por orden de llegada: si no, el que
 * estuviera primero en la lista ganaria siempre todos los empates.
 */
public class GestorDeTurnos {

    private final List<Combatiente> participantes = new ArrayList<>();
    private final List<Combatiente> ordenDeLaRonda = new ArrayList<>();
    private final Random random;
    private int numeroRonda;
    private int indiceActual;

    public GestorDeTurnos(List<Combatiente> participantes) {
        this(participantes, new Random());
    }

    public GestorDeTurnos(List<Combatiente> participantes, Random random) {
        this.participantes.addAll(participantes);
        this.random = random;
        this.numeroRonda = 0;
        this.indiceActual = 0;
    }

    /** Recalcula el orden con las velocidades de ESTE momento. */
    public void iniciarNuevaRonda() {
        numeroRonda++;
        indiceActual = 0;
        ordenDeLaRonda.clear();
        ordenDeLaRonda.addAll(ordenarPorVelocidad());
    }

    /**
     * Quien tiene el turno. Saltea a los que murieron despues de armarse el
     * orden: la ronda se planifica al principio, pero alguien puede caer en el
     * medio.
     */
    public Combatiente getSiguienteEnActuar() {
        while (indiceActual < ordenDeLaRonda.size()
                && !ordenDeLaRonda.get(indiceActual).estaVivo()) {
            indiceActual++;
        }
        return indiceActual < ordenDeLaRonda.size() ? ordenDeLaRonda.get(indiceActual) : null;
    }

    public void consumirTurno() {
        indiceActual++;
    }

    public boolean rondaTerminada() {
        return getSiguienteEnActuar() == null;
    }

    public int getNumeroRonda() {
        return numeroRonda;
    }

    // ------------------------------------------------- altas y bajas

    /**
     * Saca a un caido. Si ya habia actuado en esta ronda (esta antes del indice),
     * el indice retrocede uno: si no, la lista se corre y el que venia despues
     * perderia su turno sin que nadie se entere.
     */
    public void retirar(Combatiente combatiente) {
        participantes.remove(combatiente);
        int posicion = ordenDeLaRonda.indexOf(combatiente);
        if (posicion >= 0) {
            ordenDeLaRonda.remove(posicion);
            if (posicion < indiceActual) {
                indiceActual--;
            }
        }
    }

    /**
     * Los que entran a mitad de ronda (un personaje revivido) actuan
     * recien en la ronda siguiente: se suman a los participantes pero no al
     * orden ya armado.
     */
    public void incorporar(Combatiente combatiente) {
        if (!participantes.contains(combatiente)) {
            participantes.add(combatiente);
        }
    }

    public List<Combatiente> getOrdenPrevisto() {
        return List.copyOf(ordenDeLaRonda);
    }

    public List<Combatiente> getParticipantes() {
        return List.copyOf(participantes);
    }

    // ----------------------------------------------------- ordenamiento

    private List<Combatiente> ordenarPorVelocidad() {
        List<Combatiente> vivos = new ArrayList<>();
        for (Combatiente participante : participantes) {
            if (participante.estaVivo()) {
                vivos.add(participante);
            }
        }
        // Barajar primero y despues ordenar de forma estable deja los empates
        // en orden al azar, sin tener que comparar de a pares.
        Collections.shuffle(vivos, random);
        vivos.sort((uno, otro) -> otro.getVelocidadEfectiva() - uno.getVelocidadEfectiva());
        return vivos;
    }
}
