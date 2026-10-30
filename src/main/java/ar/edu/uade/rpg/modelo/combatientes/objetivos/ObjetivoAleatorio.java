package ar.edu.uade.rpg.modelo.combatientes.objetivos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

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
