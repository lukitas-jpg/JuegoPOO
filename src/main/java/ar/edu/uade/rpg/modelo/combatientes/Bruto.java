package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasDebil;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

public final class Bruto extends Enemigo implements ObjetivoMasDebil {
    public Bruto() {
        super("Bruto");
        vidaMaxima = 70; vidaActual = 70;
        ataqueFisico = 18; defensaFisica = 8; defensaMagica = 5;
        velocidad = 9; suerte = 8;
        agregarMovimiento(new AtaqueFisico("Mazazo", "Un golpe pesado de arriba hacia abajo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 12));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        return usar(getMovimientos().get(0),
                List.of(elegirObjetivo(filtrarVivos(party))), calculador);
    }
}
