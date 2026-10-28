package ar.edu.uade.rpg.dto;

import java.util.List;

/** La tienda: los 5 consumibles fijos, los 3 slots de equipo y el reroll. */
public record TiendaDTO(int numeroEtapa, int oro, List<ItemDTO> consumibles,
                        List<EquipableEnVentaDTO> slots, int costoDelReroll,
                        boolean puedeRerollear, List<CombatienteDTO> party) {
}
