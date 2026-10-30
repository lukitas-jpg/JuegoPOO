package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public interface ObjetivoSinEfecto {
    String getEfectoBuscado();

    Combatiente elegirDeRespaldo(List<Combatiente> vivos);

    default Combatiente elegirObjetivo(List<Combatiente> candidatos) {
        for (Combatiente candidato : candidatos) {
            if (candidato.estaVivo() && !tieneElEfecto(candidato)) {
                return candidato;
            }
        }
        return elegirDeRespaldo(candidatos);
    }

    private boolean tieneElEfecto(Combatiente candidato) {
        return candidato.getEfectosActivos().stream()
                .anyMatch(efecto -> efecto.getNombre().equals(getEfectoBuscado()));
    }
}
