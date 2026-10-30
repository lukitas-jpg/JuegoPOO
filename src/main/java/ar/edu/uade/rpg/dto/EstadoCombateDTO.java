package ar.edu.uade.rpg.dto;

import java.util.List;

public record EstadoCombateDTO(int numeroEtapa, String descripcionEtapa, int numeroRonda,
                               List<CombatienteDTO> aliados, List<CombatienteDTO> enemigos,
                               boolean turnoDelJugador, String nombreTurnoActual,
                               List<MovimientoDTO> movimientos, List<ItemDTO> inventario,
                               List<String> registro) {
}
