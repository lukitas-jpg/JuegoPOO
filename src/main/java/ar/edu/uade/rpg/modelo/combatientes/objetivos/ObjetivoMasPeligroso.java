package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public interface ObjetivoMasPeligroso {
    default Combatiente elegirObjetivo(List<Combatiente> candidatos) {
        Combatiente elegido = null;
        int mayorAmenaza = -1;
        for (Combatiente candidato : candidatos) {
            if (!candidato.estaVivo()) {
                continue;
            }
            int amenaza = candidato.getAtaqueFisicoEfectivo() + candidato.getAtaqueMagicoEfectivo();
            if (amenaza > mayorAmenaza) {
                mayorAmenaza = amenaza;
                elegido = candidato;
            }
        }
        return elegido;
    }
}
