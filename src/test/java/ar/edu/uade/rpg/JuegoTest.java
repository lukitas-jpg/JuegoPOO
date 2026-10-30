package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.items.CatalogoDeConsumibles;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.partida.EstadoPartida;
import ar.edu.uade.rpg.modelo.partida.GeneradorDeEtapas;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Party;
import ar.edu.uade.rpg.persistencia.RepositorioPartidasArchivo;
import ar.edu.uade.rpg.modelo.tienda.MaquinaDeSlots;
import ar.edu.uade.rpg.modelo.tienda.ResultadoTirada;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

class JuegoTest {
    private static final List<ClaseDePersonaje> TRES_CLASES =
            List.of(ClaseDePersonaje.GUERRERO, ClaseDePersonaje.MAGO, ClaseDePersonaje.CURADOR);

    private Juego juegoNuevo(long semilla) throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(semilla)));
        juego.iniciarNuevaRun(TRES_CLASES);
        return juego;
    }

    private Juego juegoEnLaEtapa(int numero) throws Exception {
        Juego juego = juegoNuevo(1);
        while (juego.getPartida().getEtapaActual() < numero) {
            if (juego.getEtapaActual().esDeCombate()) {
                juego.finalizarEtapaDeCombate(
                        new ar.edu.uade.rpg.modelo.combate.ResultadoCombate(true, 0, 0, List.of()));
            } else {
                juego.finalizarEtapaDeTienda();
            }
        }
        return juego;
    }

    private Party party(Juego juego) {
        return juego.getPartida().getParty();
    }

    @Test
    void comprarUnConsumibleCobraYLoGuardaEnElInventario() throws Exception {
        Juego juego = juegoEnLaEtapa(4);
        party(juego).agregarOro(100);
        Consumible pocion = juego.getTienda().getConsumiblesALaVenta().get(0);

        assertTrue(juego.comprarConsumible(0));

        assertEquals(100 - pocion.getPrecio(), party(juego).getOro());
        assertEquals(1, party(juego).getInventario().contar(pocion.getNombre()));
    }

    @Test
    void sinOroNoSeCompraYNoSeCobra() throws Exception {
        Juego juego = juegoEnLaEtapa(4);

        assertFalse(juego.comprarConsumible(0));
        assertEquals(0, party(juego).getOro());
        assertTrue(party(juego).getInventario().estaVacio());
    }

    @Test
    void elTopeDelInventarioFrenaLaCompraSinCobrar() throws Exception {
        Juego juego = juegoEnLaEtapa(4);
        party(juego).agregarOro(10_000);
        Consumible pocion = juego.getTienda().getConsumiblesALaVenta().get(0);
        for (int i = 0; i < pocion.getTopeEnInventario(); i++) {
            assertTrue(juego.comprarConsumible(0));
        }
        int oroAntes = party(juego).getOro();

        assertFalse(juego.comprarConsumible(0));
        assertEquals(oroAntes, party(juego).getOro());
    }

    @Test
    void comprarUnEquipableCobraLoEquipaYVaciaElSlot() throws Exception {
        Juego juego = juegoEnLaEtapa(4);
        party(juego).agregarOro(10_000);
        Tienda tienda = juego.getTienda();
        Equipable enVenta = tienda.getSlot(0);
        Personaje destinatario = quienPuedeUsar(juego, enVenta);
        int oroAntes = party(juego).getOro();

        Equipable descartado = juego.comprarEquipable(0, destinatario);

        assertNull(descartado, "no tenia nada puesto en ese lugar");
        assertSame(enVenta, destinatario.getEquipadoEn(enVenta.getSlot()));
        assertEquals(oroAntes - enVenta.getPrecio(), party(juego).getOro());
        assertNull(tienda.getSlot(0), "el slot comprado queda vacio");
    }

    @Test
    void noSePuedeComprarParaUnPersonajeDeOtraClase() throws Exception {
        Juego juego = juegoEnLaEtapa(4);
        party(juego).agregarOro(10_000);
        Equipable enVenta = juego.getTienda().getSlot(0);
        Personaje otro = null;
        for (Personaje miembro : party(juego).getMiembros()) {
            if (!enVenta.puedeEquiparlo(miembro)) {
                otro = miembro;
            }
        }
        Personaje deOtraClase = otro;

        assertFalse(juego.puedeComprarEquipable(0, deOtraClase));
        assertThrows(IllegalStateException.class, () -> juego.comprarEquipable(0, deOtraClase));
    }

    @Test
    void elRerollCobraYVuelveASortear() throws Exception {
        Juego juego = juegoEnLaEtapa(4);
        party(juego).agregarOro(100);
        int costo = juego.getTienda().getCostoDelProximoReroll();

        assertTrue(juego.rerollearTienda());

        assertEquals(100 - costo, party(juego).getOro());
        assertEquals(1, juego.getTienda().getRerollsHechos());
    }

    private Personaje quienPuedeUsar(Juego juego, Equipable equipable) {
        for (Personaje miembro : party(juego).getMiembros()) {
            if (equipable.puedeEquiparlo(miembro)) {
                return miembro;
            }
        }
        throw new AssertionError("la tienda solo ofrece equipo de la party");
    }

    @Test
    void laPrimeraTiradaEsGratisYLaSegundaCuestaLaApuesta() throws Exception {
        Juego juego = juegoEnLaEtapa(9);
        party(juego).agregarOro(100);

        ResultadoTirada primera = juego.tirarEnLaMaquina();
        assertTrue(primera.fueGratis());
        assertEquals(100 + primera.getOroGanado(), party(juego).getOro());

        int oroAntes = party(juego).getOro();
        ResultadoTirada segunda = juego.tirarEnLaMaquina();
        assertFalse(segunda.fueGratis());
        assertEquals(oroAntes - MaquinaDeSlots.APUESTA + segunda.getOroGanado(), party(juego).getOro());
    }

    @Test
    void despuesDePasarALaTiendaNoSePuedeVolverATirar() throws Exception {
        Juego juego = juegoEnLaEtapa(9);
        party(juego).agregarOro(100);

        juego.pasarALaTienda();

        assertThrows(IllegalStateException.class, juego::tirarEnLaMaquina);
    }

    @Test
    void laTiendaDeLaEtapa4NoTieneMaquina() throws Exception {
        assertNull(juegoEnLaEtapa(4).getMaquinaDeSlots());
    }

    @Test
    void unaPocionFueraDeCombateCuraYSeGasta() throws Exception {
        Juego juego = juegoNuevo(1);
        Personaje herido = party(juego).getMiembros().get(0);
        herido.recibirDaño(50, TipoDaño.FISICO);
        int vidaHerido = herido.getVidaActual();
        Consumible pocion = CatalogoDeConsumibles.pocionDeVida();
        party(juego).getInventario().agregar(pocion);

        assertTrue(juego.usarConsumibleFueraDeCombate(pocion, herido));

        assertTrue(herido.getVidaActual() > vidaHerido);
        assertTrue(party(juego).getInventario().estaVacio());
    }

    @Test
    void elPolvoDeRenacerNoSeGastaSobreAlguienVivo() throws Exception {
        Juego juego = juegoNuevo(1);
        Personaje vivo = party(juego).getMiembros().get(0);
        Consumible polvo = CatalogoDeConsumibles.polvoDeRenacer();
        party(juego).getInventario().agregar(polvo);

        assertFalse(juego.usarConsumibleFueraDeCombate(polvo, vivo));
        assertFalse(party(juego).getInventario().estaVacio());
    }

    @Test
    void unRevividoEnCombateVuelveAActuar() throws Exception {
        Juego juego = juegoNuevo(1);
        Combate combate = juego.iniciarCombate();
        Personaje caido = party(juego).getMiembros().get(2);
        caido.recibirDaño(10_000, TipoDaño.FISICO);
        combate.retirarCaidos();
        Consumible polvo = CatalogoDeConsumibles.polvoDeRenacer();
        party(juego).getInventario().agregar(polvo);

        while (!combate.esTurnoDelJugador()) {
            combate.defender();
        }
        assertEquals(List.of(caido), combate.getObjetivosDeConsumible(polvo));
        combate.usarConsumible(polvo, caido);
        assertTrue(caido.estaVivo());

        boolean actuo = false;
        for (int i = 0; i < 30 && !actuo; i++) {
            actuo = combate.getSiguienteEnActuar() == caido;
            if (combate.puedeActuarElSiguiente()) {
                combate.defender();
            } else {
                combate.perderTurno();
            }
        }
        assertTrue(actuo, "el revivido tiene que volver a tener turno");
    }

    @Test
    void unaRunConPeleasDeVerdadSiempreTermina() throws Exception {
        for (long semilla = 1; semilla <= 5; semilla++) {
            Juego juego = juegoNuevo(semilla);
            int etapas = 0;
            while (juego.getPartida().estaEnCurso()) {
                etapas++;
                if (juego.getEtapaActual().esDeCombate()) {
                    Combate combate = juego.iniciarCombate();
                    int turnos = 0;
                    while (!combate.hayGanador()) {
                        jugarUnTurno(combate);
                        turnos++;
                        assertTrue(turnos < 2_000, "la pelea de la etapa "
                                + juego.getPartida().getEtapaActual() + " no termina");
                    }
                    juego.finalizarEtapaDeCombate(combate.calcularResultado());
                } else {
                    while (juego.comprarConsumible(0)) {
                    }
                    juego.finalizarEtapaDeTienda();
                }
            }
            assertNotEquals(EstadoPartida.EN_CURSO, juego.getEstadoPartida());
            assertTrue(etapas >= 1 && etapas <= 20);
        }
    }

    private void jugarUnTurno(Combate combate) {
        if (!combate.puedeActuarElSiguiente()) {
            combate.perderTurno();
        } else if (!combate.esTurnoDelJugador()) {
            combate.ejecutarTurnoDeEnemigo();
        } else {
            List<Movimiento> movimientos = combate.getMovimientosDelTurno();
            if (movimientos.isEmpty()) {
                combate.defender();
                return;
            }
            Movimiento elegido = movimientos.get(movimientos.size() - 1);
            List<Combatiente> objetivos = combate.getObjetivosPosibles(elegido);
            if (elegido.requiereElegirObjetivo() && !objetivos.isEmpty()) {
                objetivos = List.of(objetivos.get(0));
            }
            combate.ejecutarMovimiento(elegido, objetivos);
        }
    }
}
