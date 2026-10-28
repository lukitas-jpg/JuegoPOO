package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * El de mas ataque fisico, mas velocidad y mas suerte, y tambien el mas
 * fragil en defensas. Pensado para resolver la pelea antes de recibir.
 */
public final class Asesino extends Personaje {

    public Asesino(String nombre) {
        super(nombre);
        vidaMaxima = 85;
        vidaActual = 85;
        manaMaximo = 45;
        manaActual = 45;
        ataqueFisico = 19;
        defensaFisica = 7;
        ataqueMagico = 4;
        defensaMagica = 7;
        velocidad = 18;
        suerte = 18;

        cargarMovimientos();
    }

    /**
     * Los 4 movimientos de esta clase. Son instancias PROPIAS de este
     * personaje y no se comparten: dos personajes de la misma clase en la
     * party tienen cada uno sus movimientos, con su propio cooldown.
     */
    private void cargarMovimientos() {
        // 1. Basico: gratis y desde el arranque.
        agregarMovimiento(new AtaqueFisico(
                "Puñalada", "Una estocada corta y precisa.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        // 2. Mismo veneno que el Dardo Toxico del Arquero, pero instancia propia.
        agregarMovimiento(new HabilidadDeEstado(
                "Hoja Envenenada", "Envenena al objetivo: pierde 6% de su vida maxima por turno, 3 turnos.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false,
                new Veneno(6, 3), 100));

        // 3. Se lo tira a si mismo: mas velocidad es actuar antes en cada ronda.
        agregarMovimiento(new HabilidadDeEstado(
                "Sombra Agil", "Sube su propia velocidad un 40% durante 3 turnos.",
                10, AlcanceMovimiento.UNO_MISMO, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.VELOCIDAD, 40, 3), 100));

        // 4. Definitiva: la potencia mas alta del juego, a un solo objetivo.
        agregarMovimiento(new AtaqueFisico(
                "Golpe Mortal", "Busca el punto exacto y descarga todo ahi.",
                20, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_DEFINITIVA, true, 35));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.ASESINO;
    }

    @Override
    public String getDescripcionClase() {
        return "El mas rapido y el que mas critica, pero no aguanta un golpe.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(8);
        subirManaMaximo(4);
        ataqueFisico += 3;
        defensaFisica += 1;
        defensaMagica += 1;
        velocidad += 2;
        suerte += 2;
    }
}
