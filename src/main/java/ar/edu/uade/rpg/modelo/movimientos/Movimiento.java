package ar.edu.uade.rpg.modelo.movimientos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;

/**
 * Algo que un combatiente puede hacer en su turno. De aca bajan dos familias
 * que no se mezclan: Ataque (hace daño) y Habilidad (no hace daño: cura o
 * aplica un efecto de estado).
 *
 * Ni el ataque basico ni la definitiva son clases aparte. El basico es un
 * AtaqueFisico o AtaqueMagico con costoMana = 0 y nivelDesbloqueo = 1, y la
 * definitiva es cualquier Movimiento con esDefinitivo = true. La variedad del
 * juego sale de los datos, no de multiplicar clases: los 24 movimientos se
 * resuelven con solo 4 clases concretas.
 */
public abstract class Movimiento implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Turnos propios que hay que esperar para repetir una definitiva. */
    public static final int COOLDOWN_DEFINITIVA = 4;

    protected String nombre;
    protected String descripcion;
    protected int costoMana;
    protected AlcanceMovimiento alcance;
    protected int nivelDesbloqueo;
    protected boolean esDefinitivo;
    protected int cooldownMaximo;
    protected int cooldownRestante;

    protected Movimiento(String nombre, String descripcion, int costoMana,
                         AlcanceMovimiento alcance, int nivelDesbloqueo,
                         boolean esDefinitivo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoMana = costoMana;
        this.alcance = alcance;
        this.nivelDesbloqueo = nivelDesbloqueo;
        this.esDefinitivo = esDefinitivo;
        this.cooldownMaximo = esDefinitivo ? COOLDOWN_DEFINITIVA : 0;
        this.cooldownRestante = 0;
    }

    /**
     * Lo resuelve cada subclase: ahi esta la diferencia entre pegar y curar.
     *
     * El CalculadorDeDaño entra por parametro y no como atributo porque vive
     * lo que dura el combate, mientras que el movimiento pertenece al
     * personaje y lo acompaña toda la partida. Guardarlo adentro seria dejar
     * al movimiento con una referencia a algo que ya no existe en cuanto
     * termina la etapa. Las habilidades lo ignoran: no hacen daño.
     */
    public abstract ResultadoAccion aplicar(Combatiente usuario, List<Combatiente> objetivos,
                                            CalculadorDeDaño calculador);

    // ------------------------------------------------------- disponibilidad

    /**
     * El desbloqueo es DERIVADO, no un estado que se guarde: se compara el
     * nivel del personaje con el nivel que pide el movimiento cada vez que se
     * consulta. Por eso al subir de nivel no hay nada que marcar.
     */
    public boolean estaDesbloqueado(Personaje personaje) {
        return personaje.getNivel() >= nivelDesbloqueo;
    }

    /** Junta las condiciones que la vista necesita para mostrarlo habilitado. */
    public boolean estaDisponible(Combatiente usuario) {
        return !estaEnCooldown() && usuario.tieneManaSuficiente(costoMana);
    }

    public boolean estaEnCooldown() {
        return cooldownRestante > 0;
    }

    public void iniciarCooldown() {
        cooldownRestante = cooldownMaximo;
    }

    public void reducirCooldown() {
        if (cooldownRestante > 0) {
            cooldownRestante--;
        }
    }

    /** Se llama al empezar cada etapa: las definitivas arrancan disponibles. */
    public void reiniciarCooldown() {
        cooldownRestante = 0;
    }

    // ------------------------------------------------------------- objetivos

    /**
     * Devuelve a quienes puede apuntar este movimiento. Si la lista tiene mas
     * de uno y el alcance es de objetivo unico, el jugador elige; si el alcance
     * abarca a todos, la lista ya es la definitiva.
     */
    public List<Combatiente> getObjetivosPosibles(List<Combatiente> aliados,
                                                  List<Combatiente> enemigos) {
        return switch (alcance) {
            case OBJETIVO_UNICO, TODOS_LOS_ENEMIGOS -> new ArrayList<>(enemigos);
            case ALIADO, TODA_LA_PARTY -> new ArrayList<>(aliados);
            case UNO_MISMO -> new ArrayList<>();
        };
    }

    /**
     * true si el jugador tiene que elegir a quien apuntar (uno entre varios).
     * Si abarca a todos o es sobre uno mismo, no hay nada que elegir.
     */
    public boolean requiereElegirObjetivo() {
        return alcance == AlcanceMovimiento.OBJETIVO_UNICO || alcance == AlcanceMovimiento.ALIADO;
    }

    /**
     * Los movimientos de alcance UNO_MISMO (Muro de Hierro, Sombra Agil) no
     * piden objetivo: getObjetivosPosibles() devuelve la lista vacia porque no
     * hay nada que elegir. Aca se cierra ese caso de una vez para las tres
     * familias, asi Combate no tiene que acordarse de pasar al usuario.
     */
    protected List<Combatiente> resolverObjetivos(Combatiente usuario,
                                                   List<Combatiente> objetivos) {
        return alcance == AlcanceMovimiento.UNO_MISMO ? List.of(usuario) : objetivos;
    }

    // ------------------------------------------------------------- consultas

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCostoMana() {
        return costoMana;
    }

    public AlcanceMovimiento getAlcance() {
        return alcance;
    }

    public int getNivelDesbloqueo() {
        return nivelDesbloqueo;
    }

    public boolean esDefinitivo() {
        return esDefinitivo;
    }

    public int getCooldownRestante() {
        return cooldownRestante;
    }
}
