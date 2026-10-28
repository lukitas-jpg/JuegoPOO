package ar.edu.uade.rpg.controlador;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.dto.ClaseDTO;
import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Party;

/**
 * Controlador MVC principal: el inicio, la seleccion de la party, el estado de
 * la party entre etapas y el paso de una etapa a la siguiente.
 *
 * No confundir con Juego, que es el Controller de GRASP dentro del modelo.
 * Este traduce: recibe clicks con indices, le habla a Juego y le manda DTOs a
 * la vista.
 *
 * Flujo: Inicio -> Seleccion -> Batalla -> Resultado -> Estado de la party ->
 * (Batalla o Tienda) -> Estado de la party -> ...
 */
public final class ControladorJuego {

    public static final int TAMAÑO_DE_LA_PARTY = 3;

    private final Juego juego;
    private final ControladorCombate controladorCombate;
    private final ControladorTienda controladorTienda;
    private VistaDelJuego vista;
    /** Lo que se le ofrecio a la vista para elegir; los indices que vuelven se refieren a esto. */
    private List<Consumible> consumiblesOfrecidos = new ArrayList<>();
    private List<Personaje> objetivosOfrecidos = new ArrayList<>();

    public ControladorJuego(Juego juego) {
        this.juego = juego;
        this.controladorCombate = new ControladorCombate(juego);
        this.controladorTienda = new ControladorTienda(juego, this);
    }

    /** La vista se conecta despues de crearse, porque ella tambien necesita al controlador. */
    public void setVista(VistaDelJuego vista) {
        this.vista = vista;
        controladorCombate.setVista(vista);
        controladorTienda.setVista(vista);
    }

    public ControladorCombate getControladorCombate() {
        return controladorCombate;
    }

    public ControladorTienda getControladorTienda() {
        return controladorTienda;
    }

    // ------------------------------------------------------------------ inicio

    public void iniciar() {
        vista.mostrarInicio(juego.hayPartidaGuardada());
    }

    public void nuevaPartida() {
        List<ClaseDTO> clases = new ArrayList<>();
        for (ClaseDePersonaje clase : ClaseDePersonaje.values()) {
            Personaje muestra = Juego.crearPersonaje(clase);
            clases.add(new ClaseDTO(muestra.getNombre(),
                    "personajes/" + ConversorDTO.nombreDeArchivo(clase.name()),
                    muestra.getDescripcionClase(),
                    muestra.getVidaMaxima(), muestra.getManaMaximo(), muestra.getVelocidadEfectiva()));
        }
        vista.mostrarSeleccionDeParty(clases, TAMAÑO_DE_LA_PARTY);
    }

    /** Los indices son posiciones en la lista de clases que se mostro. */
    public void confirmarParty(List<Integer> indicesDeClases) {
        if (indicesDeClases.size() != TAMAÑO_DE_LA_PARTY) {
            vista.avisar("Elegí " + TAMAÑO_DE_LA_PARTY + " personajes.");
            return;
        }
        List<ClaseDePersonaje> elegidas = new ArrayList<>();
        for (int indice : indicesDeClases) {
            elegidas.add(ClaseDePersonaje.values()[indice]);
        }
        juego.iniciarNuevaRun(elegidas);
        jugarEtapaActual();
    }

    public void cargarPartida() {
        if (juego.cargarPartida() == null) {
            vista.avisar("No hay ninguna partida guardada.");
            iniciar();
            return;
        }
        mostrarEstadoDeLaParty();
    }

    // ----------------------------------------------------------- entre etapas

    /** Despues del resultado de una pelea: si la run termino, al inicio. */
    public void continuar() {
        if (!juego.getPartida().estaEnCurso()) {
            iniciar();
            return;
        }
        mostrarEstadoDeLaParty();
    }

    public void siguienteEtapa() {
        jugarEtapaActual();
    }

    /** La partida ya esta guardada desde que termino la etapa anterior. */
    public void volverAlInicio() {
        iniciar();
    }

    void mostrarEstadoDeLaParty() {
        vista.mostrarEstadoDeLaParty(ConversorDTO.party(juego));
    }

    /** Sin cast: Juego sabe armar la pelea o mostrar la tienda de la etapa actual. */
    private void jugarEtapaActual() {
        if (juego.getEtapaActual().esDeCombate()) {
            controladorCombate.iniciar();
        } else {
            controladorTienda.iniciar();
        }
    }

    // ------------------------------------------------- inventario entre etapas

    /**
     * Los miembros sobre los que se puede usar ese consumible del inventario
     * (el Polvo de Renacer solo sobre caidos, el resto sobre vivos).
     */
    public List<CombatienteDTO> getObjetivosDeConsumible(int indiceEnElInventario) {
        Party party = juego.getPartida().getParty();
        consumiblesOfrecidos = ConversorDTO.distintosDelInventario(party.getInventario());
        Consumible consumible = consumiblesOfrecidos.get(indiceEnElInventario);
        objetivosOfrecidos = new ArrayList<>();
        List<CombatienteDTO> lista = new ArrayList<>();
        for (Personaje miembro : party.getMiembros()) {
            if (consumible.puedeUsarseSobre(miembro)) {
                objetivosOfrecidos.add(miembro);
                lista.add(ConversorDTO.personaje(miembro, false));
            }
        }
        return lista;
    }

    /** Los dos indices se refieren a lo ultimo que devolvio getObjetivosDeConsumible. */
    public void usarConsumible(int indiceEnElInventario, int indiceDeObjetivo) {
        Consumible consumible = consumiblesOfrecidos.get(indiceEnElInventario);
        Personaje objetivo = objetivosOfrecidos.get(indiceDeObjetivo);
        if (!juego.usarConsumibleFueraDeCombate(consumible, objetivo)) {
            vista.avisar(consumible.getNombre() + " no sirve sobre " + objetivo.getNombre() + ".");
        }
        mostrarEstadoDeLaParty();
    }
}
