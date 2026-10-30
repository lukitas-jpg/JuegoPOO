package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Inventario;

public class AccionUsarConsumible implements Accion {
    private final Combatiente autor;
    private final Consumible consumible;
    private final Combatiente objetivo;
    private final Inventario inventario;

    public AccionUsarConsumible(Combatiente autor, Consumible consumible,
                                Combatiente objetivo, Inventario inventario) {
        this.autor = autor;
        this.consumible = consumible;
        this.objetivo = objetivo;
        this.inventario = inventario;
    }

    @Override
    public ResultadoAccion ejecutar() {
        ResultadoAccion resultado = new ResultadoAccion(getDescripcion());

        if (!consumible.puedeUsarseSobre(objetivo)) {
            resultado.setDescripcion(consumible.getNombre() + " no se puede usar sobre "
                    + objetivo.getNombre() + ".");
            resultado.agregarObjetivoFallado(objetivo);
            return resultado;
        }

        int vidaAntes = objetivo.getVidaActual();
        consumible.usarSobre(objetivo);
        inventario.quitar(consumible);

        resultado.agregarObjetivoAfectado(objetivo);
        int curado = objetivo.getVidaActual() - vidaAntes;
        if (curado > 0) {
            resultado.setDescripcion(getDescripcion() + " Recupera " + curado + " de vida.");
        }
        return resultado;
    }

    @Override
    public String getDescripcion() {
        return autor.getNombre() + " usa " + consumible.getNombre()
                + " sobre " + objetivo.getNombre() + ".";
    }

    public Consumible getConsumible() {
        return consumible;
    }
}
