package ar.edu.uade.rpg.modelo.partida;

import ar.edu.uade.rpg.modelo.tienda.MaquinaDeSlots;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

/**
 * Una etapa de descanso: se compra y, en dos de las cuatro, se puede apostar.
 *
 * La maquina aparece PRIMERO. El jugador elige apostar o pasar a la tienda, y
 * si pasa no puede volver. Ese estado "abandonada" vive aca y no en la maquina,
 * porque es una regla de la etapa: la maquina no sabe que hay una tienda al
 * lado.
 *
 * No da experiencia: es la unica etapa que responde false a otorgaExperiencia().
 */
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

    /** null en las tiendas de las etapas 4 y 14. */
    public MaquinaDeSlots getMaquinaDeSlots() {
        return maquinaDeSlots;
    }

    /** Se abandona la maquina y se pasa a la tienda, sin vuelta atras. */
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
