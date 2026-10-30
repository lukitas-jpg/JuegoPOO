package ar.edu.uade.rpg.modelo.tienda;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.items.CatalogoDeConsumibles;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;

public class Tienda {
    public static final int CANTIDAD_DE_SLOTS = 3;
    public static final int MAXIMO_DE_REROLLS = 6;

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

    public List<Consumible> getConsumiblesALaVenta() {
        return CatalogoDeConsumibles.catalogoCompleto();
    }

    public List<Equipable> getSlots() {
        return new ArrayList<>(slots);
    }

    public Equipable getSlot(int indice) {
        return slots.get(indice);
    }

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

    public int getCostoDelProximoReroll() {
        return COSTO_BASE_DE_REROLL * numeroDeTienda * (rerollsHechos + 1);
    }

    public boolean puedeRerollear(int oroDisponible) {
        return rerollsHechos < MAXIMO_DE_REROLLS && oroDisponible >= getCostoDelProximoReroll();
    }

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
