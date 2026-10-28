package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * Equilibrado y resistente, el punto de partida mas seguro. Pega fuerte en
 * fisico, aguanta golpes y no depende de la magia.
 */
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

    /**
     * Los 4 movimientos de esta clase. Son instancias PROPIAS de este
     * personaje y no se comparten: dos personajes de la misma clase en la
     * party tienen cada uno sus movimientos, con su propio cooldown.
     */
    private void cargarMovimientos() {
        // 1. Basico: gratis y desde el arranque.
        agregarMovimiento(new AtaqueFisico(
                "Tajo", "Un corte rapido con el filo del arma.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        // 2. El golpe fuerte de siempre: casi el doble de potencia por 8 de mana.
        agregarMovimiento(new AtaqueFisico(
                "Golpe Demoledor", "Descarga todo el peso del arma sobre un enemigo.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 18));

        // 3. Apoyo: no pega, prepara a la party para los turnos siguientes.
        agregarMovimiento(new HabilidadDeEstado(
                "Grito de Guerra", "Sube el ataque fisico de toda la party un 25% durante 3 turnos.",
                12, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3), 100));

        // 4. Definitiva: menos potencia que Golpe Demoledor, pero contra todos.
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
