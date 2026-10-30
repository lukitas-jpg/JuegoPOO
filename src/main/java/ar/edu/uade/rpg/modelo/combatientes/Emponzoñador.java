package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoSinEfecto;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;
import ar.edu.uade.rpg.modelo.movimientos.HabilidadDeEstado;

public final class Emponzoñador extends Enemigo implements ObjetivoSinEfecto {
    private static final String EFECTO_BUSCADO = "Veneno";

    public Emponzoñador() {
        super("Emponzoñador");
        vidaMaxima = 60; vidaActual = 60;
        ataqueFisico = 12; defensaFisica = 8; defensaMagica = 7;
        velocidad = 10; suerte = 8;
        agregarMovimiento(new HabilidadDeEstado("Escupitajo Toxico",
                "Envenena al objetivo: pierde 6% de su vida maxima por turno, 3 turnos.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, new Veneno(6, 3), 100));
        agregarMovimiento(new AtaqueFisico("Mordisco", "Lo que le queda cuando ya estan todos verdes.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 9));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        List<Combatiente> vivos = filtrarVivos(party);
        Combatiente objetivo = elegirObjetivo(vivos);
        boolean yaEstaEnvenenado = objetivo.getEfectosActivos().stream()
                .anyMatch(efecto -> efecto.getNombre().equals(EFECTO_BUSCADO));
        int movimiento = yaEstaEnvenenado ? 1 : 0;
        return usar(getMovimientos().get(movimiento), List.of(objetivo), calculador);
    }

    @Override
    public String getEfectoBuscado() {
        return EFECTO_BUSCADO;
    }

    @Override
    public Combatiente elegirDeRespaldo(List<Combatiente> vivos) {
        return vivos.isEmpty() ? null : vivos.get(random.nextInt(vivos.size()));
    }
}
