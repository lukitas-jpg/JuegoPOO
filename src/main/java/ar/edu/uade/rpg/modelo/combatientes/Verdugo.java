package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasPeligroso;
import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

public final class Verdugo extends Enemigo implements ObjetivoMasPeligroso {
    public static final int RESISTENCIA_DE_JEFE = 50;

    public Verdugo() {
        super("Verdugo");
        vidaMaxima = 380; vidaActual = 380;
        ataqueFisico = 50; defensaFisica = 38; defensaMagica = 26;
        velocidad = 24; suerte = 14;
        resistenciaAEstados = RESISTENCIA_DE_JEFE;
        agregarMovimiento(new AtaqueFisico("Hachazo Brutal", "Un hachazo que parte el aire.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 15));
        agregarMovimiento(new HabilidadDeEstado("Mirada Petrificante",
                "Clava la mirada y le saca el turno al objetivo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, true, new Aturdimiento(1), 50));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, CalculadorDeDaño calculador) {
        Combatiente objetivo = elegirObjetivo(filtrarVivos(party));
        int movimiento = getMovimientos().get(1).estaEnCooldown() ? 0 : 1;
        return usar(getMovimientos().get(movimiento), List.of(objetivo), calculador);
    }

    @Override
    public void escalarParaEtapa(int numeroEtapa) {
    }
}
