package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasDebil;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasPeligroso;
import ar.edu.uade.rpg.modelo.efectos.Quemadura;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

public final class SeñorDeLasSombras extends Enemigo
        implements ObjetivoMasDebil, ObjetivoMasPeligroso {
    public static final int RESISTENCIA_DE_JEFE = 50;

    private static final int FASE_INICIAL = 1;
    private static final int FASE_FINAL = 2;
    private static final int PORCENTAJE_DE_CAMBIO = 50;

    private int fase;

    public SeñorDeLasSombras() {
        super("Señor de las Sombras");
        vidaMaxima = 500; vidaActual = 500;
        ataqueFisico = 52; defensaFisica = 42;
        ataqueMagico = 52; defensaMagica = 42;
        velocidad = 40; suerte = 20;
        resistenciaAEstados = RESISTENCIA_DE_JEFE;
        fase = FASE_INICIAL;
        agregarMovimiento(new AtaqueFisico("Garra de Sombra", "Una garra que sale de abajo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 17));
        agregarMovimiento(new AtaqueMagico("Marea de Sombras", "La oscuridad cubre el campo entero.",
                0, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, 1, false, 12));
        agregarMovimiento(new HabilidadDeEstado("Llamas Sombrias",
                "Prende fuego a toda la party: 10% de su vida maxima por turno, 2 turnos.",
                0, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, 1, false, new Quemadura(10, 2), 40));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        if (debeCambiarDeFase()) {
            cambiarDeFase();
        }
        List<Combatiente> vivos = filtrarVivos(party);
        if (fase == FASE_INICIAL) {
            return usar(getMovimientos().get(0), List.of(elegirObjetivo(vivos)), calculador);
        }
        int movimiento = random.nextBoolean() ? 1 : 2;
        return usar(getMovimientos().get(movimiento), vivos, calculador);
    }

    @Override
    public Combatiente elegirObjetivo(List<Combatiente> candidatos) {
        return fase == FASE_INICIAL
                ? ObjetivoMasDebil.super.elegirObjetivo(candidatos)
                : ObjetivoMasPeligroso.super.elegirObjetivo(candidatos);
    }

    private boolean debeCambiarDeFase() {
        return fase == FASE_INICIAL
                && vidaActual * 100 / vidaMaxima < PORCENTAJE_DE_CAMBIO;
    }

    private void cambiarDeFase() {
        fase = FASE_FINAL;
    }

    public int getFase() {
        return fase;
    }

    @Override
    public void escalarParaEtapa(int numeroEtapa) {
    }
}
