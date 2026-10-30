package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class Quemadura extends EfectoEstado {
    public static final int GOLPE_MAXIMO = 25;

    private static final int PORCENTAJE_TOTAL = 100;

    private final int porcentajePorTurno;

    public Quemadura(int porcentajePorTurno, int duracionEnTurnos) {
        super("Quemadura", duracionEnTurnos);
        this.porcentajePorTurno = porcentajePorTurno;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        int daño = objetivo.getVidaMaxima() * porcentajePorTurno / PORCENTAJE_TOTAL;
        objetivo.recibirDañoDirecto(Math.min(daño, GOLPE_MAXIMO));
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return true;
    }

    @Override
    public EfectoEstado copiar() {
        return new Quemadura(porcentajePorTurno, duracionEnTurnos);
    }
}
