package ar.edu.uade.rpg.modelo.partida;

import ar.edu.uade.rpg.modelo.tienda.Tienda;

/**
 * Una etapa de descanso: se compran consumibles y equipo.
 *
 * No da experiencia: es la unica etapa que responde false a otorgaExperiencia().
 */
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
