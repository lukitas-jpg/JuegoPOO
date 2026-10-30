package ar.edu.uade.rpg.modelo.combate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AccionDefender;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarConsumible;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.movimientos.ResultadoAccion;
import ar.edu.uade.rpg.modelo.partida.Party;

public class Combate {
    private final Party party;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final GestorDeTurnos gestor;
    private final CalculadorDeDaño calculador;
    private final List<String> registro = new ArrayList<>();
    private final int experienciaDeLaEtapa;
    private final int oroDeLaEtapa;
    private boolean finalizado;

    private boolean turnoPreparado;

    private Combatiente actorDelTurno;

    public Combate(Party party, List<Enemigo> enemigos,
                   int experienciaDeLaEtapa, int oroDeLaEtapa) {
        this(party, enemigos, experienciaDeLaEtapa, oroDeLaEtapa, new Random());
    }

    public Combate(Party party, List<Enemigo> enemigos,
                   int experienciaDeLaEtapa, int oroDeLaEtapa, Random random) {
        this.party = party;
        this.enemigos.addAll(enemigos);
        this.experienciaDeLaEtapa = experienciaDeLaEtapa;
        this.oroDeLaEtapa = oroDeLaEtapa;
        this.calculador = new CalculadorDeDaño(random);
        this.finalizado = false;
        this.turnoPreparado = false;

        List<Combatiente> todos = new ArrayList<>(party.getMiembrosVivos());
        todos.addAll(enemigos);
        this.gestor = new GestorDeTurnos(todos, random);
    }

    public void iniciar() {
        for (Combatiente participante : gestor.getParticipantes()) {
            participante.alIniciarCombate();
        }
        gestor.iniciarNuevaRonda();
        registro.add("Comienza el combate.");
    }

    public Combatiente getSiguienteEnActuar() {
        if (hayGanador()) {
            return null;
        }
        if (gestor.rondaTerminada()) {
            gestor.iniciarNuevaRonda();
            turnoPreparado = false;
        }
        Combatiente siguiente = gestor.getSiguienteEnActuar();
        if (!turnoPreparado) {
            siguiente.iniciarTurno();
            turnoPreparado = true;
            actorDelTurno = siguiente;
        }
        return siguiente;
    }

    public boolean puedeActuarElSiguiente() {
        Combatiente siguiente = getSiguienteEnActuar();
        return siguiente != null && siguiente.puedeActuar();
    }

    public void perderTurno() {
        registro.add(getSiguienteEnActuar().getNombre() + " esta aturdido y pierde el turno.");
        avanzarTurno();
        retirarCaidos();
    }

    public boolean esTurnoDelJugador() {
        return getSiguienteEnActuar() instanceof Personaje;
    }

    public List<Movimiento> getMovimientosDelTurno() {
        Combatiente actor = getSiguienteEnActuar();
        List<Movimiento> disponibles = new ArrayList<>();
        if (!(actor instanceof Personaje personaje)) {
            return disponibles;
        }
        for (Movimiento movimiento : personaje.getMovimientos()) {
            if (movimiento.estaDesbloqueado(personaje) && movimiento.estaDisponible(personaje)) {
                disponibles.add(movimiento);
            }
        }
        return disponibles;
    }

    public List<Combatiente> getObjetivosPosibles(Movimiento movimiento) {
        Combatiente actor = getSiguienteEnActuar();
        List<Combatiente> aliados = new ArrayList<>();
        List<Combatiente> contrarios = new ArrayList<>();
        if (actor instanceof Personaje) {
            aliados.addAll(party.getMiembrosVivos());
            contrarios.addAll(getEnemigosVivos());
        } else {
            aliados.addAll(getEnemigosVivos());
            contrarios.addAll(party.getMiembrosVivos());
        }
        return movimiento.getObjetivosPosibles(aliados, contrarios);
    }

    public ResultadoAccion ejecutarMovimiento(Movimiento movimiento, List<Combatiente> objetivos) {
        Combatiente actor = getSiguienteEnActuar();
        return ejecutarAccion(new AccionUsarMovimiento(actor, movimiento, objetivos, calculador));
    }

    public ResultadoAccion defender() {
        return ejecutarAccion(new AccionDefender(getSiguienteEnActuar()));
    }

    public ResultadoAccion usarConsumible(Consumible consumible, Combatiente objetivo) {
        Combatiente actor = getSiguienteEnActuar();
        boolean estabaCaido = !objetivo.estaVivo();
        ResultadoAccion resultado = ejecutarAccion(
                new AccionUsarConsumible(actor, consumible, objetivo, party.getInventario()));
        if (estabaCaido && objetivo.estaVivo()) {
            gestor.incorporar(objetivo);
            registro.add(objetivo.getNombre() + " vuelve al combate.");
        }
        return resultado;
    }

    public List<Combatiente> getObjetivosDeConsumible(Consumible consumible) {
        List<Combatiente> objetivos = new ArrayList<>();
        for (Personaje miembro : party.getMiembros()) {
            if (consumible.puedeUsarseSobre(miembro)) {
                objetivos.add(miembro);
            }
        }
        return objetivos;
    }

    public ResultadoAccion ejecutarAccion(Accion accion) {
        getSiguienteEnActuar();
        ResultadoAccion resultado = accion.ejecutar();
        registro.add(resultado.getDescripcion());

        avanzarTurno();
        retirarCaidos();
        return resultado;
    }

    public ResultadoAccion ejecutarTurnoDeEnemigo() {
        Combatiente actor = getSiguienteEnActuar();
        if (!(actor instanceof Enemigo enemigo)) {
            return null;
        }
        if (!enemigo.puedeActuar()) {
            perderTurno();
            return null;
        }
        Accion accion = enemigo.elegirAccion(new ArrayList<>(party.getMiembrosVivos()), calculador);
        return ejecutarAccion(accion);
    }

    private void avanzarTurno() {
        if (actorDelTurno != null) {
            actorDelTurno.procesarEfectosDeTurno();
        }
        gestor.consumirTurno();
        turnoPreparado = false;
    }

    public void retirarCaidos() {
        for (Combatiente participante : gestor.getParticipantes()) {
            if (!participante.estaVivo()) {
                gestor.retirar(participante);
                registro.add(participante.getNombre() + " cae.");
            }
        }
    }

    public boolean hayGanador() {
        return party.estaDerrotada() || getEnemigosVivos().isEmpty();
    }

    public ResultadoCombate calcularResultado() {
        finalizado = true;
        boolean victoria = !party.estaDerrotada();
        return new ResultadoCombate(
                victoria,
                victoria ? experienciaDeLaEtapa : 0,
                victoria ? oroDeLaEtapa : 0,
                registro);
    }

    public List<Combatiente> getEnemigosVivos() {
        List<Combatiente> vivos = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                vivos.add(enemigo);
            }
        }
        return vivos;
    }

    public List<String> getRegistro() {
        return List.copyOf(registro);
    }

    public boolean estaFinalizado() {
        return finalizado;
    }

    public int getNumeroRonda() {
        return gestor.getNumeroRonda();
    }

    public Party getParty() {
        return party;
    }
}
