package ar.edu.uade.rpg.modelo.tienda;

import java.util.List;

/**
 * Lo que dejo una tirada de la maquina: los tres simbolos que salieron, el
 * multiplicador que pagaron y el oro resultante.
 *
 * Viajan los tres simbolos y no solo el premio porque la pantalla tiene que
 * mostrar los rodillos. Es inmutable: una tirada ya tirada no cambia.
 */
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
