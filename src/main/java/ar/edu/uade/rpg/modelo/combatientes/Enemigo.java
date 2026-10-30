package ar.edu.uade.rpg.modelo.combatientes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;

public abstract class Enemigo extends Combatiente {
    public static final int CRECIMIENTO_AGUANTE = 14;

    public static final int CRECIMIENTO_ATAQUE = 10;

    private static final int PORCENTAJE_TOTAL = 100;

    protected final Random random;

    protected Enemigo(String nombre) {
        this(nombre, new Random());
    }

    protected Enemigo(String nombre, Random random) {
        super(nombre);
        this.random = random;
        this.manaMaximo = 0;
        this.manaActual = 0;
        this.ataqueMagico = 0;
    }

    public abstract Accion elegirAccion(List<Combatiente> party, CalculadorDeDaño calculador);

    public Random getRandom() {
        return random;
    }

    protected List<Combatiente> filtrarVivos(List<Combatiente> candidatos) {
        List<Combatiente> vivos = new ArrayList<>();
        for (Combatiente candidato : candidatos) {
            if (candidato.estaVivo()) {
                vivos.add(candidato);
            }
        }
        return vivos;
    }

    protected Accion usar(Movimiento movimiento, List<Combatiente> objetivos,
                          CalculadorDeDaño calculador) {
        return new AccionUsarMovimiento(this, movimiento, objetivos, calculador);
    }

    public void escalarParaEtapa(int numeroEtapa) {
        int aguante = PORCENTAJE_TOTAL + CRECIMIENTO_AGUANTE * (numeroEtapa - 1);
        int ataque = PORCENTAJE_TOTAL + CRECIMIENTO_ATAQUE * (numeroEtapa - 1);

        vidaMaxima = escalar(vidaMaxima, aguante);
        vidaActual = vidaMaxima;
        manaMaximo = escalar(manaMaximo, aguante);
        manaActual = manaMaximo;
        defensaFisica = escalar(defensaFisica, aguante);
        defensaMagica = escalar(defensaMagica, aguante);
        ataqueFisico = escalar(ataqueFisico, ataque);
        ataqueMagico = escalar(ataqueMagico, ataque);
    }

    private static int escalar(int valor, int porcentaje) {
        return valor == 0 ? 0 : Math.max(1, valor * porcentaje / PORCENTAJE_TOTAL);
    }
}
