package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;

public final class Mago extends Personaje {
    public Mago(String nombre) {
        super(nombre);
        vidaMaxima = 80;
        vidaActual = 80;
        manaMaximo = 80;
        manaActual = 80;
        ataqueFisico = 4;
        defensaFisica = 6;
        ataqueMagico = 20;
        defensaMagica = 14;
        velocidad = 11;
        suerte = 8;

        cargarMovimientos();
    }

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueMagico(
                "Chispa Arcana", "Una descarga breve de energia.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        agregarMovimiento(new AtaqueMagico(
                "Bola de Fuego", "Una esfera de fuego que estalla contra un enemigo.",
                10, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 20));

        agregarMovimiento(new AtaqueMagico(
                "Cadena de Rayos", "Un rayo que salta de enemigo en enemigo.",
                16, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_TERCER_MOVIMIENTO, false, 11));

        agregarMovimiento(new AtaqueMagico(
                "Meteoro", "Hace caer una roca ardiente sobre el campo entero.",
                28, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_DEFINITIVA, true, 22));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.MAGO;
    }

    @Override
    public String getDescripcionClase() {
        return "El mayor daño magico del juego, a cambio de ser el mas fragil.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(7);
        subirManaMaximo(6);
        defensaFisica += 1;
        ataqueMagico += 3;
        defensaMagica += 2;
        velocidad += 1;
        suerte += 1;
    }
}
