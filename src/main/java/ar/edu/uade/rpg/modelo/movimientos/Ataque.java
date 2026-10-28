package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combate.ResultadoDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

/**
 * Movimiento que hace daño. Resuelve el golpe una vez por objetivo, asi el
 * mismo codigo sirve para un Tajo a uno solo y para un Torbellino a todos: lo
 * unico que cambia es el largo de la lista.
 *
 * La potencia NO es el daño. Es el numero que entra a la formula del
 * CalculadorDeDaño junto con el ataque de quien pega y la defensa de quien
 * recibe. Por eso Tajo con potencia 10 hace mas en manos del Guerrero que en
 * las del Mago.
 */
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

            // El daño que devuelve recibirDaño() puede ser menor que el
            // calculado: el escudo del objetivo se come una parte. Al registro
            // va el que entro de verdad.
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
