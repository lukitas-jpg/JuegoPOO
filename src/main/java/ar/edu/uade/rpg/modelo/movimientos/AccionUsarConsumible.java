package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Inventario;

/**
 * Usar un consumible del inventario. Gasta el turno igual que atacar, y por eso
 * es una Accion y no algo aparte: la consigna pide que en cada turno se pueda
 * "atacar, defender, usar habilidad o usar un item", y las cuatro pasan por el
 * mismo lugar.
 *
 * Es la unica Accion que toca el inventario, y lo descuenta recien DESPUES de
 * comprobar que el objeto se puede usar sobre ese objetivo. Asi no se pierde un
 * Polvo de Renacer por tirarselo a alguien que estaba vivo.
 */
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
