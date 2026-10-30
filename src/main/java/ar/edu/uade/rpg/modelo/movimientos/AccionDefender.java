package ar.edu.uade.rpg.modelo.movimientos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class AccionDefender implements Accion {
    private final Combatiente autor;

    public AccionDefender(Combatiente autor) {
        this.autor = autor;
    }

    @Override
    public ResultadoAccion ejecutar() {
        int manaAntes = autor.getManaActual();
        autor.defender();

        ResultadoAccion resultado = new ResultadoAccion(getDescripcion());
        resultado.agregarObjetivoAfectado(autor);
        int recuperado = autor.getManaActual() - manaAntes;
        if (recuperado > 0) {
            resultado.setDescripcion(getDescripcion() + " Recupera " + recuperado + " de mana.");
        }
        return resultado;
    }

    @Override
    public String getDescripcion() {
        return autor.getNombre() + " se defiende.";
    }
}
