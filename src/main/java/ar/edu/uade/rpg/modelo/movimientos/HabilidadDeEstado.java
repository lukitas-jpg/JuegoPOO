package ar.edu.uade.rpg.modelo.movimientos;

import java.util.List;
import java.util.Random;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;

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
