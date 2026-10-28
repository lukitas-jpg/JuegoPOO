package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Apunta al de menos vida. Lo implementan el Bruto, que remata al herido, y el
 * Acolito, que cura al mas lastimado.
 *
 * El metodo recibe una lista de candidatos y no sabe si son enemigos o
 * aliados: el Bruto le pasa la party y el Acolito le pasa sus companeros. La
 * misma interfaz, dos usos opuestos, cero codigo duplicado.
 */
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
