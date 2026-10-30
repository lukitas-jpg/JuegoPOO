package ar.edu.uade.rpg.modelo.partida;

import ar.edu.uade.rpg.modelo.tienda.Tienda;

public class EtapaDeTienda extends Etapa {
    private final Tienda tienda;

    public EtapaDeTienda(int numero, Tienda tienda) {
        super(numero);
        this.tienda = tienda;
    }

    public Tienda getTienda() {
        return tienda;
    }

    @Override
    public String getDescripcion() {
        return "Etapa " + numero + ": tienda.";
    }

    @Override
    public boolean otorgaExperiencia() {
        return false;
    }

    @Override
    public boolean esDeCombate() {
        return false;
    }
}
