package ar.edu.uade.rpg.dto;

import java.util.List;

/**
 * Todo lo que muestra la pantalla de batalla en un momento. Si turnoDelJugador
 * es false, la pantalla espera un poco y le pide al controlador el turno
 * siguiente: asi se ven los ataques enemigos de a uno.
 */
public record EstadoCombateDTO(int numeroEtapa, String descripcionEtapa, int numeroRonda,
                               List<CombatienteDTO> aliados, List<CombatienteDTO> enemigos,
                               boolean turnoDelJugador, String nombreTurnoActual,
                               List<MovimientoDTO> movimientos, List<ItemDTO> inventario,
                               List<String> registro) {
}
