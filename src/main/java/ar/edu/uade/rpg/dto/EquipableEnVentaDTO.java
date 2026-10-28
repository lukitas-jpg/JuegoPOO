package ar.edu.uade.rpg.dto;

/**
 * Un slot de equipo de la tienda. Si ya se compro, vendido es true y el resto
 * no importa. destinatario es quien lo puede usar y reemplaza es lo que ese
 * personaje tiene puesto ahora en ese lugar (null si nada): la pantalla avisa
 * que se va a descartar.
 */
public record EquipableEnVentaDTO(boolean vendido, ItemDTO item, String bonificaciones,
                                  String destinatario, String reemplaza) {
}
