package ar.edu.uade.rpg.modelo.tienda;

import java.util.List;

public class ResultadoTirada {
    private final List<Simbolo> simbolos;
    private final int multiplicador;
    private final int oroGanado;
    private final boolean fueGratis;

    public ResultadoTirada(List<Simbolo> simbolos, int multiplicador, int oroGanado,
                           boolean fueGratis) {
        this.simbolos = List.copyOf(simbolos);
        this.multiplicador = multiplicador;
        this.oroGanado = oroGanado;
        this.fueGratis = fueGratis;
    }

    public boolean fuePremio() {
        return multiplicador > 0;
    }

    public List<Simbolo> getSimbolos() {
        return simbolos;
    }

    public int getMultiplicador() {
        return multiplicador;
    }

    public int getOroGanado() {
        return oroGanado;
    }

    public boolean fueGratis() {
        return fueGratis;
    }
}
