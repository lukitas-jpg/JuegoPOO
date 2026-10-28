package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;

/**
 * Daño magico sobre TODA la party a la vez. Es el unico enemigo que no
 * implementa ninguna interfaz de apuntado, y no por olvido: su ataque alcanza a
 * los tres, asi que no hay ningun objetivo que elegir.
 *
 * Es tambien el unico, junto al Acolito, que gasta mana. Cuando se queda seco
 * pasa a un ataque simple, asi que conviene aguantarlo en vez de apurarse.
 */
public final class Hechicero extends Enemigo {

    private static final int COSTO_ONDA = 12;

    public Hechicero() {
        super("Hechicero");
        vidaMaxima = 50; vidaActual = 50;
        manaMaximo = 60; manaActual = 60;
        ataqueFisico = 4; defensaFisica = 6;
        ataqueMagico = 17; defensaMagica = 12;
        velocidad = 11; suerte = 8;
        agregarMovimiento(new AtaqueMagico("Onda Oscura", "Una ola de sombra que barre el campo.",
                COSTO_ONDA, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, 1, false, 9));
        agregarMovimiento(new AtaqueMagico("Chispa", "Lo poco que le queda cuando se le acaba la energia.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 9));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        List<Combatiente> vivos = filtrarVivos(party);
        if (tieneManaSuficiente(COSTO_ONDA)) {
            return usar(getMovimientos().get(0), vivos, calculador);
        }
        return usar(getMovimientos().get(1), List.of(vivos.get(0)), calculador);
    }
}
