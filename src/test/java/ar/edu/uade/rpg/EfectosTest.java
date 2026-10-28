package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.Tanque;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.DebuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;
import ar.edu.uade.rpg.modelo.efectos.Escudo;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

/**
 * Pruebas de los siete efectos de estado. Son todas deterministas: ningun
 * efecto tira dados, el azar vive en el CalculadorDeDaño y en
 * HabilidadDeEstado.
 */
class EfectosTest {

    @Test
    void elVenenoPegaUnPorcentajeDeLaVidaMaximaYNoDeLaActual() {
        Guerrero guerrero = new Guerrero("Lucas"); // 120 de vida
        guerrero.aplicarEfecto(new Veneno(6, 3));

        guerrero.procesarEfectosDeTurno();
        assertEquals(113, guerrero.getVidaActual()); // 6% de 120 = 7

        // Sigue pegando 7 aunque le quede menos vida: el calculo es sobre la maxima.
        guerrero.procesarEfectosDeTurno();
        assertEquals(106, guerrero.getVidaActual());
    }

    @Test
    void elVenenoNuncaPegaMasDelTope() {
        // Un Tanque de nivel alto pasa los 400 de vida: el 6% serian mas de 25.
        Tanque tanque = new Tanque("Maximo");
        tanque.ganarExperiencia(100000);
        assertTrue(tanque.getVidaMaxima() * 6 / 100 > Veneno.GOLPE_MAXIMO);

        int antes = tanque.getVidaActual();
        tanque.aplicarEfecto(new Veneno(6, 3));
        tanque.procesarEfectosDeTurno();

        assertEquals(Veneno.GOLPE_MAXIMO, antes - tanque.getVidaActual());
    }

    @Test
    void elVenenoDuraLosTurnosQueDiceYDespuesSeVa() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new Veneno(6, 3));

        for (int turno = 0; turno < 3; turno++) {
            guerrero.procesarEfectosDeTurno();
        }
        assertEquals(99, guerrero.getVidaActual()); // 120 - 7 x 3
        assertTrue(guerrero.getEfectosActivos().isEmpty());

        // Un cuarto turno ya no le hace nada.
        guerrero.procesarEfectosDeTurno();
        assertEquals(99, guerrero.getVidaActual());
    }

    @Test
    void elVenenoSobreviveAlCierreDeEtapaYLosDemasEfectosNo() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new Veneno(6, 3));
        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));
        guerrero.aplicarEfecto(new Escudo(40, 3));
        assertEquals(3, guerrero.getEfectosActivos().size());

        guerrero.limpiarEfectosTemporales();

        assertEquals(1, guerrero.getEfectosActivos().size());
        assertEquals("Veneno", guerrero.getEfectosActivos().get(0).getNombre());
    }

    @Test
    void cadaAplicacionUsaUnaCopiaPropiaDelEfecto() {
        Veneno plantilla = new Veneno(6, 3);
        Guerrero uno = new Guerrero("Uno");
        Mago otro = new Mago("Otro");

        EfectoEstado enUno = uno.aplicarEfecto(plantilla);
        EfectoEstado enOtro = otro.aplicarEfecto(plantilla);

        assertNotSame(plantilla, enUno);
        assertNotSame(enUno, enOtro);

        // Gastarle los 3 turnos a uno no se los gasta al otro.
        for (int turno = 0; turno < 3; turno++) {
            uno.procesarEfectosDeTurno();
        }
        assertTrue(uno.getEfectosActivos().isEmpty());
        assertEquals(1, otro.getEfectosActivos().size());
    }

    @Test
    void elEscudoSeComeElDanioHastaAgotarse() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new Escudo(40, 3));

        // Los primeros 30 los absorbe enteros.
        assertEquals(0, guerrero.recibirDaño(30, TipoDaño.FISICO));
        assertEquals(120, guerrero.getVidaActual());

        // Del golpe de 25 solo le quedan 10 de absorcion: pasan 15.
        assertEquals(15, guerrero.recibirDaño(25, TipoDaño.FISICO));
        assertEquals(105, guerrero.getVidaActual());

        // Agotado, deja pasar todo aunque le queden turnos.
        assertEquals(20, guerrero.recibirDaño(20, TipoDaño.FISICO));
        assertEquals(85, guerrero.getVidaActual());
    }

    @Test
    void elEscudoNoFrenaAlVeneno() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new Escudo(40, 3));
        guerrero.aplicarEfecto(new Veneno(6, 3));

        guerrero.procesarEfectosDeTurno();

        // El veneno usa recibirDañoDirecto(): no pasa por el escudo.
        assertEquals(113, guerrero.getVidaActual());
    }

    @Test
    void losBuffosSeSumanEntreSiYNoSeMultiplican() {
        Guerrero guerrero = new Guerrero("Lucas"); // 18 de ataque fisico
        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));
        assertEquals(22, guerrero.getAtaqueFisicoEfectivo()); // 18 x 1.25

        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));
        assertEquals(27, guerrero.getAtaqueFisicoEfectivo()); // 18 x 1.50, no x1.5625
    }

    @Test
    void elBuffoNoTocaLasDemasEstadisticas() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));

        assertEquals(12, guerrero.getDefensaFisicaEfectiva());
        assertEquals(10, guerrero.getVelocidadEfectiva());
    }

    @Test
    void alExpirarElBuffoLaEstadisticaVuelveSolaASuValor() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));
        assertEquals(22, guerrero.getAtaqueFisicoEfectivo());

        for (int turno = 0; turno < 3; turno++) {
            guerrero.procesarEfectosDeTurno();
        }
        assertEquals(18, guerrero.getAtaqueFisicoEfectivo());
    }

    @Test
    void elDebuffoRestaEnVezDeSumar() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new DebuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));

        assertEquals(13, guerrero.getAtaqueFisicoEfectivo()); // 18 x 0.75
    }

    @Test
    void unBuffoYUnDebuffoDeLaMismaEstadisticaSeCancelan() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.aplicarEfecto(new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));
        guerrero.aplicarEfecto(new DebuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3));

        assertEquals(18, guerrero.getAtaqueFisicoEfectivo());
    }

    @Test
    void elAturdimientoLeHacePerderElTurno() {
        Guerrero guerrero = new Guerrero("Lucas");
        assertTrue(guerrero.puedeActuar());

        guerrero.aplicarEfecto(new Aturdimiento(1));
        assertFalse(guerrero.puedeActuar());

        guerrero.procesarEfectosDeTurno();
        assertTrue(guerrero.puedeActuar());
    }

    @Test
    void unCaidoNoPuedeActuarAunqueNoTengaEfectos() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.recibirDaño(500, TipoDaño.FISICO);

        assertFalse(guerrero.puedeActuar());
    }
}
