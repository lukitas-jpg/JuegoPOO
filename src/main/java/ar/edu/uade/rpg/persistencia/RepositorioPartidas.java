package ar.edu.uade.rpg.persistencia;

import ar.edu.uade.rpg.modelo.partida.Partida;

/**
 * Guardar y cargar la partida. Es una INTERFAZ a proposito: ninguna clase del
 * juego sabe que se guarda en un archivo.
 *
 * Protected Variations. Pasar a JSON o a una base de datos es escribir otra
 * implementacion de estos cuatro metodos; ni Juego ni Partida ni nadie del
 * modelo se entera. Es la unica pieza del trabajo que toca el disco, y queda
 * detras de una sola puerta.
 */
public interface RepositorioPartidas {

    void guardar(Partida partida);

    /** Devuelve null si no hay nada guardado o si el archivo no se pudo leer. */
    Partida cargar();

    boolean existePartidaGuardada();

    void borrar();
}
