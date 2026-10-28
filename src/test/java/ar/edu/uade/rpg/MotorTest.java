package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combate.GestorDeTurnos;
import ar.edu.uade.rpg.modelo.combate.ResultadoCombate;
import ar.edu.uade.rpg.modelo.combatientes.Bruto;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Curador;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.items.CatalogoDeConsumibles;
import ar.edu.uade.rpg.modelo.partida.Etapa;
import ar.edu.uade.rpg.modelo.partida.EtapaDeCombate;
import ar.edu.uade.rpg.modelo.partida.EstadoPartida;
import ar.edu.uade.rpg.modelo.partida.GeneradorDeEtapas;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Partida;
import ar.edu.uade.rpg.modelo.partida.Party;
import ar.edu.uade.rpg.modelo.partida.TipoEtapa;
import ar.edu.uade.rpg.persistencia.RepositorioPartidasArchivo;

/**
 * Pruebas del motor: orden de turnos, combate, etapas, partida y persistencia.
 */
class MotorTest {

    private static final List<ClaseDePersonaje> TRES_CLASES =
            List.of(ClaseDePersonaje.GUERRERO, ClaseDePersonaje.MAGO, ClaseDePersonaje.CURADOR);

    private Party party() {
        return new Party(List.of(new Guerrero("G"), new Mago("M"), new Curador("C")));
    }

    // ------------------------------------------------------- gestor de turnos

    @Test
    void elOrdenDeLaRondaVaDelMasRapidoAlMasLento() {
        Tanque lento = new Tanque("T");      // velocidad 6
        Guerrero medio = new Guerrero("G");  // velocidad 10
        Mago rapido = new Mago("M");         // velocidad 11

        GestorDeTurnos gestor = new GestorDeTurnos(
                List.of(lento, medio, rapido), new Random(1));
        gestor.iniciarNuevaRonda();

        List<Combatiente> orden = gestor.getOrdenPrevisto();
        assertSame(rapido, orden.get(0));
        assertSame(medio, orden.get(1));
        assertSame(lento, orden.get(2));
    }

    @Test
    void elOrdenSeRecalculaCadaRondaYLosBuffosSeNotan() {
        Tanque tanque = new Tanque("T");
        Mago mago = new Mago("M");
        GestorDeTurnos gestor = new GestorDeTurnos(List.of(tanque, mago), new Random(1));

        gestor.iniciarNuevaRonda();
        assertSame(mago, gestor.getOrdenPrevisto().get(0));

        // Un buffo de velocidad enorme sobre el Tanque da vuelta el orden,
        // pero recien en la ronda siguiente.
        tanque.aplicarEfecto(new ar.edu.uade.rpg.modelo.efectos.BuffEstadistica(
                ar.edu.uade.rpg.modelo.movimientos.Estadistica.VELOCIDAD, 200, 3));
        assertSame(mago, gestor.getOrdenPrevisto().get(0), "la ronda en curso no cambia");

        gestor.iniciarNuevaRonda();
        assertSame(tanque, gestor.getOrdenPrevisto().get(0));
    }

    @Test
    void elGestorSalteaALosQueMurieronDuranteLaRonda() {
        Guerrero guerrero = new Guerrero("G");
        Mago mago = new Mago("M");
        GestorDeTurnos gestor = new GestorDeTurnos(List.of(guerrero, mago), new Random(1));
        gestor.iniciarNuevaRonda();

        assertSame(mago, gestor.getSiguienteEnActuar()); // el Mago es mas rapido
        mago.recibirDañoDirecto(mago.getVidaActual());

        assertSame(guerrero, gestor.getSiguienteEnActuar());
    }

    @Test
    void losQueEntranAMitadDeRondaActuanReciénEnLaSiguiente() {
        Guerrero guerrero = new Guerrero("G");
        GestorDeTurnos gestor = new GestorDeTurnos(List.of(guerrero), new Random(1));
        gestor.iniciarNuevaRonda();

        Bruto tardio = new Bruto();
        gestor.incorporar(tardio);

        assertEquals(1, gestor.getOrdenPrevisto().size(), "la ronda en curso no lo incluye");
        gestor.iniciarNuevaRonda();
        assertEquals(2, gestor.getOrdenPrevisto().size());
    }

    @Test
    void laRondaTerminaCuandoActuaronTodos() {
        GestorDeTurnos gestor = new GestorDeTurnos(
                List.of(new Guerrero("G"), new Mago("M")), new Random(1));
        gestor.iniciarNuevaRonda();

        assertFalse(gestor.rondaTerminada());
        gestor.consumirTurno();
        assertFalse(gestor.rondaTerminada());
        gestor.consumirTurno();
        assertTrue(gestor.rondaTerminada());
        assertEquals(1, gestor.getNumeroRonda());
    }

