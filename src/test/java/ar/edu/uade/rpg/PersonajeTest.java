package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combatientes.Arquero;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.items.Arma;
import ar.edu.uade.rpg.modelo.items.Rareza;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Pruebas de lo que ya esta codeado del modelo: estadisticas base, daño,
 * curacion, mana, defender, progresion de nivel y equipo.
 *
 * No prueban movimientos ni efectos concretos porque esas clases todavia no
 * estan escritas.
 */
class PersonajeTest {

    @Test
    void elGuerreroArrancaConLasEstadisticasDelDocumentoDeDiseño() {
        Guerrero guerrero = new Guerrero("Lucas");

        assertEquals(1, guerrero.getNivel());
        assertEquals(120, guerrero.getVidaMaxima());
        assertEquals(120, guerrero.getVidaActual());
        assertEquals(40, guerrero.getManaMaximo());
        assertEquals(18, guerrero.getAtaqueFisicoEfectivo());
        assertEquals(12, guerrero.getDefensaFisicaEfectiva());
        assertEquals(10, guerrero.getVelocidadEfectiva());
        assertEquals(8, guerrero.getSuerteEfectiva());
        assertTrue(guerrero.estaVivo());
    }

    @Test
    void recibirDañoBajaLaVidaYNuncaLaDejaNegativa() {
        Tanque tanque = new Tanque("Maximo");

        assertEquals(30, tanque.recibirDaño(30, TipoDaño.FISICO));
        assertEquals(120, tanque.getVidaActual());

        // El exceso no se descuenta: devuelve solo lo que entro de verdad.
        assertEquals(120, tanque.recibirDaño(500, TipoDaño.FISICO));
        assertEquals(0, tanque.getVidaActual());
        assertFalse(tanque.estaVivo());
    }

    @Test
    void curarNoPasaDeLaVidaMaxima() {
        Mago mago = new Mago("Giovanni");
        mago.recibirDaño(30, TipoDaño.MAGICO);

        assertEquals(30, mago.curar(100));
        assertEquals(80, mago.getVidaActual());
        assertEquals(0, mago.curar(50));
    }

    @Test
    void defenderRecuperaDiezPorCientoDelManaYMarcaLaGuardia() {
        Mago mago = new Mago("Giovanni");
        mago.consumirMana(50);

        mago.defender();

        assertTrue(mago.estaDefendiendo());
        assertEquals(50, mago.getPorcentajeDeDañoRecibido());
        assertEquals(38, mago.getManaActual()); // 30 + 10% de 80

        // La guardia dura hasta el turno propio siguiente.
        mago.iniciarTurno();
        assertFalse(mago.estaDefendiendo());
        assertEquals(100, mago.getPorcentajeDeDañoRecibido());
    }

    @Test
    void elManaSeLlenaAlEmpezarElCombatePeroLaVidaNo() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.consumirMana(40);
        guerrero.recibirDaño(50, TipoDaño.FISICO);

        guerrero.alIniciarCombate();

        assertEquals(40, guerrero.getManaActual());
        assertEquals(70, guerrero.getVidaActual());
    }

    @Test
    void elGuerreroSubeDeNivelConLosIncrementosQueLeCorresponden() {
        Guerrero guerrero = new Guerrero("Lucas");

        assertEquals(1, guerrero.ganarExperiencia(Personaje.EXPERIENCIA_BASE_POR_NIVEL));

        assertEquals(2, guerrero.getNivel());
        assertEquals(132, guerrero.getVidaMaxima());
        assertEquals(43, guerrero.getManaMaximo());
        assertEquals(21, guerrero.getAtaqueFisicoEfectivo());
        assertEquals(14, guerrero.getDefensaFisicaEfectiva());
        // El nivel 2 pide 60 (30 x 2) y no sobro nada del anterior.
        assertEquals(60, guerrero.getExperienciaParaSubir());
        assertEquals(0, guerrero.getExperienciaActual());
    }

    @Test
    void unaSolaTandaDeExperienciaPuedeSubirVariosNiveles() {
        Arquero arquero = new Arquero("Santiago");

        // 30 para el nivel 2 + 60 para el 3 + 10 que quedan sueltos.
        assertEquals(2, arquero.ganarExperiencia(100));

        assertEquals(3, arquero.getNivel());
        assertEquals(10, arquero.getExperienciaActual());
        assertEquals(90, arquero.getExperienciaParaSubir());
    }

    @Test
    void subirDeNivelCuraLoQueSumaDeVidaMaxima() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.recibirDaño(100, TipoDaño.FISICO);
        assertEquals(20, guerrero.getVidaActual());

        guerrero.ganarExperiencia(30);

        assertEquals(132, guerrero.getVidaMaxima());
        assertEquals(32, guerrero.getVidaActual());
    }

    @Test
    void elNivelMaximoEsVeinte() {
        Tanque tanque = new Tanque("Maximo");

        tanque.ganarExperiencia(100000);

        assertEquals(Personaje.NIVEL_MAXIMO, tanque.getNivel());
        assertEquals(0, tanque.ganarExperiencia(5000));
    }

    @Test
    void nadiePuedeEquiparseElArmaDeOtraClase() {
        Mago mago = new Mago("Giovanni");
        Arma espadaDeGuerrero = new Arma("Espada corta", "Un filo honesto.", 60, Rareza.RARO,
                ClaseDePersonaje.GUERRERO, TipoDaño.FISICO, Map.of(Estadistica.ATAQUE_FISICO, 5));

        assertFalse(espadaDeGuerrero.puedeEquiparlo(mago));
        assertThrows(IllegalArgumentException.class, () -> mago.equipar(espadaDeGuerrero));
    }

    @Test
    void equiparSumaLasBonificacionesYDevuelveLoQueEstabaPuesto() {
        Guerrero guerrero = new Guerrero("Lucas");
        Arma espada = new Arma("Espada corta", "Un filo honesto.", 60, Rareza.RARO,
                ClaseDePersonaje.GUERRERO, TipoDaño.FISICO, Map.of(Estadistica.ATAQUE_FISICO, 5));
        Arma hacha = new Arma("Hacha pesada", "Lenta pero contundente.", 200, Rareza.EPICO,
                ClaseDePersonaje.GUERRERO, TipoDaño.FISICO, Map.of(Estadistica.ATAQUE_FISICO, 9));

        assertNull(guerrero.equipar(espada));
        assertEquals(23, guerrero.getAtaqueFisicoEfectivo());

        // Al cambiar de arma se descuenta la vieja antes de sumar la nueva.
        assertSame(espada, guerrero.equipar(hacha));
        assertEquals(27, guerrero.getAtaqueFisicoEfectivo());
        assertSame(hacha, guerrero.getArmaEquipada());
    }
}
