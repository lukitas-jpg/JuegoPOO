package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combate.ResultadoDaño;
import ar.edu.uade.rpg.modelo.combatientes.Arquero;
import ar.edu.uade.rpg.modelo.combatientes.Asesino;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Curador;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.movimientos.ResultadoAccion;

/**
 * Pruebas de los 24 movimientos, de la formula de daño y de las dos familias
 * de habilidad.
 *
 * Donde interviene el azar se inyecta un Random con semilla fija, asi los
 * numeros son siempre los mismos y ningun test falla una vez cada tantas
 * corridas.
 */
class MovimientosTest {

    /**
     * Combatiente minimo para tener contra que pegar sin depender de los
     * enemigos, que todavia no estan escritos. Suerte en 0 para que nunca
     * critique y el daño sea predecible.
     */
    private static class Maniqui extends Combatiente {
        Maniqui(int vida, int defensa, int resistencia) {
            super("maniqui");
            vidaMaxima = vida;
            vidaActual = vida;
            manaMaximo = 10;
            manaActual = 10;
            ataqueFisico = 10;
            defensaFisica = defensa;
            ataqueMagico = 10;
            defensaMagica = defensa;
            velocidad = 10;
            suerte = 0;
            resistenciaAEstados = resistencia;
        }
    }

    private final CalculadorDeDaño calculador = new CalculadorDeDaño(new Random(2024));

    // ----------------------------------------------- carga de los movimientos

    @Test
    void lasSeisClasesArrancanConCuatroMovimientos() {
        for (Personaje personaje : List.of(new Guerrero("a"), new Mago("b"), new Arquero("c"),
                                           new Curador("d"), new Asesino("e"), new Tanque("f"))) {
            assertEquals(4, personaje.getMovimientos().size(), personaje.getDescripcionClase());
        }
    }

    @Test
    void elPrimerMovimientoDeCadaClaseEsGratis() {
        for (Personaje personaje : List.of(new Guerrero("a"), new Mago("b"), new Arquero("c"),
                                           new Curador("d"), new Asesino("e"), new Tanque("f"))) {
            assertEquals(0, personaje.getMovimientos().get(0).getCostoMana());
        }
    }

    @Test
    void losSlotsSeDesbloqueanEnUnoUnoCuatroYOcho() {
        Guerrero guerrero = new Guerrero("Lucas");
        List<Movimiento> movimientos = guerrero.getMovimientos();

        assertTrue(movimientos.get(0).estaDesbloqueado(guerrero));
        assertTrue(movimientos.get(1).estaDesbloqueado(guerrero));
        assertFalse(movimientos.get(2).estaDesbloqueado(guerrero));
        assertFalse(movimientos.get(3).estaDesbloqueado(guerrero));

        guerrero.ganarExperiencia(30 + 60 + 90); // hasta nivel 4
        assertEquals(4, guerrero.getNivel());
        assertTrue(movimientos.get(2).estaDesbloqueado(guerrero));
        assertFalse(movimientos.get(3).estaDesbloqueado(guerrero));

        guerrero.ganarExperiencia(10000);
        assertTrue(movimientos.get(3).estaDesbloqueado(guerrero));
    }

    @Test
    void soloElCuartoMovimientoEsDefinitivoYTieneCooldown() {
        Mago mago = new Mago("Giovanni");
        List<Movimiento> movimientos = mago.getMovimientos();

        assertFalse(movimientos.get(0).esDefinitivo());
        assertFalse(movimientos.get(1).esDefinitivo());
        assertFalse(movimientos.get(2).esDefinitivo());
        assertTrue(movimientos.get(3).esDefinitivo());

        Movimiento meteoro = movimientos.get(3);
        assertEquals("Meteoro", meteoro.getNombre());
        assertFalse(meteoro.estaEnCooldown());

        meteoro.iniciarCooldown();
        assertEquals(Movimiento.COOLDOWN_DEFINITIVA, meteoro.getCooldownRestante());
        assertFalse(meteoro.estaDisponible(mago));

        for (int turno = 0; turno < Movimiento.COOLDOWN_DEFINITIVA; turno++) {
            meteoro.reducirCooldown();
        }
        assertTrue(meteoro.estaDisponible(mago));
    }

    @Test
    void cadaEtapaDevuelveLasDefinitivasDisponibles() {
        Mago mago = new Mago("Giovanni");
        Movimiento meteoro = mago.getMovimientos().get(3);
        meteoro.iniciarCooldown();

        mago.alIniciarCombate();

        assertFalse(meteoro.estaEnCooldown());
    }