    // ---------------------------------------------------------------- combate

    @Test
    void elCombateArrancaYRegistraLoQuePasa() {
        Combate combate = new Combate(party(), List.of(new Bruto()), 35, 35, new Random(1));
        combate.iniciar();

        assertFalse(combate.getRegistro().isEmpty());
        assertNotNull(combate.getSiguienteEnActuar());
        assertFalse(combate.hayGanador());
    }

    @Test
    void elCombateTerminaCuandoCaenTodosLosEnemigos() {
        Bruto bruto = new Bruto();
        Combate combate = new Combate(party(), List.of(bruto), 35, 35, new Random(1));
        combate.iniciar();

        bruto.recibirDañoDirecto(bruto.getVidaActual());
        combate.retirarCaidos();

        assertTrue(combate.hayGanador());
        ResultadoCombate resultado = combate.calcularResultado();
        assertTrue(resultado.fueVictoria());
        assertEquals(35, resultado.getExperienciaGanada());
        assertEquals(35, resultado.getOroGanado());
    }

    @Test
    void siCaeLaPartyNoSePagaNiExperienciaNiOro() {
        Party party = party();
        Combate combate = new Combate(party, List.of(new Bruto()), 35, 35, new Random(1));
        combate.iniciar();
        for (Personaje miembro : party.getMiembros()) {
            miembro.recibirDañoDirecto(miembro.getVidaActual());
        }

        ResultadoCombate resultado = combate.calcularResultado();

        assertFalse(resultado.fueVictoria());
        assertEquals(0, resultado.getExperienciaGanada());
        assertEquals(0, resultado.getOroGanado());
    }

    @Test
    void elTurnoDeUnEnemigoLoResuelveElSolo() {
        Party party = party();
        Combate combate = new Combate(party, List.of(new Bruto()), 35, 35, new Random(7));
        combate.iniciar();

        // Se avanza hasta que le toque al enemigo.
        int vueltas = 0;
        while (combate.esTurnoDelJugador() && vueltas++ < 10) {
            combate.ejecutarMovimiento(combate.getMovimientosDelTurno().get(0),
                    combate.getObjetivosPosibles(combate.getMovimientosDelTurno().get(0)));
        }
        int vidaAntes = 0;
        for (Personaje miembro : party.getMiembros()) {
            vidaAntes += miembro.getVidaActual();
        }
        combate.ejecutarTurnoDeEnemigo();

        int vidaDespues = 0;
        for (Personaje miembro : party.getMiembros()) {
            vidaDespues += miembro.getVidaActual();
        }
        assertTrue(vidaDespues < vidaAntes, "el enemigo deberia haber pegado");
    }

    // ----------------------------------------------------------------- party

    @Test
    void laPartyRepartLaExperienciaYLosCaidosRecibenLaMitad() {
        Party party = party();
        Personaje caido = party.getMiembros().get(1);
        caido.recibirDañoDirecto(caido.getVidaActual());

        party.repartirExperiencia(90);

        // El vivo cobra 90: 30 para el nivel 2 y 60 para el 3.
        assertEquals(3, party.getMiembros().get(0).getNivel());
        // El caido cobra 45: le alcanza para el nivel 2 y le sobran 15.
        assertEquals(2, caido.getNivel());
        assertEquals(15, caido.getExperienciaActual());
    }

    @Test
    void laPartyEstaDerrotadaSoloCuandoNoQuedaNadie() {
        Party party = party();
        assertFalse(party.estaDerrotada());

        for (Personaje miembro : party.getMiembros()) {
            miembro.recibirDañoDirecto(miembro.getVidaActual());
        }
        assertTrue(party.estaDerrotada());
        assertEquals(3, party.getMiembrosCaidos().size());
    }

    @Test
    void elOroNoSePuedeGastarSiNoAlcanza() {
        Party party = party();
        party.agregarOro(100);

        assertFalse(party.gastarOro(150));
        assertEquals(100, party.getOro());
        assertTrue(party.gastarOro(60));
        assertEquals(40, party.getOro());
    }

    @Test
    void alCerrarLaEtapaSeVaLoTemporalYSeQuedaElVeneno() {
        Party party = party();
        Personaje uno = party.getMiembros().get(0);
        uno.aplicarEfecto(new ar.edu.uade.rpg.modelo.efectos.Veneno(6, 3));
        uno.aplicarEfecto(new ar.edu.uade.rpg.modelo.efectos.Escudo(40, 3));

        party.limpiarEfectosTemporales();

        assertEquals(1, uno.getEfectosActivos().size());
        assertTrue(party.tieneAlgunEfectoPersistente());
    }

    // ---------------------------------------------------------------- etapas

