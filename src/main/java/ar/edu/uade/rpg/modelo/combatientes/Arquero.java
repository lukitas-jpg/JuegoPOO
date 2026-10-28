package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * Daño fisico a distancia con buena velocidad y la segunda mejor suerte, asi
 * que es el que mas seguido saca criticos despues del Asesino.
 */
public final class Arquero extends Personaje {

    public Arquero(String nombre) {
        super(nombre);
        vidaMaxima = 95;
        vidaActual = 95;
        manaMaximo = 50;
        manaActual = 50;
        ataqueFisico = 16;
        defensaFisica = 8;
        ataqueMagico = 6;
        defensaMagica = 9;
        velocidad = 14;
        suerte = 14;

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
                "Disparo Certero", "Una flecha apuntada con calma.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        // 2. No hace daño al tirarlo: el daño llega despues y no lo frena la defensa.
        agregarMovimiento(new HabilidadDeEstado(
                "Dardo Toxico", "Envenena al objetivo: pierde 6% de su vida maxima por turno, 3 turnos.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false,
                new Veneno(6, 3), 100));

        // 3. Misma potencia que el basico, pero a todos y por 14 de mana.
        agregarMovimiento(new AtaqueFisico(
                "Lluvia de Flechas", "Cubre el campo de flechas.",
                14, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_TERCER_MOVIMIENTO, false, 10));

        // 4. Definitiva: no hace daño, hace perder turnos. Tirada individual por enemigo.
        agregarMovimiento(new HabilidadDeEstado(
                "Andanada Cegadora", "Puede aturdir 1 turno a cada enemigo, con un 40% de acierto por objetivo.",
                22, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_DEFINITIVA, true,
                new Aturdimiento(1), 40));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.ARQUERO;
    }

    @Override
    public String getDescripcionClase() {
        return "Daño fisico constante, rapido y con buena chance de critico.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(9);
        subirManaMaximo(4);
        ataqueFisico += 3;
        defensaFisica += 1;
        defensaMagica += 1;
        velocidad += 2;
        suerte += 1;
    }
}
