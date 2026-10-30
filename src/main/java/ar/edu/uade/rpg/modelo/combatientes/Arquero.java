package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.Aturdimiento;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

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

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueFisico(
                "Disparo Certero", "Una flecha apuntada con calma.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 10));

        agregarMovimiento(new HabilidadDeEstado(
                "Dardo Toxico", "Envenena al objetivo: pierde 6% de su vida maxima por turno, 3 turnos.",
                8, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false,
                new Veneno(6, 3), 100));

        agregarMovimiento(new AtaqueFisico(
                "Lluvia de Flechas", "Cubre el campo de flechas.",
                14, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_TERCER_MOVIMIENTO, false, 10));

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
