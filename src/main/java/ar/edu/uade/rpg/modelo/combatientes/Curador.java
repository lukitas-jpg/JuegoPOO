package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeCuracion;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * El unico que sostiene a la party entre etapas: la vida no se repone sola al
 * terminar un combate, solo el mana. Pega poco pero tiene el mana mas alto.
 */
public final class Curador extends Personaje {

    public Curador(String nombre) {
        super(nombre);
        vidaMaxima = 90;
        vidaActual = 90;
        manaMaximo = 90;
        manaActual = 90;
        ataqueFisico = 5;
        defensaFisica = 9;
        ataqueMagico = 14;
        defensaMagica = 15;
        velocidad = 9;
        suerte = 8;

        cargarMovimientos();
    }

    /**
     * Los 4 movimientos de esta clase. Son instancias PROPIAS de este
     * personaje y no se comparten: dos personajes de la misma clase en la
     * party tienen cada uno sus movimientos, con su propio cooldown.
     */
    private void cargarMovimientos() {
        // 1. Basico: gratis y desde el arranque. Es magico, no fisico.
        agregarMovimiento(new AtaqueMagico(
                "Destello Sagrado", "Un fogonazo de luz que quema al enemigo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 9));

        // 2. Cura 20 + su ataque magico, asi que mejora sola al subir de nivel.
        agregarMovimiento(new HabilidadDeCuracion(
                "Curacion", "Devuelve vida a un aliado: 20 mas su ataque magico.",
                10, AlcanceMovimiento.ALIADO, NIVEL_INICIAL, false, 20));

        // 3. Apoyo defensivo: el buff mas alto del juego en porcentaje.
        agregarMovimiento(new HabilidadDeEstado(
                "Bendicion", "Sube la defensa fisica de toda la party un 30% durante 3 turnos.",
                14, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.DEFENSA_FISICA, 30, 3), 100));

        // 4. Definitiva: cura menos por cabeza que Curacion, pero a los tres. No revive.
        agregarMovimiento(new HabilidadDeCuracion(
                "Luz Restauradora", "Cura a toda la party: 15 mas su ataque magico. No levanta caidos.",
                26, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_DEFINITIVA, true, 15));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.CURADOR;
    }

    @Override
    public String getDescripcionClase() {
        return "Sostiene a la party entre etapas; es el que mas mana tiene.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(8);
        subirManaMaximo(6);
        defensaFisica += 1;
        ataqueMagico += 2;
        defensaMagica += 2;
        velocidad += 1;
        suerte += 1;
    }
}
