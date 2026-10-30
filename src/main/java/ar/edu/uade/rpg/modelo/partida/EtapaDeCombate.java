package ar.edu.uade.rpg.modelo.partida;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.Combate;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;

public class EtapaDeCombate extends Etapa {
    private static final int RECOMPENSA_BASE = 25;
    private static final int RECOMPENSA_POR_ETAPA = 10;

    private final TipoEtapa tipo;
    private final List<Enemigo> enemigos;
    private final int experienciaBase;
    private final int oroBase;

    public EtapaDeCombate(int numero, TipoEtapa tipo, List<Enemigo> enemigos) {
        super(numero);
        this.tipo = tipo;
        this.enemigos = new ArrayList<>(enemigos);
        int recompensa = calcularRecompensa(numero, tipo);
        this.experienciaBase = recompensa;
        this.oroBase = recompensa;
    }

    private static int calcularRecompensa(int numero, TipoEtapa tipo) {
        int base = RECOMPENSA_BASE + RECOMPENSA_POR_ETAPA * numero;
        return tipo == TipoEtapa.NORMAL ? base : base * 2;
    }

    public Combate crearCombate(Party party) {
        return new Combate(party, enemigos, experienciaBase, oroBase);
    }

    public List<Enemigo> getEnemigos() {
        return List.copyOf(enemigos);
    }

    public boolean esJefe() {
        return tipo != TipoEtapa.NORMAL;
    }

    public TipoEtapa getTipo() {
        return tipo;
    }

    public int getExperienciaBase() {
        return experienciaBase;
    }

    public int getOroBase() {
        return oroBase;
    }

    @Override
    public String getDescripcion() {
        return switch (tipo) {
            case MINIJEFE -> "Etapa " + numero + ": el Verdugo te corta el paso.";
            case JEFE_FINAL -> "Etapa " + numero + ": el Señor de las Sombras te espera.";
            case NORMAL -> "Etapa " + numero + ": " + enemigos.size() + " enemigos.";
        };
    }

    @Override
    public boolean otorgaExperiencia() {
        return true;
    }

    @Override
    public boolean esDeCombate() {
        return true;
    }
}
