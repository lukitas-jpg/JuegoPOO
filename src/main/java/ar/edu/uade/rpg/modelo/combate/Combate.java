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
    /** Si al que tiene el turno ya se le aviso que arranca (iniciarTurno). */
    private boolean turnoPreparado;
    /** Quien tiene el turno ahora. Se guarda porque puede caer en su propia accion. */
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

    /**
     * Prepara la pelea y arranca la primera ronda: a cada uno se le llena el
     * mana y se le reinician las definitivas.
     */
    public void iniciar() {
        for (Combatiente participante : gestor.getParticipantes()) {
            participante.alIniciarCombate();
        }
        gestor.iniciarNuevaRonda();
        registro.add("Comienza el combate.");
    }

    // ---------------------------------------------------------------- turnos

    /**
     * Quien tiene el turno. La primera vez que se pregunta por un turno nuevo
     * se le avisa que arranca (iniciarTurno: baja la guardia y los cooldowns).
     * Se hace una sola vez por turno aunque se pregunte muchas veces, y vale
     * tambien para el primero de cada ronda. Devuelve null si la pelea termino.
     */
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

    /** False si el que tiene el turno esta aturdido: ese turno se pierde. */
    public boolean puedeActuarElSiguiente() {
        Combatiente siguiente = getSiguienteEnActuar();
        return siguiente != null && siguiente.puedeActuar();
    }

    /** El que tiene el turno no puede actuar. Vale igual para personajes y enemigos. */
    public void perderTurno() {
        registro.add(getSiguienteEnActuar().getNombre() + " esta aturdido y pierde el turno.");
        avanzarTurno();
        retirarCaidos();
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

    /** El que tiene el turno se pone en guardia. */
    public ResultadoAccion defender() {
        return ejecutarAccion(new AccionDefender(getSiguienteEnActuar()));
    }

    /**
     * El que tiene el turno usa un consumible del inventario de la party sobre
     * un aliado. Gasta el turno igual que atacar.
     */
    public ResultadoAccion usarConsumible(Consumible consumible, Combatiente objetivo) {
        Combatiente actor = getSiguienteEnActuar();
        boolean estabaCaido = !objetivo.estaVivo();
        ResultadoAccion resultado = ejecutarAccion(
                new AccionUsarConsumible(actor, consumible, objetivo, party.getInventario()));
        if (estabaCaido && objetivo.estaVivo()) {
            // Un revivido vuelve a la pelea: actua desde la ronda siguiente.
            gestor.incorporar(objetivo);
            registro.add(objetivo.getNombre() + " vuelve al combate.");
        }
        return resultado;
    }

    /** Sobre quien se puede usar: el Polvo de Renacer solo sobre caidos, el resto sobre vivos. */
    public List<Combatiente> getObjetivosDeConsumible(Consumible consumible) {
        List<Combatiente> objetivos = new ArrayList<>();
        for (Personaje miembro : party.getMiembros()) {
            if (consumible.puedeUsarseSobre(miembro)) {
                objetivos.add(miembro);
            }
        }
        return objetivos;
    }

    /**
     * El unico camino por el que pasa todo lo que se hace en un turno. Primero
     * se asegura de que el turno este abierto, asi el cierre (avanzarTurno) se
     * le aplica a quien corresponde aunque la Accion se haya armado afuera.
     */
    public ResultadoAccion ejecutarAccion(Accion accion) {
        getSiguienteEnActuar();
        ResultadoAccion resultado = accion.ejecutar();
        registro.add(resultado.getDescripcion());

        avanzarTurno();
        retirarCaidos();
        return resultado;
    }

    /** El turno de un enemigo: el se decide solo. */
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

    /**
     * Cierra el turno del que actuaba y pasa al siguiente. El cierre procesa
     * sus efectos: el veneno pega y el aturdimiento se descuenta recien cuando
     * ya hizo perder el turno. Se consume el turno ANTES de retirar a los
     * caidos, asi el indice de la ronda no se corre (ver GestorDeTurnos.retirar).
     */
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

    // ------------------------------------------------------------- resultado

    public boolean hayGanador() {
        return party.estaDerrotada() || getEnemigosVivos().isEmpty();
    }

    /**
     * La recompensa la define la ETAPA, no los enemigos: la etapa se la paso a
     * este combate al crearlo.
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
