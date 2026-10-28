package ar.edu.uade.rpg.controlador;

import java.util.List;

import ar.edu.uade.rpg.dto.ClaseDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.PartyDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;

/**
 * Lo que los controladores le pueden pedir a la vista: que muestre una
 * pantalla con ciertos datos. Nada mas.
 *
 * Es una interfaz del paquete controlador y la implementa la ventana Swing.
 * Asi los controladores no importan nada de la vista ni de Swing (la D de
 * SOLID: dependen de una abstraccion), y se pueden probar con una vista falsa.
 */
public interface VistaDelJuego {

    void mostrarInicio(boolean hayPartidaGuardada);

    void mostrarSeleccionDeParty(List<ClaseDTO> clases, int tamañoDeLaParty);

    void mostrarBatalla(EstadoCombateDTO estado);

    void mostrarResultado(ResultadoDTO resultado);

    void mostrarEstadoDeLaParty(PartyDTO party);

    void mostrarTienda(TiendaDTO tienda);

    /** Un aviso corto: "No alcanza el oro", "Ese objeto no sirve sobre...". */
    void avisar(String mensaje);
}
