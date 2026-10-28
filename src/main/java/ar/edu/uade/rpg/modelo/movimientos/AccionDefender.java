package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Defender: el turno completo se gasta en cubrirse. Hasta el proximo turno
 * propio recibe la mitad del daño directo y recupera un 10% de su mana maximo.
 *
 * No es un Movimiento y por eso no esta en la lista de los 4: no tiene costo,
 * ni nivel de desbloqueo, ni objetivo, ni cooldown. Es una accion y nada mas,
 * que es exactamente lo que pide la consigna cuando dice que en cada turno se
 * puede "atacar, defender, usar habilidad o usar un item".
 */
public class AccionDefender implements Accion {

    private final Combatiente autor;

    public AccionDefender(Combatiente autor) {
        this.autor = autor;
    }

    @Override
    public ResultadoAccion ejecutar() {
        int manaAntes = autor.getManaActual();
        autor.defender();

        ResultadoAccion resultado = new ResultadoAccion(getDescripcion());
        resultado.agregarObjetivoAfectado(autor);
        int recuperado = autor.getManaActual() - manaAntes;
        if (recuperado > 0) {
            resultado.setDescripcion(getDescripcion() + " Recupera " + recuperado + " de mana.");
        }
        return resultado;
    }

    @Override
    public String getDescripcion() {
        return autor.getNombre() + " se defiende.";
    }
}
