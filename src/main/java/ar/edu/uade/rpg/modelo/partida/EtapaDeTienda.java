package ar.edu.uade.rpg.modelo.partida;

import ar.edu.uade.rpg.modelo.tienda.MaquinaDeSlots;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

public class EtapaDeTienda extends Etapa {
    private final Tienda tienda;
    private final MaquinaDeSlots maquinaDeSlots;

    public EtapaDeTienda(int numero, Tienda tienda, MaquinaDeSlots maquinaDeSlots) {
        super(numero);
        this.tienda = tienda;
        this.maquinaDeSlots = maquinaDeSlots;
    }

    public boolean tieneMaquinaDeSlots() {
        return maquinaDeSlots != null;
    }

    public Tienda getTienda() {
        return tienda;
    }

    public MaquinaDeSlots getMaquinaDeSlots() {
        return maquinaDeSlots;
    }

    public void pasarALaTienda() {
        if (maquinaDeSlots != null) {
            maquinaDeSlots.abandonar();
        }
    }

    @Override
    public String getDescripcion() {
        return tieneMaquinaDeSlots()
                ? "Etapa " + numero + ": maquina de slots y tienda."
                : "Etapa " + numero + ": tienda.";
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
