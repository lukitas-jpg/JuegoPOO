package ar.edu.uade.rpg.modelo.tienda;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.items.CatalogoDeConsumibles;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;

/**
 * La tienda de las etapas 4, 9, 14 y 19. Tiene dos mitades distintas:
 *
 * - Un catalogo FIJO de 5 consumibles, siempre los mismos, siempre disponibles.
 * - 3 SLOTS ROTATIVOS de equipables, sorteados por GeneradorDeStock.
 *
 * Los slots NO se refrescan al comprar: el slot comprado queda vacio y la unica
 * forma de ver objetos nuevos es pagar un reroll, que vuelve a sortear los tres.
 *
 * La Tienda NO maneja oro. Dice cuanto cuesta cada cosa y entrega el objeto; la
 * resta la hace quien lleva la cuenta. Asi no necesita conocer a la Partida, y
 * se la puede probar sin armar media partida alrededor.
 */
public class Tienda {

    public static final int CANTIDAD_DE_SLOTS = 3;
    public static final int MAXIMO_DE_REROLLS = 6;

    /** El reroll cuesta 20 x nº de tienda x nº de reroll de esta visita. */
    private static final int COSTO_BASE_DE_REROLL = 20;

    private final int numeroDeTienda;
    private final List<ClaseDePersonaje> clasesDeLaParty;
    private final GeneradorDeStock generador;
    private final List<Equipable> slots = new ArrayList<>();
    private int rerollsHechos;

    public Tienda(int numeroDeTienda, List<ClaseDePersonaje> clasesDeLaParty,
                  GeneradorDeStock generador) {
        this.numeroDeTienda = numeroDeTienda;
        this.clasesDeLaParty = List.copyOf(clasesDeLaParty);
        this.generador = generador;
        this.rerollsHechos = 0;
        slots.addAll(generador.sortearStock(numeroDeTienda, this.clasesDeLaParty, CANTIDAD_DE_SLOTS));
    }

    // ------------------------------------------------------------ consumibles

    /** Siempre los mismos cinco, recien construidos. */
    public List<Consumible> getConsumiblesALaVenta() {
        return CatalogoDeConsumibles.catalogoCompleto();
    }

    // -------------------------------------------------------- slots de equipo

    /** Los 3 slots; un null es un slot ya comprado y todavia sin rerollear. */
    public List<Equipable> getSlots() {
        return new ArrayList<>(slots);
    }

    public Equipable getSlot(int indice) {
        return slots.get(indice);
    }

    /**
     * Entrega el equipable de ese slot y lo deja vacio. Devuelve null si el
     * slot ya estaba vacio.
     */
    public Equipable comprarSlot(int indice) {
        Equipable comprado = slots.get(indice);
        if (comprado != null) {
            slots.set(indice, null);
        }
        return comprado;
    }

    public boolean quedaAlgoEnLosSlots() {
        return slots.stream().anyMatch(equipable -> equipable != null);
    }

    // ---------------------------------------------------------------- reroll

    /**
     * El precio sube dentro de la misma visita y se reinicia en la tienda
     * siguiente. Es lo que impide farmear la tienda hasta que salga lo que uno
     * quiere: en la tienda 1 se llega con 135 de oro, el primer reroll cuesta
     * 20 y diez rerolls costarian 1100.
     */
    public int getCostoDelProximoReroll() {
        return COSTO_BASE_DE_REROLL * numeroDeTienda * (rerollsHechos + 1);
    }

    public boolean puedeRerollear(int oroDisponible) {
        return rerollsHechos < MAXIMO_DE_REROLLS && oroDisponible >= getCostoDelProximoReroll();
    }

    /** Vuelve a sortear los tres slots a la vez. */
    public void rerollear() {
        rerollsHechos++;
        slots.clear();
        slots.addAll(generador.sortearStock(numeroDeTienda, clasesDeLaParty, CANTIDAD_DE_SLOTS));
    }

    public int getRerollsHechos() {
        return rerollsHechos;
    }

    public int getNumeroDeTienda() {
        return numeroDeTienda;
    }
}
