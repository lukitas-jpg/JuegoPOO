package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Meter combatientes nuevos en el campo. Solo la usa el Nigromante con sus
 * esqueletos.
 *
 * Los invocados NO se agregan al combate desde aca: viajan dentro del
 * ResultadoAccion y es Combate quien los incorpora. Esa vuelta es lo que evita
 * la dependencia circular: si el Nigromante llamara a Combate.agregarCombatiente(),
 * cada enemigo tendria que conocer al Combate que lo esta jugando. Asi la
 * informacion sube y la decision la toma quien corresponde.
 */
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
