package ar.edu.uade.rpg.modelo.combate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.movimientos.ResultadoAccion;
import ar.edu.uade.rpg.modelo.partida.Party;

/**
 * El motor de una pelea. Coordina: no calcula daño (eso es CalculadorDeDaño),
 * no ordena turnos (eso es GestorDeTurnos) y no decide que hace cada enemigo
 * (eso es Enemigo.elegirAccion). Junta las piezas y lleva el registro.
 *
 * No pregunta nunca de que tipo es nadie para decidir que hacer: le pide una
 * Accion a quien tenga el turno y la ejecuta. El jugador arma la suya desde la
 * pantalla y el enemigo la arma solo; Combate no los distingue.
 *
 * Vive en el MODELO: no tiene un solo metodo que muestre, pinte o reciba
 * clicks.
 */
public class Combate {

    private final Party party;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final GestorDeTurnos gestor;
    private final CalculadorDeDaño calculador;
    private final List<String> registro = new ArrayList<>();
    private final int experienciaDeLaEtapa;
    private final int oroDeLaEtapa;
    private boolean finalizado;

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

        List<Combatiente> todos = new ArrayList<>(party.getMiembrosVivos());
        todos.addAll(enemigos);
        this.gestor = new GestorDeTurnos(todos, random);
    }

    /**
     * Prepara la pelea y arranca la primera ronda. Aca es donde el Nigromante
     * mete sus cinco esqueletos antes de que nadie actue.
     */
    public void iniciar() {
        notificarInicioDeCombate();
        gestor.iniciarNuevaRonda();
        registro.add("Comienza el combate.");
    }

    /**
     * Le avisa a cada uno que empieza el combate. Los que devuelven invocados
     * los suman al campo. Nadie llama a Combate desde afuera: la informacion
     * sube y la decision la toma el combate.
     */
    private void notificarInicioDeCombate() {
        List<Combatiente> presentes = new ArrayList<>(gestor.getParticipantes());
        for (Combatiente presente : presentes) {
            for (Combatiente invocado : presente.alIniciarCombate()) {
                agregarCombatiente(invocado);
                registro.add(presente.getNombre() + " invoca a " + invocado.getNombre() + ".");
            }
        }
    }

    // ---------------------------------------------------------------- turnos

    public Combatiente getSiguienteEnActuar() {
        if (gestor.rondaTerminada()) {
            gestor.iniciarNuevaRonda();
        }
        return gestor.getSiguienteEnActuar();
    }

    public boolean esTurnoDelJugador() {
        return getSiguienteEnActuar() instanceof Personaje;
    }

    /** Los movimientos que la pantalla tiene que mostrar habilitados. */
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

    // -------------------------------------------------------------- acciones

    /**
     * Creator: el combate arma la Accion porque es quien tiene los datos (quien
     * actua, que movimiento y sobre que objetivos).
     */
    public ResultadoAccion ejecutarMovimiento(Movimiento movimiento, List<Combatiente> objetivos) {
        Combatiente actor = getSiguienteEnActuar();
        return ejecutarAccion(new AccionUsarMovimiento(actor, movimiento, objetivos, calculador));
    }

    /** El unico camino por el que pasa todo lo que se hace en un turno. */
    public ResultadoAccion ejecutarAccion(Accion accion) {
        ResultadoAccion resultado = accion.ejecutar();
        registro.add(resultado.getDescripcion());

        incorporarInvocados(resultado);
        retirarCaidos();
        avanzarTurno();
        return resultado;
    }

    /** El turno de un enemigo: el se decide solo. */
    public ResultadoAccion ejecutarTurnoDeEnemigo() {
        Combatiente actor = getSiguienteEnActuar();
        if (!(actor instanceof Enemigo enemigo)) {
            return null;
        }
        if (!enemigo.puedeActuar()) {
            registro.add(enemigo.getNombre() + " pierde el turno.");
            avanzarTurno();
            return null;
        }
        Accion accion = enemigo.elegirAccion(
                new ArrayList<>(party.getMiembrosVivos()), getAliadosVivosDe(enemigo), calculador);
        if (accion == null) {
            // Cubrir aliados o desarmarse tambien gasta el turno.
            registro.add(enemigo.getNombre() + " no ataca este turno.");
            retirarCaidos();
            avanzarTurno();
            return null;
        }
        return ejecutarAccion(accion);
    }

    private void avanzarTurno() {
        gestor.consumirTurno();
        Combatiente siguiente = gestor.getSiguienteEnActuar();
        if (siguiente != null) {
            siguiente.procesarEfectosDeTurno();
            siguiente.reducirCooldowns();
        }
    }

    private void incorporarInvocados(ResultadoAccion resultado) {
        for (Combatiente invocado : resultado.getCombatientesInvocados()) {
            agregarCombatiente(invocado);
        }
    }

    public void agregarCombatiente(Combatiente combatiente) {
        if (combatiente instanceof Enemigo enemigo && !enemigos.contains(enemigo)) {
            enemigos.add(enemigo);
        }
        gestor.incorporar(combatiente);
    }

    public void retirarCaidos() {
        for (Combatiente participante : gestor.getParticipantes()) {
            if (!participante.estaVivo()) {
                gestor.retirar(participante);
                registro.add(participante.getNombre() + " cae.");
            }
        }
    }

    // ------------------------------------------------------------- resultado

    public boolean hayGanador() {
        return party.estaDerrotada() || getEnemigosVivos().isEmpty();
    }

    /**
     * La recompensa la define la ETAPA, no los enemigos. Si viniera por
     * enemigo, el Nigromante seria una fuente infinita de experiencia y oro.
     */
    public ResultadoCombate calcularResultado() {
        finalizado = true;
        boolean victoria = !party.estaDerrotada();
        return new ResultadoCombate(
                victoria,
                victoria ? experienciaDeLaEtapa : 0,
                victoria ? oroDeLaEtapa : 0,
                registro);
    }

    /** Los companeros vivos de un enemigo: los necesita el Guardian y el Acolito. */
    private List<Enemigo> getAliadosVivosDe(Enemigo enemigo) {
        List<Enemigo> aliados = new ArrayList<>();
        for (Enemigo otro : enemigos) {
            if (otro.estaVivo()) {
                aliados.add(otro);
            }
        }
        return aliados;
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
