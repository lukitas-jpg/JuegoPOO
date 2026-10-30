package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

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

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueFisico(
                "Puñalada", "Una estocada corta y precisa.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        agregarMovimiento(new HabilidadDeEstado(
                "Hoja Envenenada", "Envenena al objetivo: pierde 6% de su vida maxima por turno, 3 turnos.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false,
                new Veneno(6, 3), 100));

        agregarMovimiento(new HabilidadDeEstado(
                "Sombra Agil", "Sube su propia velocidad un 40% durante 3 turnos.",
                10, AlcanceMovimiento.UNO_MISMO, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.VELOCIDAD, 40, 3), 100));

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
