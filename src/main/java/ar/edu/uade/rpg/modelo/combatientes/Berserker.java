package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasPeligroso;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

public final class Berserker extends Enemigo implements ObjetivoMasPeligroso {
    private static final int PORCENTAJE_TOTAL = 100;

    public Berserker() {
        super("Berserker");
        vidaMaxima = 80; vidaActual = 80;
        ataqueFisico = 15; defensaFisica = 6; defensaMagica = 5;
        velocidad = 12; suerte = 10;
        agregarMovimiento(new AtaqueFisico("Embate", "Un golpe cada vez mas desesperado.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 11));
    }

    @Override
    public int getAtaqueFisicoEfectivo() {
        int base = super.getAtaqueFisicoEfectivo();
        int vidaPerdida = PORCENTAJE_TOTAL - (vidaActual * PORCENTAJE_TOTAL / vidaMaxima);
        return base * (PORCENTAJE_TOTAL + vidaPerdida) / PORCENTAJE_TOTAL;
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, CalculadorDeDaño calculador) {
        return usar(getMovimientos().get(0),
                List.of(elegirObjetivo(filtrarVivos(party))), calculador);
    }
}
