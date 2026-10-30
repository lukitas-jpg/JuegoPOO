package ar.edu.uade.rpg.modelo.combate;

public class ResultadoDaño {
    private final int daño;
    private final boolean fueCritico;

    public ResultadoDaño(int daño, boolean fueCritico) {
        this.daño = daño;
        this.fueCritico = fueCritico;
    }

    public int getDaño() {
        return daño;
    }

    public boolean fueCritico() {
        return fueCritico;
    }
}
