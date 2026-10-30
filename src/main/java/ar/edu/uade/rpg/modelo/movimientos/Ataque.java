package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combate.ResultadoDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

public abstract class Ataque extends Movimiento {
    protected final int potencia;
    protected final TipoDaño tipoDaño;

    protected Ataque(String nombre, String descripcion, int costoMana,
                     AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo,
                     int potencia, TipoDaño tipoDaño) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo);
        this.potencia = potencia;
        this.tipoDaño = tipoDaño;
    }

    @Override
    public ResultadoAccion aplicar(Combatiente usuario, List<Combatiente> objetivos,
                                   CalculadorDeDaño calculador) {
        ResultadoAccion resultado = new ResultadoAccion(usuario.getNombre() + " usa " + nombre + ".");
        for (Combatiente objetivo : resolverObjetivos(usuario, objetivos)) {
            ResultadoDaño golpe = calculador.calcular(usuario, objetivo, potencia, tipoDaño);

            resultado.sumarDaño(objetivo.recibirDaño(golpe.getDaño(), tipoDaño));
            if (golpe.fueCritico()) {
                resultado.marcarCritico();
            }
            resultado.agregarObjetivoAfectado(objetivo);
        }
        return resultado;
    }

    public int getPotencia() {
        return potencia;
    }

    public TipoDaño getTipoDaño() {
        return tipoDaño;
    }
}
