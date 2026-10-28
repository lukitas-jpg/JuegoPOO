package ar.edu.uade.rpg.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ar.edu.uade.rpg.modelo.partida.Partida;

/**
 * Guarda la partida en un archivo con la serializacion de Java.
 *
 * Se eligio serializacion y no un formato de texto porque la Partida arrastra
 * un grafo entero de objetos: la Party, los tres personajes con su nivel y su
 * equipo, los efectos activos, el inventario. Escribir eso a mano en JSON
 * seria un mapeador de varias clases que no agrega nada al trabajo practico.
 *
 * El guardado es AUTOMATICO al terminar cada etapa, asi que si el juego se
 * cierra no se pierde mas que la etapa en curso.
 */
public class RepositorioPartidasArchivo implements RepositorioPartidas {

    public static final String RUTA_POR_DEFECTO = "saves/partida.dat";

    private final String rutaArchivo;

    public RepositorioPartidasArchivo() {
        this(RUTA_POR_DEFECTO);
    }

    public RepositorioPartidasArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void guardar(Partida partida) {
        File archivo = new File(rutaArchivo);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (ObjectOutputStream salida =
                     new ObjectOutputStream(new FileOutputStream(archivo))) {
            salida.writeObject(partida);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la partida en " + rutaArchivo, e);
        }
    }

    /**
     * Devuelve null en vez de tirar una excepcion si no hay nada: no poder
     * cargar es un caso normal (la primera vez que alguien abre el juego), no
     * un error.
     */
    @Override
    public Partida cargar() {
        if (!existePartidaGuardada()) {
            return null;
        }
        try (ObjectInputStream entrada =
                     new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (Partida) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean existePartidaGuardada() {
        File archivo = new File(rutaArchivo);
        return archivo.exists() && archivo.length() > 0;
    }

    @Override
    public void borrar() {
        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            archivo.delete();
        }
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
