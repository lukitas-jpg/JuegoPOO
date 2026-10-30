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

    public boolean hayPartidaGuardada() {
        return repositorio.existePartidaGuardada();
    }

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

    public Combate iniciarCombate() {
        Combate combate = getEtapaDeCombate().crearCombate(partida.getParty());
        combate.iniciar();
        return combate;
    }

    public boolean puedeComprarEquipable(int indiceSlot, Personaje destinatario) {
        Equipable equipable = getTienda().getSlot(indiceSlot);
        return equipable != null
                && equipable.puedeEquiparlo(destinatario)
                && partida.getParty().puedePagar(equipable.getPrecio());
    }

    public Equipable comprarEquipable(int indiceSlot, Personaje destinatario) {
        if (!puedeComprarEquipable(indiceSlot, destinatario)) {
            throw new IllegalStateException("No se puede comprar el slot " + indiceSlot
                    + " para " + destinatario.getNombre());
        }
        Tienda tienda = getTienda();
        partida.getParty().gastarOro(tienda.getSlot(indiceSlot).getPrecio());
        return destinatario.equipar(tienda.comprarSlot(indiceSlot));
    }

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

    public boolean usarConsumibleFueraDeCombate(Consumible consumible, Personaje objetivo) {
        if (!consumible.puedeUsarseSobre(objetivo)) {
            return false;
        }
        consumible.usarSobre(objetivo);
        partida.getParty().getInventario().quitar(consumible);
        return true;
    }

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

    public Tienda getTienda() {
        return getEtapaDeTienda().getTienda();
    }

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
