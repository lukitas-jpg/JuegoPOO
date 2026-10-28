package ar.edu.uade.rpg.modelo.items;

import java.util.List;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Saca efectos de estado por nombre. El Antidoto lo usa para quitar el Veneno
 * y la Quemadura, que son justamente los dos unicos que se arrastran de una
 * etapa a la siguiente.
 *
 * Guarda los nombres en una lista en vez de tener un metodo por efecto: el dia
 * que haya un tercer estado que se arrastre, se agrega a la lista del item y no
 * hay codigo nuevo.
 */
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

    /** No tiene sentido gastarlo en alguien que no tiene ninguno de esos efectos. */
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
