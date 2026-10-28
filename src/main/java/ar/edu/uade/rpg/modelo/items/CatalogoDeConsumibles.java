package ar.edu.uade.rpg.modelo.items;

import java.util.List;

/**
 * Los cinco consumibles del juego. El catalogo es FIJO: los cinco estan
 * siempre disponibles en las cuatro tiendas, no rotan y no se sortean.
 *
 * Todos son de rareza COMUN, que quedo reservada para ellos cuando los
 * equipables pasaron a usar solo RARO, EPICO y LEGENDARIO.
 *
 * Cada metodo DEVUELVE UNA INSTANCIA NUEVA. Si entregara siempre la misma,
 * quitar una pocion del inventario sacaria todas, porque serian el mismo
 * objeto repetido.
 */
public final class CatalogoDeConsumibles {

    private CatalogoDeConsumibles() {
    }

    /**
     * Cura el 55% de la vida maxima. Sale barata a proposito: sin Curador en la
     * party, es lo unico que sostiene la run.
     */
    public static Consumible pocionDeVida() {
        return new Consumible("Pocion de Vida",
                "Cura el 55% de la vida maxima del objetivo.",
                25, Rareza.COMUN, new EfectoCuracion(55), 12);
    }

    public static Consumible elixirDeMana() {
        return new Consumible("Elixir de Mana",
                "Restaura el 50% del mana maximo.",
                20, Rareza.COMUN, new EfectoRestaurarMana(50), 8);
    }

    /** Quita los dos unicos efectos que se arrastran de una etapa a la otra. */
    public static Consumible antidoto() {
        return new Consumible("Antidoto",
                "Quita el Veneno y la Quemadura.",
                15, Rareza.COMUN, new EfectoCurarEstado(List.of("Veneno", "Quemadura")), 6);
    }

    /** Lo mas caro del catalogo: es la unica forma de deshacer una muerte. */
    public static Consumible polvoDeRenacer() {
        return new Consumible("Polvo de Renacer",
                "Revive a un caido con el 50% de su vida.",
                120, Rareza.COMUN, new EfectoRevivir(50), 3);
    }

    public static Consumible piedraDeAfilar() {
        return new Consumible("Piedra de Afilar",
                "Sube un 30% el ataque durante 3 turnos.",
                40, Rareza.COMUN, new EfectoBuffTemporal(30, 3), 6);
    }

    /** Uno nuevo de cada uno, en el orden en que los muestra la tienda. */
    public static List<Consumible> catalogoCompleto() {
        return List.of(pocionDeVida(), elixirDeMana(), antidoto(),
                       polvoDeRenacer(), piedraDeAfilar());
    }
}
