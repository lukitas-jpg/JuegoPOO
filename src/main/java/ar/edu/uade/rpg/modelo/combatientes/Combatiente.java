package ar.edu.uade.rpg.modelo.combatientes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;

/**
 * Todo lo que participa de un combate y puede recibir daño: los personajes del
 * jugador y los enemigos. Esta clase guarda lo que ambos comparten (las ocho
 * estadisticas, la lista de movimientos, los efectos de estado activos) y las
 * operaciones que no dependen de quien sea: recibir daño, curarse, gastar
 * mana, defenderse.
 *
 * Es abstracta porque un Combatiente suelto no existe: siempre es un Personaje
 * o un Enemigo. Lo que cambia entre esas dos ramas (si sube de nivel, como
 * elige a quien atacar) queda fuera de aca.
 *
 * Importante: esta clase NO calcula el daño. El daño llega ya calculado desde
 * CalculadorDeDaño y aca solo se descuenta de la vida despues de pasar por los
 * escudos. Asi la formula vive en un solo lugar y no se repite en cada clase.
 */
public abstract class Combatiente implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Porcentaje de la vida/mana que representa el total, para los calculos. */
    private static final int PORCENTAJE_TOTAL = 100;

    /** Reduccion que aplica defender sobre el daño directo recibido. */
    private static final int REDUCCION_AL_DEFENDER = 50;

    /** Mana que recupera quien decide defenderse, sobre su mana maximo. */
    private static final int MANA_RECUPERADO_AL_DEFENDER = 10;

    protected String nombre;

    protected int vidaMaxima;
    protected int vidaActual;
    protected int manaMaximo;
    protected int manaActual;

    protected int ataqueFisico;
    protected int defensaFisica;
    protected int ataqueMagico;
    protected int defensaMagica;
    protected int velocidad;
    protected int suerte;

    /**
     * Cuanto le cuesta a un efecto de estado prenderse en este combatiente,
     * en porcentaje. 0 en los comunes y 50 en jefes y minijefes, asi un
     * aturdimiento de 40% les entra al 20%. Es un numero y no un metodo
     * esJefe() para poder graduarlo sin preguntar de que clase es nadie.
     */
    protected int resistenciaAEstados;

    /** Se levanta al defender y se baja al empezar el turno siguiente propio. */
    protected boolean defendiendo;

    protected final List<Movimiento> movimientos = new ArrayList<>();
    protected final List<EfectoEstado> efectosActivos = new ArrayList<>();

    protected Combatiente(String nombre) {
        this.nombre = nombre;
        this.resistenciaAEstados = 0;
        this.defendiendo = false;
    }

    // ------------------------------------------------------------ vida y daño

    /**
     * Descuenta de la vida el daño que le manda CalculadorDeDaño. Antes de
     * tocar la vida le da la oportunidad a cada efecto activo de absorber una
     * parte: el Escudo devuelve menos de lo que recibio y el resto de los
     * efectos lo dejan pasar igual.
     *
     * @return el daño que efectivamente entro, que es lo que se muestra en el
     *         registro del combate y no siempre coincide con el que entro por
     *         parametro.
     */
    public int recibirDaño(int daño, TipoDaño tipo) {
        int dañoRestante = daño;
        for (EfectoEstado efecto : efectosActivos) {
            dañoRestante = efecto.absorber(dañoRestante, tipo);
        }
        if (dañoRestante < 0) {
            dañoRestante = 0;
        }
        if (dañoRestante > vidaActual) {
            dañoRestante = vidaActual;
        }
        vidaActual -= dañoRestante;
        return dañoRestante;
    }

    /**
     * Daño que no pasa por defensas ni por escudos: el del veneno. Lo usa el
     * Veneno desde su alTerminarTurno.
     */
    public int recibirDañoDirecto(int daño) {
        int dañoAplicado = Math.min(daño, vidaActual);
        vidaActual -= dañoAplicado;
        return dañoAplicado;
    }

    /** Cura sin pasarse de la vida maxima. Devuelve lo que realmente curo. */
    public int curar(int cantidad) {
        int curacion = Math.min(cantidad, vidaMaxima - vidaActual);
        vidaActual += curacion;
        return curacion;
    }

    /**
     * Lo usa el consumible que revive. Solo funciona sobre un caido y lo deja
     * con el porcentaje de vida que indique el item, nunca en cero.
     */
    public void revivir(int porcentajeVida) {
        if (estaVivo()) {
            return;
        }
        vidaActual = Math.max(1, vidaMaxima * porcentajeVida / PORCENTAJE_TOTAL);
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }

    // ------------------------------------------------------------------ mana

    public boolean tieneManaSuficiente(int costo) {
        return manaActual >= costo;
    }

    public void consumirMana(int cantidad) {
        manaActual -= cantidad;
        if (manaActual < 0) {
            manaActual = 0;
        }
    }

    public void recuperarMana(int cantidad) {
        manaActual = Math.min(manaActual + cantidad, manaMaximo);
    }

    // --------------------------------------------------------------- defender

    /**
     * Defender es una accion de turno completo: no se puede defender y atacar
     * en el mismo turno. Hasta el proximo turno propio recibe la mitad del daño
     * directo y de paso recupera un 10% de su mana maximo, que es lo que hace
     * que defender sirva aunque no haya nada que esquivar.
     */
    public void defender() {
        defendiendo = true;
        recuperarMana(manaMaximo * MANA_RECUPERADO_AL_DEFENDER / PORCENTAJE_TOTAL);
    }

    public boolean estaDefendiendo() {
        return defendiendo;
    }

    /**
     * Porcentaje al que queda el daño recibido por estar defendiendo: 50 si
     * defiende y 100 si no. CalculadorDeDaño lo consulta en vez de preguntar
     * por el booleano y decidir el numero por su cuenta.
     */
    public int getPorcentajeDeDañoRecibido() {
        return defendiendo ? REDUCCION_AL_DEFENDER : PORCENTAJE_TOTAL;
    }

    // -------------------------------------------------------------- efectos

    /**
     * Aplica una COPIA del efecto, nunca la instancia que vino por parametro:
     * si no, dos envenenados compartirian el mismo Veneno y al expirar en uno
     * se le iria al otro.
     *
     * Devuelve la copia que quedo activa, para que el ResultadoAccion informe
     * el efecto real con su duracion y no la plantilla del movimiento.
     */
    public EfectoEstado aplicarEfecto(EfectoEstado efecto) {
        EfectoEstado copia = efecto.copiar();
        efectosActivos.add(copia);
        copia.alAplicarse(this);
        return copia;
    }

    /**
     * Arranque del turno propio: baja la guardia que habia levantado el turno
     * anterior y acerca un turno las definitivas. Los efectos NO se procesan
     * aca: si el aturdimiento se descontara al arrancar, uno de 1 turno se
     * venceria justo antes de consultarlo y nunca haria nada.
     */
    public void iniciarTurno() {
        defendiendo = false;
        reducirCooldowns();
    }

    /**
     * Cierre del turno propio, lo haya jugado o lo haya perdido aturdido. Cada
     * efecto hace lo suyo (el veneno pega aca) y en la misma pasada se le
     * descuenta un turno de duracion y se sacan los que se vencieron: primero
     * actua, despues envejece.
     */
    public void procesarEfectosDeTurno() {
        Iterator<EfectoEstado> iterador = efectosActivos.iterator();
        while (iterador.hasNext()) {
            EfectoEstado efecto = iterador.next();
            efecto.alTerminarTurno(this);
            efecto.reducirDuracion();
            if (efecto.haExpirado()) {
                efecto.alExpirar(this);
                iterador.remove();
            }
        }
    }

    /** true si algun efecto activo le hace perder el turno (aturdimiento). */
    public boolean puedeActuar() {
        if (!estaVivo()) {
            return false;
        }
        for (EfectoEstado efecto : efectosActivos) {
            if (efecto.impideActuar()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Al cerrar la etapa se limpian las mejoras, los escudos y el aturdimiento.
     * El veneno se queda, porque es el unico que responde true a
     * persisteFueraDeCombate().
     */
    public void limpiarEfectosTemporales() {
        efectosActivos.removeIf(efecto -> !efecto.persisteFueraDeCombate());
    }

    /**
     * Saca un efecto por nombre y avisa si habia alguno. Lo usa el Antidoto,
     * que quita el Veneno.
     */
    public boolean quitarEfecto(String nombre) {
        boolean habia = false;
        Iterator<EfectoEstado> iterador = efectosActivos.iterator();
        while (iterador.hasNext()) {
            EfectoEstado efecto = iterador.next();
            if (efecto.getNombre().equals(nombre)) {
                efecto.alExpirar(this);
                iterador.remove();
                habia = true;
            }
        }
        return habia;
    }

    public List<EfectoEstado> getEfectosActivos() {
        return List.copyOf(efectosActivos);
    }

    // ------------------------------------------------------ inicio de combate

    /**
     * Preparacion antes del primer turno de la etapa. El mana se llena entero
     * (asi cada pelea arranca con todos los recursos) pero la vida NO se toca:
     * si vengo golpeado de la etapa anterior, entro golpeado, y para eso estan
     * las pociones y el curador. Las definitivas tambien arrancan disponibles.
     */
    public void alIniciarCombate() {
        manaActual = manaMaximo;
        reiniciarCooldowns();
    }

    // ----------------------------------------------------- estadisticas

    /**
     * Las estadisticas "efectivas" son las que valen en el combate: la base mas
     * lo que sumen las mejoras activas. Los
     * movimientos y CalculadorDeDaño usan siempre estas, nunca las base.
     */
    public int getAtaqueFisicoEfectivo() {
        return conModificadores(ataqueFisico, Estadistica.ATAQUE_FISICO);
    }

    public int getDefensaFisicaEfectiva() {
        return conModificadores(defensaFisica, Estadistica.DEFENSA_FISICA);
    }

    public int getAtaqueMagicoEfectivo() {
        return conModificadores(ataqueMagico, Estadistica.ATAQUE_MAGICO);
    }

    public int getDefensaMagicaEfectiva() {
        return conModificadores(defensaMagica, Estadistica.DEFENSA_MAGICA);
    }

    public int getVelocidadEfectiva() {
        return conModificadores(velocidad, Estadistica.VELOCIDAD);
    }

    public int getSuerteEfectiva() {
        return conModificadores(suerte, Estadistica.SUERTE);
    }

    /**
     * Suma los porcentajes de todos los efectos activos y los aplica sobre el
     * valor base. Se suman entre ellos y se aplican una sola vez, no uno sobre
     * el resultado del otro: dos mejoras de +20% dan +40%, no +44%. Nunca baja
     * de 1, por las dudas.
     */
    private int conModificadores(int valorBase, Estadistica estadistica) {
        int porcentaje = 0;
        for (EfectoEstado efecto : efectosActivos) {
            porcentaje += efecto.getModificadorPorcentual(estadistica);
        }
        int valor = valorBase * (PORCENTAJE_TOTAL + porcentaje) / PORCENTAJE_TOTAL;
        return Math.max(valor, 1);
    }

    /**
     * Suma (o resta, con cantidad negativa) una cantidad fija a una estadistica
     * base. Lo usan los Equipable al equiparse y al sacarse. Esta aca y es
     * publico porque los items viven en otro paquete y no pueden tocar los
     * atributos protegidos directamente.
     */
    public void sumarAEstadistica(Estadistica estadistica, int cantidad) {
        switch (estadistica) {
            case VIDA -> subirVidaMaxima(cantidad);
            case MANA -> subirManaMaximo(cantidad);
            case ATAQUE_FISICO -> ataqueFisico += cantidad;
            case DEFENSA_FISICA -> defensaFisica += cantidad;
            case ATAQUE_MAGICO -> ataqueMagico += cantidad;
            case DEFENSA_MAGICA -> defensaMagica += cantidad;
            case VELOCIDAD -> velocidad += cantidad;
            case SUERTE -> suerte += cantidad;
        }
    }

    /**
     * Sube el techo de vida y la vida actual en la misma cantidad, asi subir de
     * nivel o ponerse una armadura cura de hecho en vez de dejar un hueco.
     */
    protected void subirVidaMaxima(int cantidad) {
        vidaMaxima += cantidad;
        vidaActual += cantidad;
        if (vidaActual > vidaMaxima) {
            vidaActual = vidaMaxima;
        }
    }

    protected void subirManaMaximo(int cantidad) {
        manaMaximo += cantidad;
        manaActual += cantidad;
        if (manaActual > manaMaximo) {
            manaActual = manaMaximo;
        }
    }

    // ------------------------------------------------------------ movimientos

    /** Se llama al arrancar el turno propio (ver iniciarTurno): acerca un turno las definitivas. */
    public void reducirCooldowns() {
        for (Movimiento movimiento : movimientos) {
            movimiento.reducirCooldown();
        }
    }

    /** Al empezar una etapa las definitivas vuelven a estar disponibles. */
    public void reiniciarCooldowns() {
        for (Movimiento movimiento : movimientos) {
            movimiento.reiniciarCooldown();
        }
    }

    /**
     * final a proposito: las seis clases jugables lo llaman desde su
     * constructor, y si fuera redefinible el compilador avisaria que se esta
     * usando un metodo que una subclase podria cambiar antes de terminar de
     * construirse (warning this-escape).
     */
    protected final void agregarMovimiento(Movimiento movimiento) {
        movimientos.add(movimiento);
    }

    public List<Movimiento> getMovimientos() {
        return List.copyOf(movimientos);
    }

    // -------------------------------------------------------------- consultas

    public String getNombre() {
        return nombre;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int getManaActual() {
        return manaActual;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    public int getResistenciaAEstados() {
        return resistenciaAEstados;
    }
}
