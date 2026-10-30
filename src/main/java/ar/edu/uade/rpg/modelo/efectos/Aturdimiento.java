package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class Aturdimiento extends EfectoEstado {
    public Aturdimiento(int duracionEnTurnos) {
        super("Aturdimiento", duracionEnTurnos);
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
    }

    @Override
    public boolean impideActuar() {
        return true;
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new Aturdimiento(duracionEnTurnos);
    }
}
