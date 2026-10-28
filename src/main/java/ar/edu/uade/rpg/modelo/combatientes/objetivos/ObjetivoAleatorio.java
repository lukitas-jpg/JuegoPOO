package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Apunta a cualquiera, al azar. Lo implementan el Esqueleto, el Guardian, el
 * Acechador y el Nigromante.
 *
 * Como las interfaces no pueden tener atributos de instancia, esta PIDE el
 * Random en vez de tenerlo: declara getRandom() como abstracto y quien la
 * implemente lo resuelve. Enemigo ya tiene el Random como atributo protegido,
 * asi que ningun enemigo tiene que escribir ese metodo.
 */
public interface ObjetivoAleatorio {

    Random getRandom();

    default Combatiente elegirObjetivo(List<Combatiente> candidatos) {
        List<Combatiente> vivos = new ArrayList<>();
        for (Combatiente candidato : candidatos) {
            if (candidato.estaVivo()) {
                vivos.add(candidato);
            }
        }
        return vivos.isEmpty() ? null : vivos.get(getRandom().nextInt(vivos.size()));
    }
}
