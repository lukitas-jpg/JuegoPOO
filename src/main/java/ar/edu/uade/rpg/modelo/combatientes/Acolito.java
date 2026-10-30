package ar.edu.uade.rpg.modelo.combatientes;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasDebil;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeCuracion;

public final class Acolito extends Enemigo implements ObjetivoMasDebil {
    private static final int COSTO_PLEGARIA = 10;
    private static final int UMBRAL_PARA_CURAR = 70;

    public Acolito() {
        super("Acolito");
        vidaMaxima = 55; vidaActual = 55;
        manaMaximo = 50; manaActual = 50;
        ataqueFisico = 5; defensaFisica = 7;
        ataqueMagico = 12; defensaMagica = 12;
        velocidad = 10; suerte = 6;
        agregarMovimiento(new HabilidadDeCuracion("Plegaria", "Devuelve vida a un companero.",
                COSTO_PLEGARIA, AlcanceMovimiento.ALIADO, 1, false, 15));
        agregarMovimiento(new AtaqueMagico("Golpe de Baculo", "Cuando no hay a quien curar.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 8));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        Combatiente herido = buscarCompaneroHerido(aliados);
        if (herido != null && tieneManaSuficiente(COSTO_PLEGARIA)) {
            return usar(getMovimientos().get(0), List.of(herido), calculador);
        }
        List<Combatiente> vivos = filtrarVivos(party);
        return usar(getMovimientos().get(1), List.of(vivos.get(0)), calculador);
    }

    private Combatiente buscarCompaneroHerido(List<Enemigo> aliados) {
        List<Combatiente> candidatos = new ArrayList<>();
        for (Enemigo aliado : aliados) {
            if (aliado != this && aliado.estaVivo()) {
                candidatos.add(aliado);
            }
        }
        Combatiente masDebil = elegirObjetivo(candidatos);
        if (masDebil == null) {
            return null;
        }
        int porcentaje = masDebil.getVidaActual() * 100 / masDebil.getVidaMaxima();
        return porcentaje < UMBRAL_PARA_CURAR ? masDebil : null;
    }
}
