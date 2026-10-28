package ar.edu.uade.rpg.modelo.partida;

/**
 * Una de las 20 paradas de la run. Hay dos tipos: pelear o comprar.
 *
 * Los tres metodos abstractos son la razon por la que Juego no tiene un solo
 * instanceof ni un cast: cada subclase responde por si misma si da experiencia
 * y si es de combate. Agregar un tercer tipo de etapa seria agregar una clase,
 * sin tocar al Juego.
 */
public abstract class Etapa {

    protected final int numero;
    protected boolean completada;

    protected Etapa(int numero) {
        this.numero = numero;
        this.completada = false;
    }

    public int getNumero() {
        return numero;
    }

    public boolean estaCompletada() {
        return completada;
    }

    public void marcarCompletada() {
        completada = true;
    }

    public abstract String getDescripcion();

    public abstract boolean otorgaExperiencia();

    public abstract boolean esDeCombate();
}
