package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Acechador;
import ar.edu.uade.rpg.modelo.combatientes.Acolito;
import ar.edu.uade.rpg.modelo.combatientes.Berserker;
import ar.edu.uade.rpg.modelo.combatientes.Bruto;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Emponzoñador;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Guardian;
import ar.edu.uade.rpg.modelo.combatientes.Hechicero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Nigromante;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.SeñorDeLasSombras;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.combatientes.Verdugo;
import ar.edu.uade.rpg.modelo.efectos.Proteccion;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.ResultadoAccion;

/**
 * Pruebas de los 11 enemigos, de las cuatro interfaces de apuntado y del
 * escalado por etapa.
 */
class EnemigosTest {

    private final CalculadorDeDaño calculador = new CalculadorDeDaño(new Random(77));

    private List<Combatiente> party() {
        return new ArrayList<>(List.of(new Guerrero("G"), new Mago("M"), new Tanque("T")));
    }

    // -------------------------------------------------- interfaces de apuntado

    @Test
    void elBrutoApuntaAlMasHerido() {
        List<Combatiente> party = party();
        party.get(1).recibirDaño(50, TipoDaño.MAGICO); // el Mago queda en 30

        Accion accion = new Bruto().elegirAccion(party, List.of(), calculador);
        ResultadoAccion resultado = accion.ejecutar();

        assertSame(party.get(1), resultado.getObjetivosAfectados().get(0));
    }

    @Test
    void elBerserkerApuntaAlQueMasPega() {
        List<Combatiente> party = party(); // Guerrero 18+4, Mago 4+20, Tanque 12+3

        ResultadoAccion resultado = new Berserker()
                .elegirAccion(party, List.of(), calculador).ejecutar();

        assertSame(party.get(1), resultado.getObjetivosAfectados().get(0)); // el Mago suma 24
    }

    @Test
    void elEmponzoniadorBuscaAlQueNoEstaEnvenenado() {
        List<Combatiente> party = party();
        party.get(0).aplicarEfecto(new Veneno(6, 3));
        party.get(1).aplicarEfecto(new Veneno(6, 3));

        ResultadoAccion resultado = new Emponzoñador()
                .elegirAccion(party, List.of(), calculador).ejecutar();

        assertSame(party.get(2), resultado.getObjetivosAfectados().get(0));
        assertEquals(1, party.get(2).getEfectosActivos().size());
    }

    @Test
    void siYaEstanTodosEnvenenadosElEmponzoniadorMuerde() {
        List<Combatiente> party = party();
        for (Combatiente miembro : party) {
            miembro.aplicarEfecto(new Veneno(6, 3));
        }
        int vidaAntes = 0;
        for (Combatiente miembro : party) {
            vidaAntes += miembro.getVidaActual();
        }

        new Emponzoñador().elegirAccion(party, List.of(), calculador).ejecutar();

        int vidaDespues = 0;
        for (Combatiente miembro : party) {
            vidaDespues += miembro.getVidaActual();
        }
        assertTrue(vidaDespues < vidaAntes, "deberia haber mordido a alguien");
    }

    @Test
    void elHechiceroNoEligeObjetivoPorqueLeDaATodos() {
        List<Combatiente> party = party();

        ResultadoAccion resultado = new Hechicero()
                .elegirAccion(party, List.of(), calculador).ejecutar();

        assertEquals(3, resultado.getObjetivosAfectados().size());
    }

    @Test
    void elHechiceroPasaAlAtaqueBaratoCuandoSeQuedaSinMana() {
        Hechicero hechicero = new Hechicero();
        List<Combatiente> party = party();

        // 60 de mana a 12 por Onda Oscura: cinco usos y se seca.
        for (int uso = 0; uso < 5; uso++) {
            hechicero.elegirAccion(party, List.of(), calculador).ejecutar();
        }
        assertEquals(0, hechicero.getManaActual());

        ResultadoAccion resultado = hechicero.elegirAccion(party, List.of(), calculador).ejecutar();
        assertEquals(1, resultado.getObjetivosAfectados().size());
    }

    // ------------------------------------------------------ conductas propias

    @Test
    void elAcechadorPegaTresVecesEnUnTurno() {
        List<Combatiente> party = party();

        ResultadoAccion resultado = new Acechador()
                .elegirAccion(party, List.of(), calculador).ejecutar();

        assertEquals(3, resultado.getObjetivosAfectados().size());
    }

    @Test
    void elBerserkerPegaMasFuerteCuantaMenosVidaTiene() {
        Berserker berserker = new Berserker();
        int conVidaLlena = berserker.getAtaqueFisicoEfectivo();

        berserker.recibirDañoDirecto(berserker.getVidaMaxima() / 2);
        int aLaMitad = berserker.getAtaqueFisicoEfectivo();

        assertTrue(aLaMitad > conVidaLlena,
                "a la mitad de vida deberia pegar mas: " + aLaMitad + " vs " + conVidaLlena);
        assertEquals(conVidaLlena * 3 / 2, aLaMitad);
    }

