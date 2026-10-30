package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combate.GestorDeTurnos;
import ar.edu.uade.rpg.modelo.combatientes.Bruto;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.AccionDefender;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.partida.Party;

class TurnosTest {
    private Combate combateContraUnBruto() {
        Party party = new Party(List.of(new Guerrero("Lucas"), new Mago("Giovanni")));
        List<Enemigo> enemigos = new ArrayList<>(List.of(new Bruto()));
        Combate combate = new Combate(party, enemigos, 10, 10, new Random(7));
        combate.iniciar();
        return combate;
    }

    private void jugarHastaElTurnoDe(Combate combate, Combatiente buscado) {
        for (int i = 0; i < 20; i++) {
            Combatiente actual = combate.getSiguienteEnActuar();
            if (actual == buscado) {
                return;
            }
            if (combate.puedeActuarElSiguiente()) {
                combate.ejecutarAccion(new AccionDefender(actual));
            } else {
                combate.perderTurno();
            }
        }
        throw new AssertionError("nunca le toco a " + buscado.getNombre());
    }

    private void cerrarElTurnoActual(Combate combate) {
        Combatiente actual = combate.getSiguienteEnActuar();
        combate.ejecutarAccion(new AccionDefender(actual));
    }

    @Test
    void unAturdimientoDeUnTurnoHacePerderExactamenteUnTurno() {
        Combate combate = combateContraUnBruto();
        Combatiente primero = combate.getSiguienteEnActuar();
        primero.aplicarEfecto(new Aturdimiento(1));

        assertFalse(combate.puedeActuarElSiguiente(), "aturdido no juega este turno");
        combate.perderTurno();

        jugarHastaElTurnoDe(combate, primero);
        assertTrue(combate.puedeActuarElSiguiente(), "en la ronda siguiente ya juega");
        assertEquals(2, combate.getNumeroRonda());
    }

    @Test
    void elPrimeroDeCadaRondaTambienBajaSusCooldowns() {
        Combate combate = combateContraUnBruto();
        Combatiente primero = combate.getSiguienteEnActuar();
        Movimiento definitiva = primero.getMovimientos().get(3);
        definitiva.iniciarCooldown();
        int alEmpezar = definitiva.getCooldownRestante();

        cerrarElTurnoActual(combate);
        jugarHastaElTurnoDe(combate, primero);

        assertEquals(alEmpezar - 1, definitiva.getCooldownRestante());
    }

    @Test
    void elVenenoLePegaAlPrimeroDeCadaRonda() {
        Combate combate = combateContraUnBruto();
        Combatiente primero = combate.getSiguienteEnActuar();
        primero.aplicarEfecto(new Veneno(6, 3));
        int vidaAntes = primero.getVidaActual();

        cerrarElTurnoActual(combate);

        assertTrue(primero.getVidaActual() < vidaAntes, "el veneno pega al cerrar su turno");
    }

    @Test
    void laGuardiaDuraHastaElTurnoPropioSiguiente() {
        Combate combate = combateContraUnBruto();
        Combatiente primero = combate.getSiguienteEnActuar();

        cerrarElTurnoActual(combate);
        assertTrue(primero.estaDefendiendo(), "sigue en guardia durante la ronda");

        jugarHastaElTurnoDe(combate, primero);
        assertFalse(primero.estaDefendiendo(), "la baja al arrancar su turno");
    }

    @Test
    void unaAccionArmadaAfueraCierraElTurnoDeQuienCorresponde() {
        Combate combate = combateContraUnBruto();
        Combatiente primero = combate.getSiguienteEnActuar();
        cerrarElTurnoActual(combate);

        primero.aplicarEfecto(new Veneno(6, 3));
        int vidaAntes = primero.getVidaActual();
        combate.ejecutarAccion(new AccionDefender(primero));

        assertEquals(vidaAntes, primero.getVidaActual(), "el veneno no le pega en un turno ajeno");
    }

    @Test
    void siCaeAlguienQueYaActuoElSiguienteNoPierdeSuTurno() {
        Guerrero lucas = new Guerrero("Lucas");
        Mago giovanni = new Mago("Giovanni");
        Bruto bruto = new Bruto();
        GestorDeTurnos gestor = new GestorDeTurnos(List.of(lucas, giovanni, bruto), new Random(3));
        gestor.iniciarNuevaRonda();
        List<Combatiente> orden = gestor.getOrdenPrevisto();

        gestor.consumirTurno();
        assertSame(orden.get(1), gestor.getSiguienteEnActuar());

        orden.get(0).recibirDaño(10_000, TipoDaño.FISICO);
        gestor.retirar(orden.get(0));

        assertSame(orden.get(1), gestor.getSiguienteEnActuar(),
                "el que tenia el turno lo sigue teniendo");
        gestor.consumirTurno();
        assertSame(orden.get(2), gestor.getSiguienteEnActuar(),
                "y despues le toca al tercero, no se lo saltea");
    }
}
