package ar.edu.uade.rpg.controlador;

import java.util.List;

import ar.edu.uade.rpg.dto.ClaseDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.PartyDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;

public interface VistaDelJuego {
    void mostrarInicio(boolean hayPartidaGuardada);

    void mostrarSeleccionDeParty(List<ClaseDTO> clases, int tamañoDeLaParty);

    void mostrarBatalla(EstadoCombateDTO estado);

    void mostrarResultado(ResultadoDTO resultado);

    void mostrarEstadoDeLaParty(PartyDTO party);

    void mostrarTienda(TiendaDTO tienda);

    void avisar(String mensaje);
}
