package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Apunta al primero que NO tenga cierto efecto encima. Lo implementa solo el
 * Emponzoñador, que busca a quien todavia no envenenó.
 *
 * Pide dos cosas a quien la implemente: que efecto busca y que hacer si ya
 * estan todos afectados. Esa segunda pregunta es la que hace que la interfaz
 * sea reutilizable: el dia que haya un enemigo que queme en vez de envenenar,
 * cambia el texto que devuelve getEfectoBuscado() y nada mas.
 */
public interface ObjetivoSinEfecto {

    String getEfectoBuscado();

    /** A quien apuntar cuando ya todos tienen el efecto. */
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
