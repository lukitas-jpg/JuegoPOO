package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class AccionUsarMovimiento implements Accion {
    private final Combatiente autor;
    private final Movimiento movimiento;
    private final List<Combatiente> objetivos;
    private final CalculadorDeDaño calculador;

    public AccionUsarMovimiento(Combatiente autor, Movimiento movimiento,
                                List<Combatiente> objetivos, CalculadorDeDaño calculador) {
        this.autor = autor;
        this.movimiento = movimiento;
        this.objetivos = List.copyOf(objetivos);
        this.calculador = calculador;
    }

    @Override
    public ResultadoAccion ejecutar() {
        autor.consumirMana(movimiento.getCostoMana());
        if (movimiento.esDefinitivo()) {
            movimiento.iniciarCooldown();
        }
        return movimiento.aplicar(autor, objetivos, calculador);
    }

    @Override
    public String getDescripcion() {
        return autor.getNombre() + " usa " + movimiento.getNombre() + ".";
    }

    public Movimiento getMovimiento() {
        return movimiento;
    }
}
