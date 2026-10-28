package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;

/**
 * Aplica un efecto de estado sobre uno o varios objetivos: veneno, mejora,
 * escudo o aturdimiento.
 *
 * Guarda el efecto como PLANTILLA y es Combatiente.aplicarEfecto() quien lo
 * clona en cada aplicacion. Si se aplicara siempre la misma instancia, dos
 * enemigos envenenados compartirian el mismo Veneno y al expirar en uno se le
 * iria tambien al otro.
 *
 * La tirada es INDIVIDUAL por objetivo: Andanada Cegadora se tira una vez por
 * cada enemigo, no una sola vez para todos. Contra cuatro enemigos son cuatro
 * tiradas separadas, y que entren las cuatro es poco probable aunque cada una
 * sea del 40%.
 */
public class HabilidadDeEstado extends Habilidad {

    private static final int PORCENTAJE_TOTAL = 100;

    private final EfectoEstado plantillaEfecto;
    private final int probabilidadDeAcierto;
    private final Random random;

    public HabilidadDeEstado(String nombre, String descripcion, int costoMana,
            AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo,
            EfectoEstado plantillaEfecto, int probabilidadDeAcierto) {
        this(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo,
             plantillaEfecto, probabilidadDeAcierto, new Random());
    }

    /** Con Random inyectado, para que los tests no dependan de la suerte. */
    public HabilidadDeEstado(String nombre, String descripcion, int costoMana,
            AlcanceMovimiento alcance, int nivelDesbloqueo, boolean esDefinitivo,
            EfectoEstado plantillaEfecto, int probabilidadDeAcierto, Random random) {
        super(nombre, descripcion, costoMana, alcance, nivelDesbloqueo, esDefinitivo);
        this.plantillaEfecto = plantillaEfecto;
        this.probabilidadDeAcierto = probabilidadDeAcierto;
        this.random = random;
    }

    @Override
    public ResultadoAccion aplicar(Combatiente usuario, List<Combatiente> objetivos,
                                   CalculadorDeDaño calculador) {
        ResultadoAccion resultado = new ResultadoAccion(usuario.getNombre() + " usa " + nombre + ".");
        for (Combatiente objetivo : resolverObjetivos(usuario, objetivos)) {
            if (!objetivo.estaVivo()) {
                resultado.agregarObjetivoFallado(objetivo);
            } else if (acierta(objetivo)) {
                resultado.agregarEfectoAplicado(objetivo.aplicarEfecto(plantillaEfecto));
                resultado.agregarObjetivoAfectado(objetivo);
            } else {
                resultado.agregarObjetivoFallado(objetivo);
            }
        }
        return resultado;
    }

    /**
     * Un efecto declarado al 100% entra siempre: son el veneno y las mejoras
     * propias, que por diseño no fallan nunca. La resistencia del objetivo
     * recorta solo lo que ya era una apuesta.
     *
     * La resistencia es un numero del combatiente (0 en casi todos, 50 en
     * jefes y minijefes), asi que un aturdimiento del 40% entra al 20% contra
     * un jefe. Sin instanceof y sin un metodo esJefe(): cada combatiente sabe
     * cuanto aguanta, y mañana se puede poner en 25 o en 70 sin tocar nada mas.
     */
    private boolean acierta(Combatiente objetivo) {
        if (probabilidadDeAcierto >= PORCENTAJE_TOTAL) {
            return true;
        }
        int probabilidad = probabilidadDeAcierto
                * (PORCENTAJE_TOTAL - objetivo.getResistenciaAEstados()) / PORCENTAJE_TOTAL;
        return random.nextInt(PORCENTAJE_TOTAL) < probabilidad;
    }

    public EfectoEstado getPlantillaEfecto() {
        return plantillaEfecto;
    }

    public int getProbabilidadDeAcierto() {
        return probabilidadDeAcierto;
    }
}
