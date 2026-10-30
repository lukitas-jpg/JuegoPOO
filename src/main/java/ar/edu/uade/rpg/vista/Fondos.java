package ar.edu.uade.rpg.vista;

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
