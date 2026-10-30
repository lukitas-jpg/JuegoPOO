package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public interface ObjetivoMasDebil {
    default Combatiente elegirObjetivo(List<Combatiente> candidatos) {
        Combatiente elegido = null;
        for (Combatiente candidato : candidatos) {
            if (!candidato.estaVivo()) {
                continue;
            }
            if (elegido == null || candidato.getVidaActual() < elegido.getVidaActual()) {
                elegido = candidato;
            }
        }
        return elegido;
    }
}
