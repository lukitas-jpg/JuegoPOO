package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Sube la vida de uno o varios aliados. Son las dos del Curador.
 *
 * La curacion es cantidadBase + el ataque magico de quien cura. Esa suma es lo
 * que mantiene vivo al Curador como clase: si fuera un numero fijo, curaria 20
 * en la etapa 1 y seguiria curando 20 en la etapa 20, cuando los golpes pasan
 * de 80. Atado al ataque magico, el Curador mejora al subir de nivel y se
 * beneficia de equiparse como cualquier otro.
 *
 * NO revive: a un caido no se le cura nada. Para eso esta el consumible, que
 * es la unica forma de levantar a alguien.
 */
public class HabilidadDeCuracion extends Habilidad {

    private final int cantidadBase;

    public HabilidadDeCuracion(String nombre, String descripcion, int costoMana,
            AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo,
            int cantidadBase) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo);
        this.cantidadBase = cantidadBase;
    }

    @Override
    public ResultadoAccion aplicar(Combatiente usuario, List<Combatiente> objetivos,
                                   CalculadorDeDaño calculador) {
        ResultadoAccion resultado = new ResultadoAccion(usuario.getNombre() + " usa " + nombre + ".");
        int curacion = calcularCuracion(usuario);

        for (Combatiente objetivo : resolverObjetivos(usuario, objetivos)) {
            if (objetivo.estaVivo()) {
                objetivo.curar(curacion);
                resultado.agregarObjetivoAfectado(objetivo);
            } else {
                resultado.agregarObjetivoFallado(objetivo);
            }
        }
        return resultado;
    }

    /**
     * Se calcula UNA vez por uso y no una vez por objetivo. Con Luz
     * Restauradora da lo mismo, pero si en algun momento la curacion pasara a
     * depender de algo que cambia mientras se resuelve, los tres miembros de
     * la party tienen que recibir lo mismo.
     */
    private int calcularCuracion(Combatiente usuario) {
        return cantidadBase + usuario.getAtaqueMagicoEfectivo();
    }

    public int getCantidadBase() {
        return cantidadBase;
    }
}
