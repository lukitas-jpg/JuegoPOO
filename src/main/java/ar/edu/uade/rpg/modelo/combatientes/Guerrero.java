package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

public final class Guerrero extends Personaje {
    public Guerrero(String nombre) {
        super(nombre);
        vidaMaxima = 120;
        vidaActual = 120;
        manaMaximo = 40;
        manaActual = 40;
        ataqueFisico = 18;
        defensaFisica = 12;
        ataqueMagico = 4;
        defensaMagica = 8;
        velocidad = 10;
        suerte = 8;

        cargarMovimientos();
    }

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueFisico(
                "Tajo", "Un corte rapido con el filo del arma.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        agregarMovimiento(new AtaqueFisico(
                "Golpe Demoledor", "Descarga todo el peso del arma sobre un enemigo.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 18));

        agregarMovimiento(new HabilidadDeEstado(
                "Grito de Guerra", "Sube el ataque fisico de toda la party un 25% durante 3 turnos.",
                12, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3), 100));

        agregarMovimiento(new AtaqueFisico(
                "Torbellino", "Gira sobre si mismo golpeando a todos los enemigos.",
                18, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_DEFINITIVA, true, 16));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.GUERRERO;
    }

    @Override
    public String getDescripcionClase() {
        return "Equilibrado: pega fuerte en fisico y aguanta golpes.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(12);
        subirManaMaximo(3);
        ataqueFisico += 3;
        defensaFisica += 2;
        defensaMagica += 1;
        velocidad += 1;
        suerte += 1;
    }
}
