package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Daño por turno proporcional a la vida maxima del envenenado. No pasa por
 * defensas ni por escudos, asi que es la forma mas confiable de sacarle vida a
 * algo con mucha defensa.
 *
 * Tiene tope por una razon concreta de balance: el 6% de un jefe de 800 de
 * vida serian 48 por turno sin defensa que valga, mas que la definitiva mas
 * fuerte del juego y por una cuarta parte del mana. Con el tope en 25 sigue
 * siendo buena contra los jefes pero no los trivializa.
 *
 * Es uno de los dos efectos que se arrastran de una etapa a la otra.
 */
public class Veneno extends EfectoEstado {

    public static final int GOLPE_MINIMO = 2;
    public static final int GOLPE_MAXIMO = 25;

    private static final int PORCENTAJE_TOTAL = 100;

    private final int porcentajePorTurno;

    public Veneno(int porcentajePorTurno, int duracionEnTurnos) {
        super("Veneno", duracionEnTurnos);
        this.porcentajePorTurno = porcentajePorTurno;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
        // El veneno no hace nada al entrar: empieza a doler en el turno siguiente.
    }

    @Override
    public void alIniciarTurno(Combatiente objetivo) {
        objetivo.recibirDañoDirecto(calcularDaño(objetivo));
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // Se va sin dejar rastro.
    }

    /**
     * El porcentaje se calcula sobre la vida maxima y no sobre la actual: si
     * fuera sobre la actual, el veneno pegaria cada vez menos y nunca mataria.
     */
    private int calcularDaño(Combatiente objetivo) {
        int daño = objetivo.getVidaMaxima() * porcentajePorTurno / PORCENTAJE_TOTAL;
        return Math.min(Math.max(daño, GOLPE_MINIMO), GOLPE_MAXIMO);
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return true;
    }

    @Override
    public EfectoEstado copiar() {
        return new Veneno(porcentajePorTurno, duracionEnTurnos);
    }

    public int getPorcentajePorTurno() {
        return porcentajePorTurno;
    }
}
