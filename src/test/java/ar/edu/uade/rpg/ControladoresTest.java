package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.controlador.VistaDelJuego;
import ar.edu.uade.rpg.dto.ClaseDTO;
import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.MovimientoDTO;
import ar.edu.uade.rpg.dto.PartyDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;
import ar.edu.uade.rpg.modelo.partida.GeneradorDeEtapas;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.persistencia.RepositorioPartidasArchivo;

/**
 * Los controladores probados sin Swing: una vista falsa anota que pantalla se
 * le pidio y con que datos, y el test "hace click" como lo haria un jugador.
 *
 * Es posible porque los controladores dependen de la interfaz VistaDelJuego y
 * no de la ventana.
 */
class ControladoresTest {

    /** Guarda lo ultimo que se le pidio mostrar. */
    private static final class VistaFalsa implements VistaDelJuego {
        String pantalla = "";
        final List<String> recorrido = new ArrayList<>();
        final List<String> avisos = new ArrayList<>();
        List<ClaseDTO> clases;
        EstadoCombateDTO batalla;
        ResultadoDTO resultado;
        PartyDTO party;
        TiendaDTO tienda;
        boolean hayGuardada;

        private void ir(String nueva) {
            pantalla = nueva;
            if (recorrido.isEmpty() || !recorrido.get(recorrido.size() - 1).equals(nueva)) {
                recorrido.add(nueva);
            }
        }

        @Override public void mostrarInicio(boolean guardada) { hayGuardada = guardada; ir("inicio"); }
        @Override public void mostrarSeleccionDeParty(List<ClaseDTO> c, int t) { clases = c; ir("seleccion"); }
        @Override public void mostrarBatalla(EstadoCombateDTO e) { batalla = e; ir("batalla"); }
        @Override public void mostrarResultado(ResultadoDTO r) { resultado = r; ir("resultado"); }
        @Override public void mostrarEstadoDeLaParty(PartyDTO p) { party = p; ir("estado"); }
        @Override public void mostrarTienda(TiendaDTO t) { tienda = t; ir("tienda"); }
        @Override public void avisar(String mensaje) { avisos.add(mensaje); }
    }

    private VistaFalsa vista;
    private ControladorJuego controlador;

