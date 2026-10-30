package ar.edu.uade.rpg.modelo.combate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

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

    public void iniciarNuevaRonda() {
        numeroRonda++;
        indiceActual = 0;
        ordenDeLaRonda.clear();
        ordenDeLaRonda.addAll(ordenarPorVelocidad());
    }

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

    private List<Combatiente> ordenarPorVelocidad() {
        List<Combatiente> vivos = new ArrayList<>();
        for (Combatiente participante : participantes) {
            if (participante.estaVivo()) {
                vivos.add(participante);
            }
        }

        Collections.shuffle(vivos, random);
        vivos.sort((uno, otro) -> otro.getVelocidadEfectiva() - uno.getVelocidadEfectiva());
        return vivos;
    }
}
