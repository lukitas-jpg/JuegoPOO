package ar.edu.uade.rpg.modelo.combate;

import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

public class CalculadorDeDaño {
    public static final int DIVISOR_DE_POTENCIA = 10;

    public static final int CONSTANTE_DE_DEFENSA = 50;

    public static final int VARIACION_MINIMA = 90;
    public static final int VARIACION_MAXIMA = 110;

    public static final int PROBABILIDAD_CRITICA_MAXIMA = 50;

    public static final int MULTIPLICADOR_CRITICO = 150;

    public static final int GOLPE_MINIMO = 1;

    private static final int PORCENTAJE_TOTAL = 100;

    private final Random random;

    public CalculadorDeDaño() {
        this(new Random());
    }

    public CalculadorDeDaño(Random random) {
        this.random = random;
    }

    public ResultadoDaño calcular(Combatiente atacante, Combatiente objetivo,
                                  int potencia, TipoDaño tipo) {
        int daño = potencia * getAtaqueSegunTipo(atacante, tipo) / DIVISOR_DE_POTENCIA;
        daño = aplicarDefensa(daño, objetivo, tipo);
        daño = daño * objetivo.getPorcentajeDeDañoRecibido() / PORCENTAJE_TOTAL;
        daño = aplicarVariacion(daño);

        boolean critico = esCritico(atacante);
        if (critico) {
            daño = daño * MULTIPLICADOR_CRITICO / PORCENTAJE_TOTAL;
        }
        return new ResultadoDaño(Math.max(daño, GOLPE_MINIMO), critico);
    }

    private int getAtaqueSegunTipo(Combatiente atacante, TipoDaño tipo) {
        return tipo == TipoDaño.FISICO
                ? atacante.getAtaqueFisicoEfectivo()
                : atacante.getAtaqueMagicoEfectivo();
    }

    private int aplicarDefensa(int daño, Combatiente objetivo, TipoDaño tipo) {
        int defensa = tipo == TipoDaño.FISICO
                ? objetivo.getDefensaFisicaEfectiva()
                : objetivo.getDefensaMagicaEfectiva();
        return daño * CONSTANTE_DE_DEFENSA / (CONSTANTE_DE_DEFENSA + defensa);
    }

    private int aplicarVariacion(int daño) {
        int porcentaje = VARIACION_MINIMA
                + random.nextInt(VARIACION_MAXIMA - VARIACION_MINIMA + 1);
        return daño * porcentaje / PORCENTAJE_TOTAL;
    }

    private boolean esCritico(Combatiente atacante) {
        int probabilidad = Math.min(atacante.getSuerteEfectiva(), PROBABILIDAD_CRITICA_MAXIMA);
        return random.nextInt(PORCENTAJE_TOTAL) < probabilidad;
    }
}