    @Test
    void lasEtapas4Y9Y14Y19SonDeTiendaYElRestoDeCombate() {
        GeneradorDeEtapas generador = new GeneradorDeEtapas(new Random(1));
        for (int numero = 1; numero <= 20; numero++) {
            Etapa etapa = generador.crearEtapa(numero, TRES_CLASES);
            boolean esperadaDeTienda = numero == 4 || numero == 9 || numero == 14 || numero == 19;
            assertEquals(!esperadaDeTienda, etapa.esDeCombate(), "etapa " + numero);
            assertEquals(!esperadaDeTienda, etapa.otorgaExperiencia(), "etapa " + numero);
        }
    }

    @Test
    void laEtapa10EsElMinijefeYLa20ElJefeFinal() {
        GeneradorDeEtapas generador = new GeneradorDeEtapas(new Random(1));

        EtapaDeCombate minijefe = (EtapaDeCombate) generador.crearEtapa(10, TRES_CLASES);
        assertEquals(TipoEtapa.MINIJEFE, minijefe.getTipo());
        assertTrue(minijefe.esJefe());
        assertEquals(1, minijefe.getEnemigos().size());
        assertEquals("Verdugo", minijefe.getEnemigos().get(0).getNombre());

        EtapaDeCombate jefe = (EtapaDeCombate) generador.crearEtapa(20, TRES_CLASES);
        assertEquals(TipoEtapa.JEFE_FINAL, jefe.getTipo());
        assertEquals("Señor de las Sombras", jefe.getEnemigos().get(0).getNombre());
    }

    @Test
    void laRecompensaDeLaEtapaEsVeinticincoMasDiezPorEtapaYElDobleEnLosJefes() {
        GeneradorDeEtapas generador = new GeneradorDeEtapas(new Random(1));

        EtapaDeCombate primera = (EtapaDeCombate) generador.crearEtapa(1, TRES_CLASES);
        assertEquals(35, primera.getExperienciaBase());
        assertEquals(35, primera.getOroBase());

        EtapaDeCombate minijefe = (EtapaDeCombate) generador.crearEtapa(10, TRES_CLASES);
        assertEquals(250, minijefe.getExperienciaBase()); // (25 + 100) x 2
    }

    @Test
    void losEnemigosDeLasPrimerasEtapasVienenSinEscalar() {
        GeneradorDeEtapas generador = new GeneradorDeEtapas(new Random(1));
        EtapaDeCombate primera = (EtapaDeCombate) generador.crearEtapa(1, TRES_CLASES);

        for (Enemigo enemigo : primera.getEnemigos()) {
            assertTrue(enemigo.getVidaMaxima() <= 95, enemigo.getNombre() + " deberia venir en crudo");
        }
        assertEquals(2, primera.getEnemigos().size());
    }

    @Test
    void losEnemigosDeLasUltimasEtapasVienenEscalados() {
        GeneradorDeEtapas generador = new GeneradorDeEtapas(new Random(1));
        EtapaDeCombate tardia = (EtapaDeCombate) generador.crearEtapa(18, TRES_CLASES);

        boolean alguienEscalado = false;
        for (Enemigo enemigo : tardia.getEnemigos()) {
            if (enemigo.getVidaMaxima() > 150) {
                alguienEscalado = true;
            }
        }
        assertTrue(alguienEscalado, "en la etapa 18 los enemigos deberian tener mucha mas vida");
    }

    // --------------------------------------------------------------- partida

    @Test
    void laPartidaArrancaEnLaEtapaUnoYEnCurso() {
        Partida partida = new Partida(party());

        assertEquals(1, partida.getEtapaActual());
        assertEquals(EstadoPartida.EN_CURSO, partida.getEstado());
        assertTrue(partida.estaEnCurso());
    }

    @Test
    void pasarLaEtapaVeinteGanaLaPartida() {
        Partida partida = new Partida(party());
        for (int etapa = 1; etapa < 20; etapa++) {
            partida.avanzarEtapa();
        }
        assertEquals(20, partida.getEtapaActual());
        assertTrue(partida.estaEnCurso());

        partida.avanzarEtapa();

        assertTrue(partida.fueGanada());
        assertFalse(partida.estaEnCurso());
        assertEquals(20, partida.getEtapasCompletadas());
    }

    @Test
    void quedarseSinPartyPierdeLaPartida() {
        Partida partida = new Partida(party());
        partida.terminarPorDerrota();

        assertTrue(partida.fuePerdida());
        assertFalse(partida.estaEnCurso());
    }

    // ------------------------------------------------------------ el Juego

