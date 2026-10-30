package ar.edu.uade.rpg.modelo.tienda;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.items.CatalogoDeEquipables;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.Rareza;

public class GeneradorDeStock {
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

    public Equipable sortearEquipable(int numeroDeTienda, List<ClaseDePersonaje> clases) {
        return sortearEquipable(numeroDeTienda, clases, List.of());
    }

    public List<Equipable> sortearStock(int numeroDeTienda, List<ClaseDePersonaje> clases,
                                        int cantidad) {
        List<Equipable> stock = new ArrayList<>();
        for (int slot = 0; slot < cantidad; slot++) {
            stock.add(sortearEquipable(numeroDeTienda, clases, stock));
        }
        return stock;
    }

    private Equipable sortearEquipable(int numeroDeTienda, List<ClaseDePersonaje> clases,
                                       List<Equipable> yaOfrecidos) {
        Rareza rareza = sortearRareza(numeroDeTienda);
        List<Equipable> posibles = CatalogoDeEquipables.filtrar(clases, rareza);
        List<Equipable> nuevos = new ArrayList<>();
        for (Equipable posible : posibles) {
            if (!estaOfrecido(posible, yaOfrecidos)) {
                nuevos.add(posible);
            }
        }
        List<Equipable> deDondeElegir = nuevos.isEmpty() ? posibles : nuevos;
        return deDondeElegir.isEmpty() ? null : deDondeElegir.get(random.nextInt(deDondeElegir.size()));
    }

    private static boolean estaOfrecido(Equipable equipable, List<Equipable> yaOfrecidos) {
        for (Equipable ofrecido : yaOfrecidos) {
            if (ofrecido != null && ofrecido.getNombre().equals(equipable.getNombre())) {
                return true;
            }
        }
        return false;
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
