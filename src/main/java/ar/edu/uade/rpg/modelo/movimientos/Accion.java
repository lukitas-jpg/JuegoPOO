package ar.edu.uade.rpg.modelo.movimientos;

public interface Accion {
    ResultadoAccion ejecutar();

    String getDescripcion();
}
