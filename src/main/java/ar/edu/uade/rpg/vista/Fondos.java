package ar.edu.uade.rpg.vista;

/**
 * Que fondo lleva cada etapa. Es puramente visual: el modelo no sabe como se
 * ve una etapa, y por eso esta tabla vive en la vista.
 *
 *   1-3 y 5      mazmorra        6-8 y 10 (minijefe)   cripta
 *   11-13 y 15   cloaca          16-18                 infierno
 *   20 (jefe)    jefe            4, 9, 14 y 19         tienda
 */
final class Fondos {

    private Fondos() {
    }

    static String paraEtapa(int numero) {
        if (numero == 4 || numero == 9 || numero == 14 || numero == 19) {
            return "fondos/tienda";
        }
        if (numero == 20) {
            return "fondos/jefe";
        }
        if (numero <= 5) {
            return "fondos/mazmorra";
        }
        if (numero <= 10) {
            return "fondos/cripta";
        }
        if (numero <= 15) {
            return "fondos/cloaca";
        }
        return "fondos/infierno";
    }
}