    private void armar(long semilla) throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(semilla)));
        controlador = new ControladorJuego(juego);
        vista = new VistaFalsa();
        controlador.setVista(vista);
        controlador.iniciar();
    }

    @Test
    void laSeleccionMuestraLasSeisClasesConSuDibujo() throws Exception {
        armar(1);
        controlador.nuevaPartida();

        assertEquals("seleccion", vista.pantalla);
        assertEquals(6, vista.clases.size());
        assertEquals("personajes/guerrero", vista.clases.get(0).imagen());
    }

    @Test
    void conMenosDeTresPersonajesNoArranca() throws Exception {
        armar(1);
        controlador.nuevaPartida();

        controlador.confirmarParty(List.of(0, 1));

        assertEquals("seleccion", vista.pantalla);
        assertFalse(vista.avisos.isEmpty());
    }

    @Test
    void elegirLaPartyLlevaDirectoALaPrimeraBatalla() throws Exception {
        armar(1);
        controlador.nuevaPartida();
        controlador.confirmarParty(List.of(0, 1, 3));

        assertEquals("batalla", vista.pantalla);
        assertEquals(1, vista.batalla.numeroEtapa());
        assertEquals(3, vista.batalla.aliados().size());
        assertTrue(vista.batalla.enemigos().get(0).imagen().startsWith("enemigos/"));
    }

    @Test
    void cuandoLeTocaAlJugadorVienenSusCuatroMovimientos() throws Exception {
        armar(1);
        controlador.nuevaPartida();
        controlador.confirmarParty(List.of(0, 1, 3));
        avanzarHastaElTurnoDelJugador();

        List<MovimientoDTO> movimientos = vista.batalla.movimientos();
        assertEquals(4, movimientos.size());
        assertTrue(movimientos.get(0).disponible(), "el basico siempre se puede usar");
        assertFalse(movimientos.get(3).disponible(), "la definitiva se desbloquea en nivel 8");
        assertEquals("Nivel 8", movimientos.get(3).motivo());
    }

    @Test
    void sePuedeSalirAlInicioYSeguirLaPartidaGuardada() throws Exception {
        armar(1);
        controlador.nuevaPartida();
        controlador.confirmarParty(List.of(0, 1, 3));
        while (!vista.pantalla.equals("resultado")) {
            jugarUnPaso();
        }
        controlador.continuar();
        controlador.volverAlInicio();
        assertTrue(vista.hayGuardada);

        controlador.cargarPartida();

        assertEquals("estado", vista.pantalla);
        assertEquals(2, vista.party.proximaEtapa());
    }

    @Test
    void unTurnoAutomaticoQueLlegaTardeNoHaceNada() throws Exception {
        armar(1);
        controlador.nuevaPartida();
        controlador.confirmarParty(List.of(0, 1, 3));
        while (!vista.pantalla.equals("resultado")) {
            jugarUnPaso();
        }
        int etapaDespuesDeGanar = vista.resultado.numeroEtapa();

        // El Timer de la pantalla dispara de nuevo despues de que termino la pelea.
        controlador.getControladorCombate().avanzarTurnoAutomatico();
        controlador.continuar();

        assertEquals(etapaDespuesDeGanar + 1, vista.party.proximaEtapa(), "la etapa se cerro una sola vez");
    }

    /**
     * Partidas enteras desde los controladores: pelear, cobrar, ver la party,
     * comprar y usar pociones, hasta volver al inicio.
     */
    @Test
    void unaRunEnteraSeJuegaDesdeLosControladores() throws Exception {
        boolean pasoPorUnaTienda = false;
        for (long semilla = 1; semilla <= 4; semilla++) {
            armar(semilla);
            controlador.nuevaPartida();
            controlador.confirmarParty(List.of(0, 1, 3));
            int pasos = 0;
            while (!vista.pantalla.equals("inicio")) {
                jugarUnPaso();
                pasos++;
                assertTrue(pasos < 20_000, "la run se trabo en la pantalla " + vista.pantalla);
            }
            assertTrue(vista.recorrido.containsAll(List.of("batalla", "resultado", "estado")));
            pasoPorUnaTienda |= vista.recorrido.contains("tienda");
            assertFalse(vista.hayGuardada, "una run terminada no deja partida guardada");
        }
        assertTrue(pasoPorUnaTienda, "alguna de las runs deberia llegar a la etapa 4");
    }

    // ---------------------------------------------------------- el "jugador"

    private void avanzarHastaElTurnoDelJugador() {
        while (vista.pantalla.equals("batalla") && !vista.batalla.turnoDelJugador()) {
            controlador.getControladorCombate().avanzarTurnoAutomatico();
        }
    }

    /** Un click, segun la pantalla en la que este. */
    private void jugarUnPaso() {
        switch (vista.pantalla) {
            case "batalla" -> jugarEnLaBatalla();
            case "resultado" -> controlador.continuar();
            case "estado" -> {
                curarAlMasHerido();
                controlador.siguienteEtapa();
            }
            case "tienda" -> comprarYSalir();
            default -> throw new AssertionError("pantalla inesperada: " + vista.pantalla);
        }
    }

    private void jugarEnLaBatalla() {
        if (!vista.batalla.turnoDelJugador()) {
            controlador.getControladorCombate().avanzarTurnoAutomatico();
            return;
        }
        List<MovimientoDTO> movimientos = vista.batalla.movimientos();
        int elegido = 0;
        for (int i = 0; i < movimientos.size(); i++) {
            if (movimientos.get(i).disponible()) {
                elegido = i;
            }
        }
        if (movimientos.get(elegido).eligeObjetivo()) {
            controlador.getControladorCombate().getObjetivosDeMovimiento(elegido);
            controlador.getControladorCombate().usarMovimiento(elegido, 0);
        } else {
            controlador.getControladorCombate().usarMovimiento(elegido);
        }
    }

    private void curarAlMasHerido() {
        if (vista.party.inventario().isEmpty()) {
            return;
        }
        List<CombatienteDTO> objetivos = controlador.getObjetivosDeConsumible(0);
        if (!objetivos.isEmpty()) {
            controlador.usarConsumible(0, 0);
        }
    }

    private void comprarYSalir() {
        for (int i = 0; i < 3; i++) {
            controlador.getControladorTienda().comprarConsumible(0);
        }
        if (!vista.tienda.slots().get(0).vendido() && vista.tienda.slots().get(0).item().disponible()) {
            controlador.getControladorTienda().comprarEquipable(0);
        }
        controlador.getControladorTienda().salir();
    }
}
