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

public final class ControladorJuego {
    public static final int TAMAÑO_DE_LA_PARTY = 3;

    private final Juego juego;
    private final ControladorCombate controladorCombate;
    private final ControladorTienda controladorTienda;
    private VistaDelJuego vista;

    private List<Consumible> consumiblesOfrecidos = new ArrayList<>();
    private List<Personaje> objetivosOfrecidos = new ArrayList<>();

    public ControladorJuego(Juego juego) {
        this.juego = juego;
        this.controladorCombate = new ControladorCombate(juego);
        this.controladorTienda = new ControladorTienda(juego, this);
    }

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

    public void volverAlInicio() {
        iniciar();
    }

    void mostrarEstadoDeLaParty() {
        vista.mostrarEstadoDeLaParty(ConversorDTO.party(juego));
    }

    private void jugarEtapaActual() {
        if (juego.getEtapaActual().esDeCombate()) {
            controladorCombate.iniciar();
        } else {
            controladorTienda.iniciar();
        }
    }

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

    public void usarConsumible(int indiceEnElInventario, int indiceDeObjetivo) {
        Consumible consumible = consumiblesOfrecidos.get(indiceEnElInventario);
        Personaje objetivo = objetivosOfrecidos.get(indiceDeObjetivo);
        if (!juego.usarConsumibleFueraDeCombate(consumible, objetivo)) {
            vista.avisar(consumible.getNombre() + " no sirve sobre " + objetivo.getNombre() + ".");
        }
        mostrarEstadoDeLaParty();
    }
}
