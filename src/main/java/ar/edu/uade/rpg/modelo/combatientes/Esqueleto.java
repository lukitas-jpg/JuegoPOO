package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoAleatorio;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

public final class Esqueleto extends Enemigo implements ObjetivoAleatorio {
    private final Nigromante invocador;

    public Esqueleto(Nigromante invocador, Random random) {
        super("Esqueleto", random);
        this.invocador = invocador;
        vidaMaxima = 30; vidaActual = 30;
        ataqueFisico = 11; defensaFisica = 5; defensaMagica = 4;
        velocidad = 12; suerte = 5;
        agregarMovimiento(new AtaqueFisico("Zarpazo", "Un manotazo de huesos.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 7));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        if (invocador != null && !invocador.estaVivo()) {
            alMorirElInvocador();
            return null;
        }
        return usar(getMovimientos().get(0),
                List.of(elegirObjetivo(filtrarVivos(party))), calculador);
    }

    public void alMorirElInvocador() {
        recibirDañoDirecto(getVidaActual());
    }

    @Override
    public void escalarParaEtapa(int numeroEtapa) {
    }
}
