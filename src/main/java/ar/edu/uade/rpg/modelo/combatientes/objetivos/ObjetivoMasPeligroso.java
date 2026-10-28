package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Apunta al que mas pega, sumando ataque fisico y magico efectivos. Lo
 * implementan el Berserker, el Verdugo y la fase 2 del SeñorDeLasSombras.
 *
 * Usa los valores EFECTIVOS y no los base, asi que un personaje recien
 * buffeado con Grito de Guerra se vuelve el objetivo. Eso le da al jugador algo
 * para pensar: potenciar al Guerrero tambien lo pinta de rojo.
 */
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
