package ar.edu.uade.rpg.modelo.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;

public final class CatalogoDeEquipables {
    private CatalogoDeEquipables() {
    }

    public static List<Equipable> catalogoCompleto() {
        List<Equipable> lista = new ArrayList<>();

        lista.add(new Arma("Espada de Acero Templado", "Arma de Guerrero.",
                60, Rareza.RARO, ClaseDePersonaje.GUERRERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 5)));
        lista.add(new Arma("Mandoble del Juramento", "Arma de Guerrero.",
                200, Rareza.EPICO, ClaseDePersonaje.GUERRERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 12)));
        lista.add(new Arma("Filo del Primer Amanecer", "Arma de Guerrero.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.GUERRERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 24)));

        lista.add(new Arma("Baculo de Roble Runico", "Arma de Mago.",
                60, Rareza.RARO, ClaseDePersonaje.MAGO, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 6)));
        lista.add(new Arma("Cetro de Cuarzo Ardiente", "Arma de Mago.",
                200, Rareza.EPICO, ClaseDePersonaje.MAGO, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 14)));
        lista.add(new Arma("Vara del Eclipse", "Arma de Mago.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.MAGO, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 28)));

        lista.add(new Arma("Arco Corto de Tejo", "Arma de Arquero.",
                60, Rareza.RARO, ClaseDePersonaje.ARQUERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 4, Estadistica.VELOCIDAD, 2)));
        lista.add(new Arma("Arco Largo del Vendaval", "Arma de Arquero.",
                200, Rareza.EPICO, ClaseDePersonaje.ARQUERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 10, Estadistica.VELOCIDAD, 4)));
        lista.add(new Arma("Arco de la Luna Menguante", "Arma de Arquero.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.ARQUERO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 20, Estadistica.VELOCIDAD, 8)));

        lista.add(new Arma("Cetro de Plata Bendecida", "Arma de Curador.",
                60, Rareza.RARO, ClaseDePersonaje.CURADOR, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 5, Estadistica.DEFENSA_MAGICA, 2)));
        lista.add(new Arma("Baculo del Alba", "Arma de Curador.",
                200, Rareza.EPICO, ClaseDePersonaje.CURADOR, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 12, Estadistica.DEFENSA_MAGICA, 4)));
        lista.add(new Arma("Cetro de la Luz Primera", "Arma de Curador.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.CURADOR, TipoDaño.MAGICO,
                Map.of(Estadistica.ATAQUE_MAGICO, 24, Estadistica.DEFENSA_MAGICA, 8)));

        lista.add(new Arma("Daga de Hoja Negra", "Arma de Asesino.",
                60, Rareza.RARO, ClaseDePersonaje.ASESINO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 5, Estadistica.SUERTE, 3)));
        lista.add(new Arma("Colmillo Gemelo", "Arma de Asesino.",
                200, Rareza.EPICO, ClaseDePersonaje.ASESINO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 11, Estadistica.SUERTE, 6)));
        lista.add(new Arma("Estilete de la Ultima Sombra", "Arma de Asesino.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.ASESINO, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 22, Estadistica.SUERTE, 10)));

        lista.add(new Arma("Maza de Hierro Macizo", "Arma de Tanque.",
                60, Rareza.RARO, ClaseDePersonaje.TANQUE, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 4, Estadistica.DEFENSA_FISICA, 3)));
        lista.add(new Arma("Martillo del Yunque", "Arma de Tanque.",
                200, Rareza.EPICO, ClaseDePersonaje.TANQUE, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 9, Estadistica.DEFENSA_FISICA, 7)));
        lista.add(new Arma("Quebrantamontanas", "Arma de Tanque.",
                430, Rareza.LEGENDARIO, ClaseDePersonaje.TANQUE, TipoDaño.FISICO,
                Map.of(Estadistica.ATAQUE_FISICO, 18, Estadistica.DEFENSA_FISICA, 14)));

        lista.add(new Armadura("Cota de Malla Reforzada", "Armadura de Guerrero.",
                70, Rareza.RARO, ClaseDePersonaje.GUERRERO,
                Map.of(Estadistica.VIDA, 20, Estadistica.DEFENSA_FISICA, 4)));
        lista.add(new Armadura("Armadura de Placas del Bastion", "Armadura de Guerrero.",
                220, Rareza.EPICO, ClaseDePersonaje.GUERRERO,
                Map.of(Estadistica.VIDA, 50, Estadistica.DEFENSA_FISICA, 10, Estadistica.DEFENSA_MAGICA, 4)));
        lista.add(new Armadura("Egida del Rey Caido", "Armadura de Guerrero.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.GUERRERO,
                Map.of(Estadistica.VIDA, 100, Estadistica.DEFENSA_FISICA, 20, Estadistica.DEFENSA_MAGICA, 8)));

        lista.add(new Armadura("Tunica de Hilo Runico", "Armadura de Mago.",
                70, Rareza.RARO, ClaseDePersonaje.MAGO,
                Map.of(Estadistica.VIDA, 14, Estadistica.DEFENSA_MAGICA, 6, Estadistica.MANA, 10)));
        lista.add(new Armadura("Manto del Conclave", "Armadura de Mago.",
                220, Rareza.EPICO, ClaseDePersonaje.MAGO,
                Map.of(Estadistica.VIDA, 35, Estadistica.DEFENSA_MAGICA, 13, Estadistica.MANA, 25)));
        lista.add(new Armadura("Vestiduras del Vacio", "Armadura de Mago.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.MAGO,
                Map.of(Estadistica.VIDA, 70, Estadistica.DEFENSA_MAGICA, 26, Estadistica.MANA, 50)));

        lista.add(new Armadura("Jubon de Cuero Endurecido", "Armadura de Arquero.",
                70, Rareza.RARO, ClaseDePersonaje.ARQUERO,
                Map.of(Estadistica.VIDA, 16, Estadistica.DEFENSA_FISICA, 4, Estadistica.VELOCIDAD, 2)));
        lista.add(new Armadura("Atuendo del Montaraz", "Armadura de Arquero.",
                220, Rareza.EPICO, ClaseDePersonaje.ARQUERO,
                Map.of(Estadistica.VIDA, 40, Estadistica.DEFENSA_FISICA, 9, Estadistica.VELOCIDAD, 4)));
        lista.add(new Armadura("Vestimenta del Cazador Eterno", "Armadura de Arquero.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.ARQUERO,
                Map.of(Estadistica.VIDA, 80, Estadistica.DEFENSA_FISICA, 18, Estadistica.VELOCIDAD, 8)));

        lista.add(new Armadura("Habito de Lino Sagrado", "Armadura de Curador.",
                70, Rareza.RARO, ClaseDePersonaje.CURADOR,
                Map.of(Estadistica.VIDA, 16, Estadistica.DEFENSA_MAGICA, 5, Estadistica.MANA, 12)));
        lista.add(new Armadura("Vestiduras del Santuario", "Armadura de Curador.",
                220, Rareza.EPICO, ClaseDePersonaje.CURADOR,
                Map.of(Estadistica.VIDA, 40, Estadistica.DEFENSA_MAGICA, 11, Estadistica.MANA, 28)));
        lista.add(new Armadura("Manto de la Aurora", "Armadura de Curador.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.CURADOR,
                Map.of(Estadistica.VIDA, 80, Estadistica.DEFENSA_MAGICA, 22, Estadistica.MANA, 55)));

        lista.add(new Armadura("Traje de Contrabandista", "Armadura de Asesino.",
                70, Rareza.RARO, ClaseDePersonaje.ASESINO,
                Map.of(Estadistica.VIDA, 14, Estadistica.DEFENSA_FISICA, 4, Estadistica.SUERTE, 2)));
        lista.add(new Armadura("Sayo del Gremio Silencioso", "Armadura de Asesino.",
                220, Rareza.EPICO, ClaseDePersonaje.ASESINO,
                Map.of(Estadistica.VIDA, 35, Estadistica.DEFENSA_FISICA, 9, Estadistica.SUERTE, 4)));
        lista.add(new Armadura("Sudario del Sin Nombre", "Armadura de Asesino.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.ASESINO,
                Map.of(Estadistica.VIDA, 70, Estadistica.DEFENSA_FISICA, 18, Estadistica.SUERTE, 8)));

        lista.add(new Armadura("Armadura de Escamas Pesada", "Armadura de Tanque.",
                70, Rareza.RARO, ClaseDePersonaje.TANQUE,
                Map.of(Estadistica.VIDA, 26, Estadistica.DEFENSA_FISICA, 6)));
        lista.add(new Armadura("Coraza del Muro Inquebrantable", "Armadura de Tanque.",
                220, Rareza.EPICO, ClaseDePersonaje.TANQUE,
                Map.of(Estadistica.VIDA, 65, Estadistica.DEFENSA_FISICA, 14, Estadistica.DEFENSA_MAGICA, 5)));
        lista.add(new Armadura("Baluarte de Obsidiana", "Armadura de Tanque.",
                470, Rareza.LEGENDARIO, ClaseDePersonaje.TANQUE,
                Map.of(Estadistica.VIDA, 130, Estadistica.DEFENSA_FISICA, 28, Estadistica.DEFENSA_MAGICA, 10)));

        return lista;
    }

    public static List<Equipable> filtrar(List<ClaseDePersonaje> clases, Rareza rareza) {
        List<Equipable> filtrados = new ArrayList<>();
        for (Equipable equipable : catalogoCompleto()) {
            if (equipable.getRareza() == rareza && clases.contains(equipable.getClaseRequerida())) {
                filtrados.add(equipable);
            }
        }
        return filtrados;
    }
}
