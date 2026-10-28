package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.Escudo;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

/**
 * El muro: casi el doble de vida que el Mago y la defensa fisica mas alta. A
 * cambio es el mas lento, asi que casi siempre actua ultimo.
 */
public final class Tanque extends Personaje {

    public Tanque(String nombre) {
        super(nombre);
        vidaMaxima = 150;
        vidaActual = 150;
        manaMaximo = 40;
        manaActual = 40;
        ataqueFisico = 12;
        defensaFisica = 18;
        ataqueMagico = 3;
        defensaMagica = 13;
        velocidad = 6;
        suerte = 6;

        cargarMovimientos();
    }

    /**
     * Los 4 movimientos de esta clase. Son instancias PROPIAS de este
     * personaje y no se comparten: dos personajes de la misma clase en la
     * party tienen cada uno sus movimientos, con su propio cooldown.
     */
    private void cargarMovimientos() {
        // 1. Basico: gratis y desde el arranque. Pega poco, como corresponde.
        agregarMovimiento(new AtaqueFisico(
                "Golpe de Escudo", "Un empujon con el borde del escudo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 9));

        // 2. 40 de absorcion propia: aguanta un golpe entero de las primeras etapas.
        agregarMovimiento(new HabilidadDeEstado(
                "Muro de Hierro", "Se cubre con un escudo que absorbe 40 de daño durante 3 turnos.",
                10, AlcanceMovimiento.UNO_MISMO, NIVEL_INICIAL, false,
                new Escudo(40, 3), 100));

        // 3. Se planta: mas defensa fisica propia, para aguantar mientras cubre.
        agregarMovimiento(new HabilidadDeEstado(
                "Postura Firme", "Se planta firme: sube un 25% su defensa fisica durante 3 turnos.",
                15, AlcanceMovimiento.UNO_MISMO, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.DEFENSA_FISICA, 25, 3), 100));

        // 4. Definitiva: menos absorcion que Muro de Hierro, pero para los tres.
        agregarMovimiento(new HabilidadDeEstado(
                "Baluarte", "Cubre a toda la party con un escudo que absorbe 30 de daño durante 3 turnos.",
                24, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_DEFINITIVA, true,
                new Escudo(30, 3), 100));
    }

    @Override
    public ClaseDePersonaje getClaseDePersonaje() {
        return ClaseDePersonaje.TANQUE;
    }

    @Override
    public String getDescripcionClase() {
        return "El muro: la vida y la defensa fisica mas altas, y el mas lento.";
    }

    @Override
    protected void aplicarCrecimientoPorNivel() {
        subirVidaMaxima(15);
        subirManaMaximo(3);
        ataqueFisico += 2;
        defensaFisica += 3;
        defensaMagica += 2;
        suerte += 1;
    }
}
