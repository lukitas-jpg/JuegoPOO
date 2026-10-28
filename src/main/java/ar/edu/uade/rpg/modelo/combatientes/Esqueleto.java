package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoAleatorio;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

/**
 * Lo invoca el Nigromante. Debil, un solo movimiento, y atado a la vida de
 * quien lo trajo: si cae el Nigromante, el esqueleto se desarma.
 *
 * Esa comprobacion la hace el esqueleto en su propio turno mirando a su
 * invocador, no el Combate avisandole. Asi nadie tiene que acordarse de
 * recorrer la lista buscando huerfanos cuando muere un enemigo.
 */
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

    /** Sin el nigromante no hay magia que lo sostenga: se cae solo. */
    public void alMorirElInvocador() {
        recibirDañoDirecto(getVidaActual());
    }

    /**
     * Los invocados no escalan con la etapa. Si escalaran, a partir de la etapa
     * 15 el Nigromante entraria con cinco enemigos de pleno nivel y la pelea
     * seria imposible.
     */
    @Override
    public void escalarParaEtapa(int numeroEtapa) {
        // a proposito, no hace nada
    }
}
