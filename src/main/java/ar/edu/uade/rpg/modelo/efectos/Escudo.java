package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

/**
 * Bolsa de puntos que se come el daño antes de que llegue a la vida. Es el
 * unico efecto que sobrescribe absorber().
 *
 * Absorbe tanto daño fisico como magico, y se gasta: cuando absorcionRestante
 * llega a cero deja pasar todo, aunque le queden turnos de duracion. No frena
 * al veneno, porque usa recibirDañoDirecto() y no pasa por aca.
 */
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
        // La absorcion ya viene cargada desde el constructor.
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        // No se regenera: lo que se gasto, se gasto.
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
