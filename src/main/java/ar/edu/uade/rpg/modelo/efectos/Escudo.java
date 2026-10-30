package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

public class Escudo extends EfectoEstado {
    private int absorcionRestante;

    public Escudo(int absorcion, int duracionEnTurnos) {
        super("Escudo", duracionEnTurnos);
        this.absorcionRestante = absorcion;
    }

    @Override
    public int absorber(int daño, TipoDaño tipo) {
        int absorbido = Math.min(daño, absorcionRestante);
        absorcionRestante -= absorbido;
        return daño - absorbido;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        absorcionRestante = 0;
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new Escudo(absorcionRestante, duracionEnTurnos);
    }

    public int getAbsorcionRestante() {
        return absorcionRestante;
    }
}
