package ar.edu.uade.rpg.modelo.items;

import java.util.List;

public final class CatalogoDeConsumibles {
    private CatalogoDeConsumibles() {
    }

    public static Consumible pocionDeVida() {
        return new Consumible("Pocion de Vida",
                "Cura el 55% de la vida maxima del objetivo.",
                25, Rareza.COMUN, new EfectoCuracion(55), 12);
    }

    public static Consumible elixirDeMana() {
        return new Consumible("Elixir de Mana",
                "Restaura el 50% del mana maximo.",
                20, Rareza.COMUN, new EfectoRestaurarMana(50), 8);
    }

    public static Consumible antidoto() {
        return new Consumible("Antidoto",
                "Quita el Veneno y la Quemadura.",
                15, Rareza.COMUN, new EfectoCurarEstado(List.of("Veneno", "Quemadura")), 6);
    }

    public static Consumible polvoDeRenacer() {
        return new Consumible("Polvo de Renacer",
                "Revive a un caido con el 50% de su vida.",
                120, Rareza.COMUN, new EfectoRevivir(50), 3);
    }

    public static Consumible piedraDeAfilar() {
        return new Consumible("Piedra de Afilar",
                "Sube un 30% el ataque durante 3 turnos.",
                40, Rareza.COMUN, new EfectoBuffTemporal(30, 3), 6);
    }

    public static List<Consumible> catalogoCompleto() {
        return List.of(pocionDeVida(), elixirDeMana(), antidoto(),
                       polvoDeRenacer(), piedraDeAfilar());
    }
}