    @Test
    void dosPersonajesDeLaMismaClaseNoCompartenSusMovimientos() {
        Mago uno = new Mago("Uno");
        Mago otro = new Mago("Otro");

        uno.getMovimientos().get(3).iniciarCooldown();

        assertTrue(uno.getMovimientos().get(3).estaEnCooldown());
        assertFalse(otro.getMovimientos().get(3).estaEnCooldown());
    }

    // --------------------------------------------------------- formula de daño

    @Test
    void elDanioEscalaConElAtaqueDeQuienPega() {
        Guerrero guerrero = new Guerrero("Lucas"); // 18 de ataque fisico
        Mago mago = new Mago("Giovanni");          // 4 de ataque fisico

        int conGuerrero = calculador.calcular(guerrero, new Maniqui(100, 10, 0), 10, TipoDaño.FISICO).getDaño();
        int conMago = calculador.calcular(mago, new Maniqui(100, 10, 0), 10, TipoDaño.FISICO).getDaño();

        assertTrue(conGuerrero > conMago,
                "el guerrero deberia pegar mas fuerte: " + conGuerrero + " vs " + conMago);
    }

    @Test
    void masDefensaEsMenosDanioPeroNuncaCero() {
        Guerrero guerrero = new Guerrero("Lucas");

        int contraBlando = calculador.calcular(guerrero, new Maniqui(100, 0, 0), 10, TipoDaño.FISICO).getDaño();
        int contraDuro = calculador.calcular(guerrero, new Maniqui(100, 200, 0), 10, TipoDaño.FISICO).getDaño();

        assertTrue(contraDuro < contraBlando);
        assertTrue(contraDuro >= CalculadorDeDaño.GOLPE_MINIMO);
    }

    @Test
    void defenderBajaALaMitadElDanioRecibido() {
        Guerrero atacante = new Guerrero("Lucas");
        // Sin variacion no se puede comparar exacto, asi que se promedian muchos golpes.
        long normal = 0;
        long defendiendo = 0;
        for (int i = 0; i < 2000; i++) {
            normal += calculador.calcular(atacante, new Maniqui(1000, 10, 0), 20, TipoDaño.FISICO).getDaño();

            Maniqui conGuardia = new Maniqui(1000, 10, 0);
            conGuardia.defender();
            defendiendo += calculador.calcular(atacante, conGuardia, 20, TipoDaño.FISICO).getDaño();
        }
        double proporcion = (double) defendiendo / normal;
        assertTrue(proporcion > 0.45 && proporcion < 0.55,
                "defender deberia dejar el daño cerca de la mitad, dio " + proporcion);
    }

    @Test
    void elCriticoNoPasaDelTopeAunqueLaSuerteSiga() {
        Asesino asesino = new Asesino("Santiago");
        asesino.ganarExperiencia(100000); // nivel 20
        assertTrue(asesino.getSuerteEfectiva() > CalculadorDeDaño.PROBABILIDAD_CRITICA_MAXIMA);

        int criticos = 0;
        for (int i = 0; i < 4000; i++) {
            ResultadoDaño golpe = calculador.calcular(asesino, new Maniqui(10000, 10, 0), 10, TipoDaño.FISICO);
            if (golpe.fueCritico()) {
                criticos++;
            }
        }
        double tasa = criticos * 100.0 / 4000;
        assertTrue(tasa > 45 && tasa < 55, "la tasa de critico deberia quedar en el tope, dio " + tasa);
    }

    // ------------------------------------------------------------- habilidades

    @Test
    void laCuracionSumaElAtaqueMagicoDeQuienCura() {
        Curador curador = new Curador("Santiago"); // 14 de ataque magico
        Guerrero herido = new Guerrero("Lucas");
        herido.recibirDaño(100, TipoDaño.FISICO);
        assertEquals(20, herido.getVidaActual());

        Movimiento curacion = curador.getMovimientos().get(1);
        curacion.aplicar(curador, List.of(herido), calculador);

        assertEquals(54, herido.getVidaActual()); // 20 + (20 base + 14 de ataque magico)
    }

    @Test
    void laCuracionNoLevantaCaidos() {
        Curador curador = new Curador("Santiago");
        Guerrero caido = new Guerrero("Lucas");
        caido.recibirDaño(500, TipoDaño.FISICO);

        ResultadoAccion resultado = curador.getMovimientos().get(1)
                .aplicar(curador, List.of(caido), calculador);

        assertEquals(0, caido.getVidaActual());
        assertTrue(resultado.getObjetivosAfectados().isEmpty());
        assertEquals(1, resultado.getObjetivosFallados().size());
    }

