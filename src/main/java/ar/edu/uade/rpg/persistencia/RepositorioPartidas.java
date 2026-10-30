package ar.edu.uade.rpg.persistencia;

import ar.edu.uade.rpg.modelo.partida.Partida;

public interface RepositorioPartidas {
    void guardar(Partida partida);

    Partida cargar();

    boolean existePartidaGuardada();

    void borrar();
}
