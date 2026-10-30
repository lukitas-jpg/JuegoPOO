package ar.edu.uade.rpg.modelo.movimientos;

public abstract class Habilidad extends Movimiento {
    protected Habilidad(String nombre, String descripcion, int costoMana,
                        AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo);
    }
}
