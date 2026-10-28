package ar.edu.uade.rpg.dto;

import java.util.List;

/** La party entre etapas: miembros, oro, inventario y que etapa viene. */
public record PartyDTO(int proximaEtapa, String descripcionProximaEtapa, int oro,
                       List<CombatienteDTO> miembros, List<ItemDTO> inventario) {
}
