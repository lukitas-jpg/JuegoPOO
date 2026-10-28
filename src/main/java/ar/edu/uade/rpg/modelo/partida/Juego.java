package ar.edu.uade.rpg.modelo.partida;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.ResultadoCombate;
import ar.edu.uade.rpg.modelo.combatientes.Arquero;
import ar.edu.uade.rpg.modelo.combatientes.Asesino;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Curador;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.persistencia.RepositorioPartidas;
import ar.edu.uade.rpg.persistencia.RepositorioPartidasArchivo;

/**
 * El Controller del GRASP: recibe los eventos del sistema (nueva run, terminar
 * etapa, guardar) y coordina delegando. No resuelve la logica el mismo.
 *
 * Vive en el MODELO. No conoce ninguna pantalla ni nada de la interfaz: no
 * tiene un solo metodo que muestre, pinte o reciba clicks. La vista le va a
 * pedir datos y a avisarle eventos, nunca al reves.
 *
 * Tampoco sabe que se guarda en un archivo: habla con la interfaz
 * RepositorioPartidas.
 */
public class Juego {

    private final RepositorioPartidas repositorio;
    private final GeneradorDeEtapas generador;
    private Partida partida;
    private Etapa etapaActual;

    public Juego() {
        this(new RepositorioPartidasArchivo(), new GeneradorDeEtapas());
    }

    public Juego(RepositorioPartidas repositorio, GeneradorDeEtapas generador) {
        this.repositorio = repositorio;
        this.generador = generador;
    }

    // ------------------------------------------------------ empezar o seguir

    public boolean hayPartidaGuardada() {
        return repositorio.existePartidaGuardada();
    }

    /**
     * Arma la party con las clases elegidas y genera la etapa 1. Las instancias
     * de Personaje se crean aca y mueren con la run: por eso la relacion con
     * Party es de composicion.
     */
    public Partida iniciarNuevaRun(List<ClaseDePersonaje> clasesElegidas) {
        List<Personaje> miembros = new ArrayList<>();
        for (ClaseDePersonaje clase : clasesElegidas) {
            miembros.add(crearPersonaje(clase));
        }
        partida = new Partida(new Party(miembros));
        generarEtapaActual();
        guardarAutomaticamente();
        return partida;
    }

    private static Personaje crearPersonaje(ClaseDePersonaje clase) {
        return switch (clase) {
            case GUERRERO -> new Guerrero("Guerrero");
            case MAGO -> new Mago("Mago");
            case ARQUERO -> new Arquero("Arquero");
            case CURADOR -> new Curador("Curador");
            case ASESINO -> new Asesino("Asesino");
            case TANQUE -> new Tanque("Tanque");
        };
    }

    /**
     * La etapa NO se guarda en el archivo: se vuelve a generar a partir del
     * numero. Asi el guardado es chico y no arrastra enemigos ni tiendas.
     */
    public Partida cargarPartida() {
        partida = repositorio.cargar();
        if (partida != null) {
            generarEtapaActual();
        }
        return partida;
    }

    private void generarEtapaActual() {
        etapaActual = generador.crearEtapa(partida.getEtapaActual(), getClasesDeLaParty());
    }

    private List<ClaseDePersonaje> getClasesDeLaParty() {
        List<ClaseDePersonaje> clases = new ArrayList<>();
        for (Personaje miembro : partida.getParty().getMiembros()) {
            clases.add(miembro.getClaseDePersonaje());
        }
        return clases;
    }

    public Etapa getEtapaActual() {
        return etapaActual;
    }

    // --------------------------------------------------------- cerrar etapa

    /**
     * Cierra una etapa de pelea. Si se perdio, la run termina; si se gano,
     * reparte las recompensas, limpia lo temporal y avanza.
     *
     * No pregunta de que tipo es la etapa: para eso estan otorgaExperiencia() y
     * esDeCombate(), que cada subclase responde por si misma.
     */
    public void finalizarEtapaDeCombate(ResultadoCombate resultado) {
        if (!resultado.fueVictoria()) {
            finalizarPorDerrota();
            return;
        }
        Party party = partida.getParty();
        if (etapaActual.otorgaExperiencia()) {
            party.repartirExperiencia(resultado.getExperienciaGanada());
        }
        party.agregarOro(resultado.getOroGanado());
        party.limpiarEfectosTemporales();
        avanzarYGuardar();
    }

    /** Cierra una etapa de tienda. No hay nada que repartir ni que limpiar. */
    public void finalizarEtapaDeTienda() {
        avanzarYGuardar();
    }

    private void avanzarYGuardar() {
        etapaActual.marcarCompletada();
        partida.avanzarEtapa();
        if (partida.estaEnCurso()) {
            generarEtapaActual();
            guardarAutomaticamente();
        } else {
            // Ganada: la run termino, no hay nada que retomar.
            repositorio.borrar();
        }
    }

    public void finalizarPorDerrota() {
        partida.terminarPorDerrota();
        repositorio.borrar();
    }

    public void guardarAutomaticamente() {
        repositorio.guardar(partida);
    }

    public EstadoPartida getEstadoPartida() {
        return partida.getEstado();
    }

    public Partida getPartida() {
        return partida;
    }
}