    @Test
    void elGuardianCubreASusAliadosYLesBajaElDanio() {
        Bruto protegido = new Bruto();
        Proteccion proteccion = new Proteccion(30, 1);
        protegido.aplicarEfecto(proteccion);

        // 100 de daño con 30% de reduccion deberian entrar 70.
        assertEquals(70, protegido.recibirDaño(100, TipoDaño.FISICO));
    }

    @Test
    void elGuardianAlgunasVecesPasaElTurnoYOtrasPega() {
        List<Combatiente> party = party();
        Guardian guardian = new Guardian();
        List<Enemigo> aliados = List.of(guardian, new Bruto());

        boolean cubrioAlgunaVez = false;
        boolean pegoAlgunaVez = false;
        for (int turno = 0; turno < 200; turno++) {
            Accion accion = guardian.elegirAccion(party, aliados, calculador);
            if (accion == null) {
                cubrioAlgunaVez = true;
            } else {
                pegoAlgunaVez = true;
            }
        }
        assertTrue(cubrioAlgunaVez, "deberia cubrir alguna vez");
        assertTrue(pegoAlgunaVez, "deberia pegar alguna vez");
    }

    @Test
    void elAcolitoCuraAlCompaneroMasHerido() {
        Bruto herido = new Bruto();
        herido.recibirDañoDirecto(50); // queda en 20 de 70
        Bruto sano = new Bruto();
        Acolito acolito = new Acolito();

        acolito.elegirAccion(party(), List.of(acolito, herido, sano), calculador).ejecutar();

        assertTrue(herido.getVidaActual() > 20, "deberia haberlo curado");
        assertEquals(70, sano.getVidaActual());
    }

    @Test
    void elAcolitoAtacaSiNoHayAQuienCurar() {
        Acolito acolito = new Acolito();
        List<Combatiente> party = party();

        ResultadoAccion resultado = acolito
                .elegirAccion(party, List.of(acolito, new Bruto()), calculador).ejecutar();

        assertEquals(1, resultado.getObjetivosAfectados().size());
        assertTrue(resultado.getDañoTotal() > 0);
    }

    // ------------------------------------------------------------- nigromante

    @Test
    void elNigromanteEntraAlCombateConCincoEsqueletos() {
        Nigromante nigromante = new Nigromante();

        List<Combatiente> invocados = nigromante.alIniciarCombate();

        assertEquals(5, invocados.size());
        assertEquals("Esqueleto", invocados.get(0).getNombre());
    }

    @Test
    void elNigromanteReponeLosEsqueletosCadaTresRondas() {
        Nigromante nigromante = new Nigromante();
        List<Combatiente> primeros = nigromante.alIniciarCombate();
        primeros.get(0).recibirDañoDirecto(100); // matamos uno

        // Dos rondas no alcanzan.
        nigromante.elegirAccion(party(), List.of(), calculador);
        nigromante.elegirAccion(party(), List.of(), calculador);
        // La tercera si.
        Accion tercera = nigromante.elegirAccion(party(), List.of(), calculador);

        ResultadoAccion resultado = tercera.ejecutar();
        assertEquals(1, resultado.getCombatientesInvocados().size());
    }

    @Test
    void losEsqueletosSeDesarmanSiMuereElNigromante() {
        Nigromante nigromante = new Nigromante();
        List<Combatiente> esqueletos = nigromante.alIniciarCombate();
        Combatiente esqueleto = esqueletos.get(0);
        assertTrue(esqueleto.estaVivo());

        nigromante.recibirDañoDirecto(nigromante.getVidaActual());
        Enemigo comoEnemigo = (Enemigo) esqueleto;
        Accion accion = comoEnemigo.elegirAccion(party(), List.of(), calculador);

        assertNull(accion, "sin invocador no deberia poder actuar");
        assertFalse(esqueleto.estaVivo());
    }

    // ---------------------------------------------------------------- jefes

    @Test
    void elVerdugoAlternaEntreElAturdimientoYElHachazo() {
        Verdugo verdugo = new Verdugo();
        List<Combatiente> party = party();

        // El primer turno usa la Mirada, que entra en cooldown.
        verdugo.elegirAccion(party, List.of(), calculador).ejecutar();
        assertTrue(verdugo.getMovimientos().get(1).estaEnCooldown());

        // Mientras este en cooldown, pega.
        ResultadoAccion segundo = verdugo.elegirAccion(party, List.of(), calculador).ejecutar();
        assertTrue(segundo.getDañoTotal() > 0);
    }

    @Test
    void elSenorDeLasSombrasCambiaDeFaseAlBajarDeLaMitad() {
        SeñorDeLasSombras jefe = new SeñorDeLasSombras();
        assertEquals(1, jefe.getFase());

        // Fase 1: un solo objetivo.
        ResultadoAccion enFase1 = jefe.elegirAccion(party(), List.of(), calculador).ejecutar();
        assertEquals(1, enFase1.getObjetivosAfectados().size());

        jefe.recibirDañoDirecto(300); // queda en 200 de 500
        ResultadoAccion enFase2 = jefe.elegirAccion(party(), List.of(), calculador).ejecutar();

        assertEquals(2, jefe.getFase());
        // En fase 2 apunta a los tres, con Marea de Sombras o con Llamas Sombrias.
        // Con las Llamas, los que zafan del 40% cuentan como fallados, no como afectados.
        int alcanzados = enFase2.getObjetivosAfectados().size() + enFase2.getObjetivosFallados().size();
        assertEquals(3, alcanzados, "en fase 2 apunta a los tres");
    }

