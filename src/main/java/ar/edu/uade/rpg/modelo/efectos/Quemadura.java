package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Igual que el Veneno pero mas corto y mas fuerte: 10% por turno durante 2
 * turnos, con el mismo tope de 25. Solo lo aplica la segunda fase del
 * SeñorDeLasSombras, asi que ningun personaje del jugador puede provocarlo.
 *
 * Es el otro efecto que se arrastra entre etapas.
 */
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
        // Empieza a quemar recien en el turno siguiente.
    }

    @Override
    public void alIniciarTurno(Combatiente objetivo) {
        int daño = objetivo.getVidaMaxima() * porcentajePorTurno / PORCENTAJE_TOTAL;
        objetivo.recibirDañoDirecto(Math.min(daño, GOLPE_MAXIMO));
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // No deja secuela.
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
