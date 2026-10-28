package ar.edu.uade.rpg.modelo.combate;

/**
 * Lo que devuelve el CalculadorDeDaño: cuanto daño salio y si fue critico.
 *
 * Son dos datos que viajan juntos y que el que pregunta necesita a la vez: el
 * daño para descontarlo y el critico para mostrarlo en el registro. Devolver
 * solo el int obligaria a preguntar dos veces y a repetir la tirada de suerte,
 * que daria otro resultado.
 *
 * Es inmutable: una vez calculado el golpe, no cambia.
 */
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