    @Test
    void elSenorApuntaAlMasDebilEnFaseUnoYAlMasPeligrosoEnFaseDos() {
        SeñorDeLasSombras jefe = new SeñorDeLasSombras();
        List<Combatiente> party = party();
        party.get(2).recibirDaño(100, TipoDaño.FISICO); // el Tanque queda mas herido

        assertSame(party.get(2), jefe.elegirObjetivo(party));

        jefe.recibirDañoDirecto(300);
        jefe.elegirAccion(party, List.of(), calculador); // dispara el cambio de fase
        assertEquals(2, jefe.getFase());
        assertSame(party.get(1), jefe.elegirObjetivo(party)); // el Mago es el que mas pega
    }

    @Test
    void losJefesResistenLaMitadDeLosEfectos() {
        assertEquals(50, new Verdugo().getResistenciaAEstados());
        assertEquals(50, new SeñorDeLasSombras().getResistenciaAEstados());
        assertEquals(0, new Bruto().getResistenciaAEstados());
    }

    // -------------------------------------------------------------- escalado

    @Test
    void elEscaladoSubeElAguanteMasRapidoQueElAtaque() {
        Bruto enEtapa1 = new Bruto();
        Bruto enEtapa11 = new Bruto();
        enEtapa11.escalarParaEtapa(11);

        // aguante: 100 + 14 x 10 = 240%  |  ataque: 100 + 10 x 10 = 200%
        assertEquals(enEtapa1.getVidaMaxima() * 240 / 100, enEtapa11.getVidaMaxima());
        assertEquals(enEtapa1.getAtaqueFisicoEfectivo() * 200 / 100,
                enEtapa11.getAtaqueFisicoEfectivo());
    }

    @Test
    void laVelocidadYLaSuerteNoEscalan() {
        Acechador enEtapa1 = new Acechador();
        Acechador enEtapa18 = new Acechador();
        enEtapa18.escalarParaEtapa(18);

        assertEquals(enEtapa1.getVelocidadEfectiva(), enEtapa18.getVelocidadEfectiva());
        assertEquals(enEtapa1.getSuerteEfectiva(), enEtapa18.getSuerteEfectiva());
    }

    @Test
    void enLaEtapaUnoElEscaladoNoCambiaNada() {
        Bruto sinEscalar = new Bruto();
        Bruto escalado = new Bruto();
        escalado.escalarParaEtapa(1);

        assertEquals(sinEscalar.getVidaMaxima(), escalado.getVidaMaxima());
        assertEquals(sinEscalar.getAtaqueFisicoEfectivo(), escalado.getAtaqueFisicoEfectivo());
    }

    @Test
    void losJefesYLosInvocadosNoEscalan() {
        Verdugo verdugo = new Verdugo();
        verdugo.escalarParaEtapa(10);
        assertEquals(380, verdugo.getVidaMaxima());

        SeñorDeLasSombras jefe = new SeñorDeLasSombras();
        jefe.escalarParaEtapa(20);
        assertEquals(500, jefe.getVidaMaxima());

        Nigromante nigromante = new Nigromante();
        Combatiente esqueleto = nigromante.alIniciarCombate().get(0);
        ((Enemigo) esqueleto).escalarParaEtapa(18);
        assertEquals(30, esqueleto.getVidaMaxima());
    }

    @Test
    void escalarLlenaLaVidaDelEnemigo() {
        Bruto bruto = new Bruto();
        bruto.recibirDañoDirecto(40);
        bruto.escalarParaEtapa(6);

        assertEquals(bruto.getVidaMaxima(), bruto.getVidaActual());
    }

    // ------------------------------------------------- estadisticas de partida

    @Test
    void losOnceEnemigosArrancanVivosYConAlMenosUnMovimiento() {
        List<Enemigo> todos = List.of(new Bruto(), new Guardian(), new Acechador(),
                new Hechicero(), new Emponzoñador(), new Acolito(), new Berserker(),
                new Nigromante(), new Verdugo(), new SeñorDeLasSombras());
        for (Enemigo enemigo : todos) {
            assertTrue(enemigo.estaVivo(), enemigo.getNombre());
            assertTrue(enemigo.getVidaMaxima() > 0, enemigo.getNombre());
            assertFalse(enemigo.getMovimientos().isEmpty(), enemigo.getNombre());
            assertNotNull(enemigo.getRandom(), enemigo.getNombre());
        }
    }

    @Test
    void ningunEnemigoSubeDeNivel() {
        // Enemigo no extiende Personaje: no tiene ganarExperiencia ni equipo.
        assertFalse(Personaje.class.isAssignableFrom(Bruto.class));
        assertTrue(Enemigo.class.isAssignableFrom(Bruto.class));
    }
}
