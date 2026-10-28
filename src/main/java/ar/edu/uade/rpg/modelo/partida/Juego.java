package ar.edu.uade.rpg.modelo.partida;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combate.ResultadoCombate;
import ar.edu.uade.rpg.modelo.combatientes.Arquero;
import ar.edu.uade.rpg.modelo.combatientes.Asesino;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Curador;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.tienda.Tienda;
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

    /**
     * Creator de los personajes. Es publico porque la pantalla de seleccion
     * tambien lo usa: muestra cada clase a partir de un personaje recien creado,
     * asi la descripcion y las estadisticas salen de la misma clase y no se
     * repiten en ningun otro lado.
     */
    public static Personaje crearPersonaje(ClaseDePersonaje clase) {
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

    // --------------------------------------------------------------- combate

    /** Arma la pelea de la etapa actual con los miembros vivos de la party. */
    public Combate iniciarCombate() {
        Combate combate = getEtapaDeCombate().crearCombate(partida.getParty());
        combate.iniciar();
        return combate;
    }

    // ------------------------------------------------------------------ tienda

    /*
     * La Tienda no maneja oro: dice cuanto cuesta y entrega. La cuenta la lleva la Party. Juego es quien junta las dos
     * cosas, como Controller: cobra, entrega y deja todo consistente en un paso.
     */

    /** La pantalla lo usa para habilitar el boton de comprar. */
    public boolean puedeComprarEquipable(int indiceSlot, Personaje destinatario) {
        Equipable equipable = getTienda().getSlot(indiceSlot);
        return equipable != null
                && equipable.puedeEquiparlo(destinatario)
                && partida.getParty().puedePagar(equipable.getPrecio());
    }

    /**
     * Cobra el equipable, lo saca del slot y se lo pone al destinatario.
     * Devuelve lo que tenia puesto en ese lugar (se descarta), o null.
     */
    public Equipable comprarEquipable(int indiceSlot, Personaje destinatario) {
        if (!puedeComprarEquipable(indiceSlot, destinatario)) {
            throw new IllegalStateException("No se puede comprar el slot " + indiceSlot
                    + " para " + destinatario.getNombre());
        }
        Tienda tienda = getTienda();
        partida.getParty().gastarOro(tienda.getSlot(indiceSlot).getPrecio());
        return destinatario.equipar(tienda.comprarSlot(indiceSlot));
    }

    /**
     * Compra una unidad del consumible que esta en esa posicion del catalogo.
     * Devuelve false si no alcanza el oro o si ya se llego al tope.
     */
    public boolean comprarConsumible(int indiceEnElCatalogo) {
        Consumible consumible = getTienda().getConsumiblesALaVenta().get(indiceEnElCatalogo);
        Party party = partida.getParty();
        if (!party.puedePagar(consumible.getPrecio()) || party.getInventario().estaLleno(consumible)) {
            return false;
        }
        party.gastarOro(consumible.getPrecio());
        party.getInventario().agregar(consumible);
        return true;
    }

    /** Vuelve a sortear los 3 slots, si alcanza el oro y quedan rerolls. */
    public boolean rerollearTienda() {
        Tienda tienda = getTienda();
        Party party = partida.getParty();
        if (!tienda.puedeRerollear(party.getOro())) {
            return false;
        }
        party.gastarOro(tienda.getCostoDelProximoReroll());
        tienda.rerollear();
        return true;
    }

    // ------------------------------------------------------------- inventario

    /**
     * Usar un consumible entre etapas (en combate se usa con una Accion, porque
     * gasta el turno). Devuelve false si no se puede usar sobre ese personaje.
     */
    public boolean usarConsumibleFueraDeCombate(Consumible consumible, Personaje objetivo) {
        if (!consumible.puedeUsarseSobre(objetivo)) {
            return false;
        }
        consumible.usarSobre(objetivo);
        partida.getParty().getInventario().quitar(consumible);
        return true;
    }

    // ----------------------------------------------------------- tipo de etapa

    /*
     * Los dos unicos casts del modelo, y los dos despues de preguntar. La etapa
     * se pide por su tipo porque combate y tienda tienen operaciones distintas;
     * mejor que el cast viva aca, en un solo lugar, que en cada pantalla.
     */

    private EtapaDeCombate getEtapaDeCombate() {
        if (!etapaActual.esDeCombate()) {
            throw new IllegalStateException("La etapa " + etapaActual.getNumero() + " no es de combate");
        }
        return (EtapaDeCombate) etapaActual;
    }

    private EtapaDeTienda getEtapaDeTienda() {
        if (etapaActual.esDeCombate()) {
            throw new IllegalStateException("La etapa " + etapaActual.getNumero() + " no es de tienda");
        }
        return (EtapaDeTienda) etapaActual;
    }

    /** La tienda de la etapa actual, para que la pantalla la muestre. */
    public Tienda getTienda() {
        return getEtapaDeTienda().getTienda();
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
