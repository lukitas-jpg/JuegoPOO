package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

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

    private int calcularCuracion(Combatiente usuario) {
        return cantidadBase + usuario.getAtaqueMagicoEfectivo();
    }

    public int getCantidadBase() {
        return cantidadBase;
    }
}
