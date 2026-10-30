package ar.edu.uade.rpg.dto;

import java.util.List;

public record TiendaDTO(int numeroEtapa, int oro, List<ItemDTO> consumibles,
                        List<EquipableEnVentaDTO> slots, int costoDelReroll,
                        boolean puedeRerollear, List<CombatienteDTO> party) {
}
