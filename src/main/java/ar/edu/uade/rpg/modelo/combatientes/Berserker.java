package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoMasPeligroso;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

/**
 * Cuanto menos vida le queda, mas fuerte pega: al borde de caer llega al doble
 * de ataque. Apunta al que mas daño hace de la party.
 *
 * Es el mejor ejemplo de polimorfismo de todo el trabajo. No tiene ningun
 * metodo propio que el motor deba conocer: sobrescribe getAtaqueFisicoEfectivo(),
 * que ya existia, y CalculadorDeDaño sigue llamando al mismo getter de siempre
 * sin enterarse de que existe una clase Berserker.
 */
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

    /**
     * Suma hasta un 100% extra, en proporcion a la vida que ya perdio. Con la
     * vida llena pega normal; con el 10% de vida pega un 90% mas fuerte.
     */
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
