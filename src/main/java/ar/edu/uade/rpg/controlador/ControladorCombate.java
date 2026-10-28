package ar.edu.uade.rpg.controlador;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.MovimientoDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;
import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combate.ResultadoCombate;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Party;

/**
 * Controlador MVC de la pantalla de batalla.
 *
 * El combate no es un while que espera clicks: Swing se congelaria. Cada vez
 * que algo cambia, el controlador le manda el estado a la vista y espera.
 * - Si le toca al jugador, la vista muestra los botones y vuelve con lo que eligio.
 * - Si le toca a un enemigo (o al jugador aturdido), la vista espera un poco y
 *   llama a avanzarTurnoAutomatico(). Asi los ataques enemigos se ven de a uno.
 *
 * La pausa la pone la vista con un javax.swing.Timer: es cuestion de como se
 * ve, y asi este controlador no depende de Swing.
 */
public class ControladorCombate {

    private final Juego juego;
    private VistaDelJuego vista;
    private Combate combate;
    /** Lo que se le ofrecio a la vista para elegir; los indices que vuelven se refieren a esto. */
    private List<Combatiente> objetivosOfrecidos = new ArrayList<>();
    private List<Consumible> consumiblesOfrecidos = new ArrayList<>();

    ControladorCombate(Juego juego) {
        this.juego = juego;
    }

    void setVista(VistaDelJuego vista) {
        this.vista = vista;
    }

    void iniciar() {
        combate = juego.iniciarCombate();
        vista.mostrarBatalla(armarEstado());
    }

    // ------------------------------------------------------ turnos automaticos

    /**
     * El turno de un enemigo, o el de un personaje aturdido que lo pierde. Si
     * llega tarde (la pelea ya termino o le toca elegir al jugador), no hace
     * nada: asi la etapa nunca se cierra dos veces.
     */
    public void avanzarTurnoAutomatico() {
        if (combate == null || combate.estaFinalizado()) {
            return;
        }
        if (combate.hayGanador()) {
            terminar();
            return;
        }
        if (combate.esTurnoDelJugador() && combate.puedeActuarElSiguiente()) {
            return;
        }
        if (!combate.puedeActuarElSiguiente()) {
            combate.perderTurno();
        } else if (!combate.esTurnoDelJugador()) {
            combate.ejecutarTurnoDeEnemigo();
        }
        despuesDeActuar();
    }

    // --------------------------------------------------------- turno del jugador

    /** Los objetivos posibles del movimiento numero indice del que tiene el turno. */
    public List<CombatienteDTO> getObjetivosDeMovimiento(int indiceMovimiento) {
        objetivosOfrecidos = combate.getObjetivosPosibles(movimientoDelTurno(indiceMovimiento));
        return convertir(objetivosOfrecidos);
    }

    /** Un movimiento que apunta a uno solo: el indice viene de getObjetivosDeMovimiento. */
    public void usarMovimiento(int indiceMovimiento, int indiceObjetivo) {
        Movimiento movimiento = movimientoDelTurno(indiceMovimiento);
        combate.ejecutarMovimiento(movimiento, List.of(objetivosOfrecidos.get(indiceObjetivo)));
        despuesDeActuar();
    }

    /** Un movimiento sin eleccion: a todos los enemigos, a toda la party o a uno mismo. */
    public void usarMovimiento(int indiceMovimiento) {
        Movimiento movimiento = movimientoDelTurno(indiceMovimiento);
        combate.ejecutarMovimiento(movimiento, combate.getObjetivosPosibles(movimiento));
        despuesDeActuar();
    }

    public void defender() {
        combate.defender();
        despuesDeActuar();
    }

    /** Sobre quien se puede usar ese consumible del inventario. */
    public List<CombatienteDTO> getObjetivosDeConsumible(int indiceEnElInventario) {
        Party party = juego.getPartida().getParty();
        consumiblesOfrecidos = ConversorDTO.distintosDelInventario(party.getInventario());
        objetivosOfrecidos = combate.getObjetivosDeConsumible(consumiblesOfrecidos.get(indiceEnElInventario));
        return convertir(objetivosOfrecidos);
    }

    /** Los dos indices se refieren a lo ultimo que devolvio getObjetivosDeConsumible. */
    public void usarConsumible(int indiceEnElInventario, int indiceObjetivo) {
        combate.usarConsumible(consumiblesOfrecidos.get(indiceEnElInventario),
                objetivosOfrecidos.get(indiceObjetivo));
        despuesDeActuar();
    }

    // ------------------------------------------------------------------ cierre

    private void despuesDeActuar() {
        if (combate.hayGanador()) {
            terminar();
        } else {
            vista.mostrarBatalla(armarEstado());
        }
    }

    private void terminar() {
        int numeroEtapa = juego.getPartida().getEtapaActual();
        ResultadoCombate resultado = combate.calcularResultado();
        juego.finalizarEtapaDeCombate(resultado);
        vista.mostrarResultado(new ResultadoDTO(
                numeroEtapa,
                resultado.fueVictoria(),
                resultado.getExperienciaGanada(),
                resultado.getOroGanado(),
                !juego.getPartida().estaEnCurso(),
                juego.getPartida().fueGanada(),
                ConversorDTO.miembros(juego.getPartida().getParty())));
    }

    // ------------------------------------------------------------------ armado

    /**
     * El movimiento numero indice del que tiene el turno, contando los 4 aunque
     * alguno no este disponible: asi el indice coincide con el boton.
     */
    private Movimiento movimientoDelTurno(int indice) {
        return combate.getSiguienteEnActuar().getMovimientos().get(indice);
    }

    private EstadoCombateDTO armarEstado() {
        Combatiente actual = combate.getSiguienteEnActuar();
        Party party = juego.getPartida().getParty();

        List<CombatienteDTO> aliados = new ArrayList<>();
        for (Personaje miembro : party.getMiembros()) {
            aliados.add(ConversorDTO.personaje(miembro, miembro == actual));
        }
        List<CombatienteDTO> enemigos = new ArrayList<>();
        for (Combatiente enemigo : combate.getEnemigosVivos()) {
            enemigos.add(ConversorDTO.enemigo(enemigo, enemigo == actual));
        }

        boolean turnoDelJugador = combate.esTurnoDelJugador() && combate.puedeActuarElSiguiente();
        List<MovimientoDTO> movimientos = new ArrayList<>();
        List<ItemDTO> inventario = new ArrayList<>();
        if (turnoDelJugador) {
            List<Movimiento> disponibles = combate.getMovimientosDelTurno();
            for (Movimiento movimiento : actual.getMovimientos()) {
                movimientos.add(ConversorDTO.movimiento(movimiento, actual, disponibles.contains(movimiento)));
            }
            inventario = ConversorDTO.inventario(party.getInventario());
        }

        return new EstadoCombateDTO(
                juego.getPartida().getEtapaActual(),
                juego.getEtapaActual().getDescripcion(),
                combate.getNumeroRonda(),
                aliados, enemigos,
                turnoDelJugador,
                actual == null ? "" : actual.getNombre(),
                movimientos, inventario,
                combate.getRegistro());
    }

    private List<CombatienteDTO> convertir(List<Combatiente> combatientes) {
        List<CombatienteDTO> lista = new ArrayList<>();
        for (Combatiente combatiente : combatientes) {
            lista.add(combatiente instanceof Personaje personaje
                    ? ConversorDTO.personaje(personaje, false)
                    : ConversorDTO.enemigo(combatiente, false));
        }
        return lista;
    }
}
