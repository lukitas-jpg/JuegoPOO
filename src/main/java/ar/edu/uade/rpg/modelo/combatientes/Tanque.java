package ar.edu.uade.rpg.modelo.combatientes;

import ar.edu.uade.rpg.modelo.efectos.DebuffEstadistica;
import ar.edu.uade.rpg.modelo.efectos.Escudo;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

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

    private void cargarMovimientos() {
        agregarMovimiento(new AtaqueFisico(
                "Golpe de Escudo", "Un empujon con el borde del escudo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, NIVEL_INICIAL, false, 9));

        agregarMovimiento(new HabilidadDeEstado(
                "Muro de Hierro", "Se cubre con un escudo que absorbe 40 de daño durante 3 turnos.",
                10, AlcanceMovimiento.UNO_MISMO, NIVEL_INICIAL, false,
                new Escudo(40, 3), 100));

        agregarMovimiento(new HabilidadDeEstado(
                "Rugido Intimidante", "Baja el ataque fisico de todos los enemigos un 25% durante 3 turnos.",
                15, AlcanceMovimiento.TODOS_LOS_ENEMIGOS, NIVEL_TERCER_MOVIMIENTO, false,
                new DebuffEstadistica(Estadistica.ATAQUE_FISICO, 25, 3), 100));

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
