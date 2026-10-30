package ar.edu.uade.rpg.dto;

public record EquipableEnVentaDTO(boolean vendido, ItemDTO item, String bonificaciones,
                                  String destinatario, String reemplaza) {
}
