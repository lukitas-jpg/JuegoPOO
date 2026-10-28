package ar.edu.uade.rpg.controlador;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.EfectoDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.MovimientoDTO;
import ar.edu.uade.rpg.dto.PartyDTO;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;
import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.Inventario;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.modelo.partida.Party;

/**
 * Arma los DTOs a partir de objetos del modelo. Lo usan los tres
 * controladores, por eso esta aparte (GRASP Pure Fabrication): asi cada
 * controlador se ocupa del flujo y no de copiar campos.
 *
 * Tambien decide el nombre de la imagen de cada cosa. Es el unico lugar que
 * conoce la convencion de nombres de src/main/resources/imagenes/.
 */
final class ConversorDTO {

    private ConversorDTO() {
    }

    // ------------------------------------------------------------ combatientes

    static CombatienteDTO personaje(Personaje personaje, boolean tieneElTurno) {
        return new CombatienteDTO(
                personaje.getNombre(),
                "personajes/" + nombreDeArchivo(personaje.getClaseDePersonaje().name()),
                true,
                personaje.getNivel(),
                personaje.getExperienciaActual(),
                personaje.getExperienciaParaSubir(),
                personaje.getVidaActual(), personaje.getVidaMaxima(),
                personaje.getManaActual(), personaje.getManaMaximo(),
                personaje.estaVivo(), personaje.estaDefendiendo(), tieneElTurno,
                efectos(personaje),
                nombreONada(personaje.getArmaEquipada()),
                nombreONada(personaje.getArmaduraEquipada()));
    }

    static CombatienteDTO enemigo(Combatiente enemigo, boolean tieneElTurno) {
        return new CombatienteDTO(
                enemigo.getNombre(),
                "enemigos/" + nombreDeArchivo(enemigo.getClass().getSimpleName()),
                false, 0, 0, 0,
                enemigo.getVidaActual(), enemigo.getVidaMaxima(),
                enemigo.getManaActual(), enemigo.getManaMaximo(),
                enemigo.estaVivo(), enemigo.estaDefendiendo(), tieneElTurno,
                efectos(enemigo), null, null);
    }

    static List<CombatienteDTO> miembros(Party party) {
        List<CombatienteDTO> lista = new ArrayList<>();
        for (Personaje miembro : party.getMiembros()) {
            lista.add(personaje(miembro, false));
        }
        return lista;
    }

    private static List<EfectoDTO> efectos(Combatiente combatiente) {
        List<EfectoDTO> lista = new ArrayList<>();
        for (EfectoEstado efecto : combatiente.getEfectosActivos()) {
            lista.add(new EfectoDTO(efecto.getNombre(),
                    "estados/" + nombreDeArchivo(efecto.getClass().getSimpleName()),
                    efecto.getDuracionEnTurnos()));
        }
        return lista;
    }

    // -------------------------------------------------------------- movimientos

    static MovimientoDTO movimiento(Movimiento movimiento, Combatiente usuario, boolean disponible) {
        return new MovimientoDTO(
                movimiento.getNombre(),
                movimiento.getDescripcion(),
                movimiento.getCostoMana(),
                disponible,
                disponible ? "" : motivoNoDisponible(movimiento, usuario),
                movimiento.requiereElegirObjetivo());
    }

    private static String motivoNoDisponible(Movimiento movimiento, Combatiente usuario) {
        if (usuario instanceof Personaje personaje && !movimiento.estaDesbloqueado(personaje)) {
            return "Nivel " + movimiento.getNivelDesbloqueo();
        }
        if (movimiento.estaEnCooldown()) {
            return "Espera " + movimiento.getCooldownRestante();
        }
        return "Sin maná";
    }

    // ------------------------------------------------------------------ objetos

    static ItemDTO consumible(Consumible consumible, int cantidad, boolean disponible) {
        return new ItemDTO(consumible.getNombre(), consumible.getDescripcion(),
                imagenDe(consumible), imagenDe(consumible), consumible.getPrecio(),
                nombreDeArchivo(consumible.getRareza().name()),
                cantidad, consumible.getTopeEnInventario(), disponible);
    }

    static ItemDTO equipable(Equipable equipable, boolean disponible) {
        return new ItemDTO(equipable.getNombre(), equipable.getDescripcion(),
                "equipables/" + archivoDeObjeto(equipable.getNombre()),
                "equipables/" + nombreDeArchivo(equipable.getSlot().name())
                        + "_" + nombreDeArchivo(equipable.getRareza().name()),
                equipable.getPrecio(),
                nombreDeArchivo(equipable.getRareza().name()), 0, 1, disponible);
    }

    /** Ej: "+12 ataque fisico, +4 velocidad". */
    static String bonificaciones(Equipable equipable) {
        List<String> partes = new ArrayList<>();
        for (Map.Entry<Estadistica, Integer> bonificacion : equipable.getBonificaciones().entrySet()) {
            partes.add("+" + bonificacion.getValue() + " "
                    + bonificacion.getKey().name().toLowerCase().replace('_', ' '));
        }
        return String.join(", ", partes);
    }

    /**
     * Una entrada por objeto distinto, en el orden en que se compraron. La
     * posicion en esta lista es el indice que la vista devuelve.
     */
    static List<ItemDTO> inventario(Inventario inventario) {
        List<ItemDTO> lista = new ArrayList<>();
        for (Consumible consumible : distintosDelInventario(inventario)) {
            lista.add(consumible(consumible, inventario.contar(consumible.getNombre()), true));
        }
        return lista;
    }

    /** Una unidad de cada objeto distinto: la que se usa si el jugador lo elige. */
    static List<Consumible> distintosDelInventario(Inventario inventario) {
        List<Consumible> distintos = new ArrayList<>();
        List<String> vistos = new ArrayList<>();
        for (Consumible consumible : inventario.getConsumibles()) {
            if (!vistos.contains(consumible.getNombre())) {
                vistos.add(consumible.getNombre());
                distintos.add(consumible);
            }
        }
        return distintos;
    }

    // -------------------------------------------------------------------- party

    static PartyDTO party(Juego juego) {
        Party party = juego.getPartida().getParty();
        return new PartyDTO(
                juego.getPartida().getEtapaActual(),
                juego.getEtapaActual().getDescripcion(),
                party.getOro(),
                miembros(party),
                inventario(party.getInventario()));
    }

    // ---------------------------------------------------------------- imagenes

    /** EfectoCuracion -> consumibles/curacion */
    private static String imagenDe(Consumible consumible) {
        String clase = consumible.getEfecto().getClass().getSimpleName().replaceFirst("^Efecto", "");
        return "consumibles/" + nombreDeArchivo(clase);
    }

    /**
     * Los archivos van en minuscula y sin ñ, por seguridad dentro de un .jar:
     * SeñorDeLasSombras -> seniordelassombras.
     */
    static String nombreDeArchivo(String nombre) {
        return nombre.toLowerCase().replace("ñ", "ni");
    }

    /**
     * El dibujo propio de un objeto: su nombre en minuscula, sin tildes y con
     * guiones bajos. "Espada de Acero Templado" -> espada_de_acero_templado.
     */
    static String archivoDeObjeto(String nombre) {
        String sinTildes = Normalizer.normalize(nombreDeArchivo(nombre), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim().replaceAll("[^a-z0-9]+", "_");
    }

    private static String nombreONada(Equipable equipable) {
        return equipable == null ? null : equipable.getNombre();
    }
}
