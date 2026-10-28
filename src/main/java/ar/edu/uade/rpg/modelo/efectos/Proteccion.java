package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;

/**
 * Baja un porcentaje el daño que recibe el protegido. Lo reparte el Guardian
 * sobre sus aliados cuando decide pasar su turno cubriendolos.
 *
 * Se apoya en el mismo hook que el Escudo, absorber(), pero con otra idea
 * detras: el Escudo tiene una bolsa de puntos que se gasta, la Proteccion
 * recorta un porcentaje de cada golpe mientras dure. Por eso son dos clases y
 * no una con un booleano adentro.
 *
 * Se multiplica con defender: un aliado protegido que ademas se defiende recibe
 * el 70% del 50%.
 */
public class Proteccion extends EfectoEstado {

    private static final int PORCENTAJE_TOTAL = 100;

    private final int reduccion;

    public Proteccion(int reduccion, int duracionEnTurnos) {
        super("Proteccion", duracionEnTurnos);
        this.reduccion = reduccion;
    }

    @Override
    public int absorber(int daño, TipoDaño tipo) {
        return daño * (PORCENTAJE_TOTAL - reduccion) / PORCENTAJE_TOTAL;
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
        // Alcanza con estar activo.
    }

    @Override
    public void alIniciarTurno(Combatiente objetivo) {
        // No actua por turno.
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // Nada que revertir.
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new Proteccion(reduccion, duracionEnTurnos);
    }

    public int getReduccion() {
        return reduccion;
    }
}
