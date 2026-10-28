package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;

/**
 * El que mas daño magico hace y el que menos aguanta. Vive de su mana: con
 * 80 de maximo puede sostener varios hechizos por pelea.
 */
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

    /**
     * Los 4 movimientos de esta clase. Son instancias PROPIAS de este
     * personaje y no se comparten: dos personajes de la misma clase en la
     * party tienen cada uno sus movimientos, con su propio cooldown.
     */
    private void cargarMovimientos() {
        // 1. Basico: gratis y desde el arranque.
        agregarMovimiento(new AtaqueMagico(
                "Chispa Arcana", "Una descarga breve de energia.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        // 2. La potencia mas alta a objetivo unico que hay antes de nivel 8.
        agregarMovimiento(new AtaqueMagico(
                "Bola de Fuego", "Una esfera de fuego que estalla contra un enemigo.",
                10, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 20));

        // 3. Cambia potencia por alcance: 11 a todos en vez de 20 a uno.
        agregarMovimiento(new AtaqueMagico(
                "Cadena de Rayos", "Un rayo que salta de enemigo en enemigo.",
                16, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_TERCER_MOVIMIENTO, false, 11));

        // 4. Definitiva: la mas cara del juego y la unica que pega 22 en area.
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