    @Test
    void iniciarUnaRunArmaLaPartyYLaEtapaUno() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(1)));

        Partida partida = juego.iniciarNuevaRun(TRES_CLASES);

        assertEquals(3, partida.getParty().getMiembros().size());
        assertEquals(1, juego.getEtapaActual().getNumero());
        assertTrue(juego.hayPartidaGuardada(), "deberia guardar solo");
    }

    @Test
    void ganarUnaEtapaRepartLaRecompensaYAvanza() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(1)));
        juego.iniciarNuevaRun(TRES_CLASES);

        juego.finalizarEtapaDeCombate(new ResultadoCombate(true, 35, 35, List.of()));

        assertEquals(2, juego.getPartida().getEtapaActual());
        assertEquals(35, juego.getPartida().getParty().getOro());
        assertEquals(2, juego.getPartida().getParty().getMiembros().get(0).getNivel());
    }

    @Test
    void perderUnCombateTerminaLaRunYBorraElGuardado() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(1)));
        juego.iniciarNuevaRun(TRES_CLASES);
        assertTrue(juego.hayPartidaGuardada());

        juego.finalizarEtapaDeCombate(new ResultadoCombate(false, 0, 0, List.of()));

        assertEquals(EstadoPartida.PERDIDA, juego.getEstadoPartida());
        assertFalse(juego.hayPartidaGuardada(), "una run perdida no se retoma");
    }

    // ----------------------------------------------------------- persistencia

    @Test
    void guardarYCargarDevuelveLaPartidaConTodoAdentro() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        RepositorioPartidasArchivo repositorio =
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString());

        Party party = party();
        party.agregarOro(500);
        party.getInventario().agregar(CatalogoDeConsumibles.pocionDeVida());
        party.getMiembros().get(0).ganarExperiencia(1000);
        party.getMiembros().get(1).recibirDaño(30, TipoDaño.MAGICO);
        Partida original = new Partida(party);
        original.avanzarEtapa();

        repositorio.guardar(original);
        Partida cargada = repositorio.cargar();

        assertNotNull(cargada);
        assertEquals(2, cargada.getEtapaActual());
        assertEquals(500, cargada.getParty().getOro());
        assertEquals(1, cargada.getParty().getInventario().contar("Pocion de Vida"));
        assertEquals(original.getParty().getMiembros().get(0).getNivel(),
                cargada.getParty().getMiembros().get(0).getNivel());
        assertEquals(original.getParty().getMiembros().get(1).getVidaActual(),
                cargada.getParty().getMiembros().get(1).getVidaActual());
    }

    @Test
    void cargarSinNadaGuardadoDevuelveNullYNoExplota() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        RepositorioPartidasArchivo repositorio =
                new RepositorioPartidasArchivo(temporal.resolve("no-existe.dat").toString());

        assertFalse(repositorio.existePartidaGuardada());
        assertNull(repositorio.cargar());
    }

    @Test
    void borrarDejaElRepositorioVacio() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        RepositorioPartidasArchivo repositorio =
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString());
        repositorio.guardar(new Partida(party()));
        assertTrue(repositorio.existePartidaGuardada());

        repositorio.borrar();

        assertFalse(repositorio.existePartidaGuardada());
    }

    // --------------------------------------------------- una run de punta a punta

    @Test
    void sePuedeJugarUnaRunEnteraDeLaEtapaUnoALaVeinte() throws Exception {
        Path temporal = Files.createTempDirectory("rpg");
        Juego juego = new Juego(
                new RepositorioPartidasArchivo(temporal.resolve("partida.dat").toString()),
                new GeneradorDeEtapas(new Random(2024)));
        juego.iniciarNuevaRun(TRES_CLASES);

        List<Integer> etapasVistas = new ArrayList<>();
        while (juego.getPartida().estaEnCurso()) {
            Etapa etapa = juego.getEtapaActual();
            etapasVistas.add(etapa.getNumero());
            if (etapa.esDeCombate()) {
                EtapaDeCombate deCombate = (EtapaDeCombate) etapa;
                juego.finalizarEtapaDeCombate(new ResultadoCombate(true,
                        deCombate.getExperienciaBase(), deCombate.getOroBase(), List.of()));
            } else {
                juego.finalizarEtapaDeTienda();
            }
        }

        assertEquals(20, etapasVistas.size(), "deberia recorrer las 20 etapas");
        assertEquals(EstadoPartida.GANADA, juego.getEstadoPartida());
        // 2390 es todo el oro que paga la run. De eso, 1940 es lo GASTABLE:
        // los 450 de la etapa 20 caen despues de la ultima tienda y no se
        // pueden usar para nada. Por eso los precios del catalogo se
        // calibraron contra 1940 y no contra 2390.
        assertEquals(2390, juego.getPartida().getParty().getOro(),
                "la run entera deberia pagar 2390 de oro");
        assertEquals(13, juego.getPartida().getParty().getMiembros().get(0).getNivel(),
                "la party deberia terminar en nivel 13");
        assertFalse(juego.hayPartidaGuardada(), "al ganar no queda nada que retomar");
    }
}
