package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.BuffEstadistica;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeCuracion;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

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

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueMagico(
                "Destello Sagrado", "Un fogonazo de luz que quema al enemigo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 9));

        agregarMovimiento(new HabilidadDeCuracion(
                "Curacion", "Devuelve vida a un aliado: 20 mas su ataque magico.",
                10, AlcanceMovimiento.ALIADO, NIVEL_INICIAL, false, 20));

        agregarMovimiento(new HabilidadDeEstado(
                "Bendicion", "Sube la defensa fisica de toda la party un 30% durante 3 turnos.",
                14, AlcanceMovimiento.TODA_LA_PARTY, NIVEL_TERCER_MOVIMIENTO, false,
                new BuffEstadistica(Estadistica.DEFENSA_FISICA, 30, 3), 100));

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
