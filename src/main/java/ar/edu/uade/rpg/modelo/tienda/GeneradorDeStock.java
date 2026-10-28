package ar.edu.uade.rpg.modelo.tienda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.items.CatalogoDeEquipables;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.Rareza;

/**
 * Sortea que equipables ofrece cada tienda. Es una Pure Fabrication: concentra
 * todo el azar de la tienda en un solo lugar, igual que GeneradorDeEtapas hace
 * con las etapas.
 *
 * Dos reglas:
 *
 * La rareza depende de la tienda. LEGENDARIO recien aparece en la tercera, y en
 * la cuarta se deja de ofrecer RARO porque a esa altura es peor que lo que el
 * jugador ya tiene puesto y solo gastaria un slot.
 *
 * El objeto se sortea SOLO entre las clases que estan en la party. Sin ese
 * filtro, con 3 slots y 12 objetos posibles, la mitad de lo que ofrece la
 * tienda no lo podria usar nadie, y como el slot solo cambia al rerollear, el
 * jugador se quedaria mirando objetos inutiles.
 */
public class GeneradorDeStock {

    /** Pesos de RARO, EPICO y LEGENDARIO, por numero de tienda (1 a 4). */
    private static final int[][] PESOS_POR_TIENDA = {
        {100,  0,  0},
        { 60, 40,  0},
        { 15, 60, 25},
        {  0, 50, 50}
    };

    private final Random random;

    public GeneradorDeStock() {
        this(new Random());
    }

    public GeneradorDeStock(Random random) {
        this.random = random;
    }

    /** Un equipable al azar para esa tienda, o null si no hay ninguno posible. */
    public Equipable sortearEquipable(int numeroDeTienda, List<ClaseDePersonaje> clases) {
        Rareza rareza = sortearRareza(numeroDeTienda);
        List<Equipable> posibles = CatalogoDeEquipables.filtrar(clases, rareza);
        return posibles.isEmpty() ? null : posibles.get(random.nextInt(posibles.size()));
    }

    public List<Equipable> sortearStock(int numeroDeTienda, List<ClaseDePersonaje> clases,
                                        int cantidad) {
        List<Equipable> stock = new ArrayList<>();
        for (int slot = 0; slot < cantidad; slot++) {
            stock.add(sortearEquipable(numeroDeTienda, clases));
        }
        return stock;
    }

    public Rareza sortearRareza(int numeroDeTienda) {
        int[] pesos = PESOS_POR_TIENDA[numeroDeTienda - 1];
        int tirada = random.nextInt(pesos[0] + pesos[1] + pesos[2]);
        if (tirada < pesos[0]) {
            return Rareza.RARO;
        }
        if (tirada < pesos[0] + pesos[1]) {
            return Rareza.EPICO;
        }
        return Rareza.LEGENDARIO;
    }

    public static int[] getPesos(int numeroDeTienda) {
        return PESOS_POR_TIENDA[numeroDeTienda - 1].clone();
    }
}
