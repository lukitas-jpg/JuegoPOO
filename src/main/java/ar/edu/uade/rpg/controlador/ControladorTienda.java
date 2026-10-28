package ar.edu.uade.rpg.controlador;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.dto.EquipableEnVentaDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Party;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

/**
 * Controlador MVC de las etapas de tienda (4, 9, 14 y 19): comprar
 * consumibles y equipo, cambiar el equipo en venta y seguir.
 */
public class ControladorTienda {

    private final Juego juego;
    private final ControladorJuego controladorJuego;
    private VistaDelJuego vista;

    ControladorTienda(Juego juego, ControladorJuego controladorJuego) {
        this.juego = juego;
        this.controladorJuego = controladorJuego;
    }

    void setVista(VistaDelJuego vista) {
        this.vista = vista;
    }

    void iniciar() {
        vista.mostrarTienda(armarTienda());
    }

    // ------------------------------------------------------------------- tienda

    /** El indice es la posicion en la lista de consumibles de la tienda. */
    public void comprarConsumible(int indice) {
        if (!juego.comprarConsumible(indice)) {
            vista.avisar("No alcanza el oro o ya tenés el máximo de ese objeto.");
        }
        vista.mostrarTienda(armarTienda());
    }

    /** El equipable va al unico miembro de la party que puede usarlo. */
    public void comprarEquipable(int indiceSlot) {
        Personaje destinatario = destinatarioDe(juego.getTienda().getSlot(indiceSlot));
        if (destinatario == null || !juego.puedeComprarEquipable(indiceSlot, destinatario)) {
            vista.avisar("No alcanza el oro para ese equipo.");
        } else {
            juego.comprarEquipable(indiceSlot, destinatario);
        }
        vista.mostrarTienda(armarTienda());
    }

    public void rerollear() {
        if (!juego.rerollearTienda()) {
            vista.avisar("No alcanza el oro o no quedan rerolls.");
        }
        vista.mostrarTienda(armarTienda());
    }

    /** Cierra la etapa (avanza y guarda) y muestra la party. */
    public void salir() {
        juego.finalizarEtapaDeTienda();
        controladorJuego.mostrarEstadoDeLaParty();
    }

    // ------------------------------------------------------------------- armado

    private TiendaDTO armarTienda() {
        Tienda tienda = juego.getTienda();
        Party party = juego.getPartida().getParty();

        List<ItemDTO> consumibles = new ArrayList<>();
        for (Consumible consumible : tienda.getConsumiblesALaVenta()) {
            boolean sePuede = party.puedePagar(consumible.getPrecio())
                    && !party.getInventario().estaLleno(consumible);
            consumibles.add(ConversorDTO.consumible(consumible,
                    party.getInventario().contar(consumible.getNombre()), sePuede));
        }

        List<EquipableEnVentaDTO> slots = new ArrayList<>();
        for (Equipable equipable : tienda.getSlots()) {
            slots.add(armarSlot(equipable, party));
        }

        return new TiendaDTO(juego.getPartida().getEtapaActual(), party.getOro(),
                consumibles, slots, tienda.getCostoDelProximoReroll(),
                tienda.puedeRerollear(party.getOro()), ConversorDTO.miembros(party));
    }

    private EquipableEnVentaDTO armarSlot(Equipable equipable, Party party) {
        if (equipable == null) {
            return new EquipableEnVentaDTO(true, null, "", "", null);
        }
        Personaje destinatario = destinatarioDe(equipable);
        Equipable puesto = destinatario.getEquipadoEn(equipable.getSlot());
        return new EquipableEnVentaDTO(false,
                ConversorDTO.equipable(equipable, party.puedePagar(equipable.getPrecio())),
                ConversorDTO.bonificaciones(equipable),
                destinatario.getNombre(),
                puesto == null ? null : puesto.getNombre());
    }

    /**
     * Cada equipable es de una sola clase y la party no repite clases: hay un
     * solo miembro que lo puede usar. La tienda solo ofrece equipo de la party.
     */
    private Personaje destinatarioDe(Equipable equipable) {
        for (Personaje miembro : juego.getPartida().getParty().getMiembros()) {
            if (equipable.puedeEquiparlo(miembro)) {
                return miembro;
            }
        }
        return null;
    }
}