    @Test
    void unMovimientoSobreUnoMismoSeAplicaAQuienLoUsaYNoAlObjetivo() {
        Tanque tanque = new Tanque("Maximo");
        Maniqui enemigo = new Maniqui(100, 10, 0);

        // Muro de Hierro es UNO_MISMO: el objetivo que se le pase da igual.
        tanque.getMovimientos().get(1).aplicar(tanque, List.of(enemigo), calculador);

        assertEquals(1, tanque.getEfectosActivos().size());
        assertTrue(enemigo.getEfectosActivos().isEmpty());
    }

    @Test
    void elVenenoDelArqueroEntraSiempreAunqueElObjetivoResista() {
        Arquero arquero = new Arquero("Santiago");
        Maniqui jefe = new Maniqui(800, 30, 50);

        arquero.getMovimientos().get(1).aplicar(arquero, List.of(jefe), calculador);

        assertEquals(1, jefe.getEfectosActivos().size());
        assertEquals("Veneno", jefe.getEfectosActivos().get(0).getNombre());
    }

    @Test
    void laResistenciaDelJefeBajaALaMitadElAturdimiento() {
        Arquero arquero = new Arquero("Santiago");
        Movimiento andanada = arquero.getMovimientos().get(3); // 40% de acierto

        int entraronComun = 0;
        int entraronJefe = 0;
        for (int i = 0; i < 4000; i++) {
            Maniqui comun = new Maniqui(100, 10, 0);
            Maniqui jefe = new Maniqui(100, 10, 50);
            entraronComun += andanada.aplicar(arquero, List.of(comun), calculador)
                    .getObjetivosAfectados().size();
            entraronJefe += andanada.aplicar(arquero, List.of(jefe), calculador)
                    .getObjetivosAfectados().size();
        }
        double tasaComun = entraronComun * 100.0 / 4000;
        double tasaJefe = entraronJefe * 100.0 / 4000;

        assertTrue(tasaComun > 35 && tasaComun < 45, "deberia rondar el 40%, dio " + tasaComun);
        assertTrue(tasaJefe > 15 && tasaJefe < 25, "deberia rondar el 20%, dio " + tasaJefe);
    }

    @Test
    void laAndanadaSeTiraUnaVezPorObjetivoYNoUnaSolaParaTodos() {
        Arquero arquero = new Arquero("Santiago");
        Movimiento andanada = arquero.getMovimientos().get(3);

        boolean huboResultadoMixto = false;
        for (int i = 0; i < 200 && !huboResultadoMixto; i++) {
            List<Combatiente> seis = List.of(
                    new Maniqui(100, 10, 0), new Maniqui(100, 10, 0), new Maniqui(100, 10, 0),
                    new Maniqui(100, 10, 0), new Maniqui(100, 10, 0), new Maniqui(100, 10, 0));
            ResultadoAccion resultado = andanada.aplicar(arquero, seis, calculador);

            // Si la tirada fuera una sola para todos, siempre entrarian 0 o 6.
            huboResultadoMixto = !resultado.getObjetivosAfectados().isEmpty()
                    && !resultado.getObjetivosFallados().isEmpty();
        }
        assertTrue(huboResultadoMixto, "cada objetivo deberia tener su propia tirada");
    }

    @Test
    void elAturdimientoAplicadoPorLaAndanadaLeSacaElTurnoAlEnemigo() {
        Maniqui enemigo = new Maniqui(100, 10, 0);
        enemigo.aplicarEfecto(new Aturdimiento(1));

        assertFalse(enemigo.puedeActuar());
    }

    @Test
    void unAtaqueEnAreaGolpeaATodosLosObjetivosQueRecibe() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.ganarExperiencia(100000); // para tener Torbellino desbloqueado

        Maniqui uno = new Maniqui(500, 10, 0);
        Maniqui dos = new Maniqui(500, 10, 0);
        Maniqui tres = new Maniqui(500, 10, 0);

        ResultadoAccion resultado = guerrero.getMovimientos().get(3)
                .aplicar(guerrero, List.of(uno, dos, tres), calculador);

        assertEquals(3, resultado.getObjetivosAfectados().size());
        assertTrue(uno.getVidaActual() < 500);
        assertTrue(dos.getVidaActual() < 500);
        assertTrue(tres.getVidaActual() < 500);
        assertTrue(resultado.getDañoTotal() > 0);
    }

    @Test
    void posturaFirmeLeSubeLaDefensaFisicaAlTanque() {
        Tanque tanque = new Tanque("Maximo");
        Movimiento postura = tanque.getMovimientos().get(2);
        int antes = tanque.getDefensaFisicaEfectiva();

        postura.aplicar(tanque, List.of(), calculador);

        assertEquals("Postura Firme", postura.getNombre());
        assertEquals(antes * 125 / 100, tanque.getDefensaFisicaEfectiva());
    }
}
