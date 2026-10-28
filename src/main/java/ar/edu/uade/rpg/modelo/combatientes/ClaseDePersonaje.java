package ar.edu.uade.rpg.modelo.combatientes;

/**
 * Las seis clases jugables, como dato.
 *
 * Existe para que un Equipable pueda decir a que clase pertenece sin guardar un
 * texto ni preguntar por el tipo con instanceof. El Mandoble del Juramento
 * declara GUERRERO y listo: comparar dos valores de enum es seguro y el
 * compilador avisa si alguien escribe mal el nombre.
 */
public enum ClaseDePersonaje {
    GUERRERO,
    MAGO,
    ARQUERO,
    CURADOR,
    ASESINO,
    TANQUE
}
