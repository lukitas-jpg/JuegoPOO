package ar.edu.uade.rpg.persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ar.edu.uade.rpg.modelo.partida.Partida;

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
