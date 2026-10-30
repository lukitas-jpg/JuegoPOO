package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class AccionInvocar implements Accion {
    private final Combatiente invocador;
    private final List<Combatiente> invocados;

    public AccionInvocar(Combatiente invocador, List<Combatiente> invocados) {
        this.invocador = invocador;
        this.invocados = List.copyOf(invocados);
    }

    @Override
    public ResultadoAccion ejecutar() {
        ResultadoAccion resultado = new ResultadoAccion(getDescripcion());
        for (Combatiente invocado : invocados) {
            resultado.agregarCombatienteInvocado(invocado);
        }
        return resultado;
    }

    @Override
    public String getDescripcion() {
        return invocador.getNombre() + " invoca " + invocados.size()
                + (invocados.size() == 1 ? " esqueleto." : " esqueletos.");
    }
}
