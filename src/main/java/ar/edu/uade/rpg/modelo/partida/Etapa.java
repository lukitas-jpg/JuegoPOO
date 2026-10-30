package ar.edu.uade.rpg.modelo.partida;

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
