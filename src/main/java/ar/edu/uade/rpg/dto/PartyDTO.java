package ar.edu.uade.rpg.dto;

import java.util.List;

public record PartyDTO(int proximaEtapa, String descripcionProximaEtapa, int oro,
                       List<CombatienteDTO> miembros, List<ItemDTO> inventario) {
}
