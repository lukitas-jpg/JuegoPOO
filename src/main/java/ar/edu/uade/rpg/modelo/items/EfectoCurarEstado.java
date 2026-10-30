package ar.edu.uade.rpg.modelo.items;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

public class EfectoCurarEstado implements EfectoConsumible {
    private final List<String> efectosQueQuita;

    public EfectoCurarEstado(List<String> efectosQueQuita) {
        this.efectosQueQuita = List.copyOf(efectosQueQuita);
    }

    @Override
    public void aplicar(Combatiente objetivo) {
        for (String nombre : efectosQueQuita) {
            objetivo.quitarEfecto(nombre);
        }
    }

    @Override
    public boolean puedeAplicarseA(Combatiente objetivo) {
        if (!objetivo.estaVivo()) {
            return false;
        }
        return objetivo.getEfectosActivos().stream()
                .anyMatch(efecto -> efectosQueQuita.contains(efecto.getNombre()));
    }

    @Override
    public String getDescripcion() {
        return "Quita " + String.join(" y ", efectosQueQuita) + ".";
    }

    public List<String> getEfectosQueQuita() {
        return efectosQueQuita;
    }
}
