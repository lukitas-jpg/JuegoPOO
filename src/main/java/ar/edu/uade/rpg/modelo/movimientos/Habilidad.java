package ar.edu.uade.rpg.modelo.movimientos;

/**
 * Movimiento que NO hace daño. De aca bajan los dos que quedan: curar
 * (HabilidadDeCuracion) y aplicar un efecto de estado (HabilidadDeEstado).
 *
 * Es la otra mitad de la division que parte en dos el arbol de movimientos.
 * No agrega atributos ni metodos: su trabajo es que la division exista, para
 * que el resto del juego pueda preguntar "esto pega o no pega" por el tipo y
 * no por el nombre del movimiento. Una curacion y un veneno comparten mucho
 * mas entre si que cualquiera de los dos con un Torbellino.
 */
public abstract class Habilidad extends Movimiento {

    protected Habilidad(String nombre, String descripcion, int costoMana,
                        AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo);
    }
}
