package ar.edu.uade.rpg.modelo.combatientes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.edu.uade.rpg.modelo.efectos.EfectoEstado;
import ar.edu.uade.rpg.modelo.movimientos.Estadistica;
import ar.edu.uade.rpg.modelo.movimientos.Movimiento;

public abstract class Combatiente implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int PORCENTAJE_TOTAL = 100;

    private static final int REDUCCION_AL_DEFENDER = 50;

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

    protected int resistenciaAEstados;

    protected boolean defendiendo;

    protected final List<Movimiento> movimientos = new ArrayList<>();
    protected final List<EfectoEstado> efectosActivos = new ArrayList<>();

    protected Combatiente(String nombre) {
        this.nombre = nombre;
        this.resistenciaAEstados = 0;
        this.defendiendo = false;
    }

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

    public int recibirDañoDirecto(int daño) {
        int dañoAplicado = Math.min(daño, vidaActual);
        vidaActual -= dañoAplicado;
        return dañoAplicado;
    }

    public int curar(int cantidad) {
        int curacion = Math.min(cantidad, vidaMaxima - vidaActual);
        vidaActual += curacion;
        return curacion;
    }

    public void revivir(int porcentajeVida) {
        if (estaVivo()) {
            return;
        }
        vidaActual = Math.max(1, vidaMaxima * porcentajeVida / PORCENTAJE_TOTAL);
    }

    public boolean estaVivo() {
        return vidaActual > 0;
    }

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

    public void defender() {
        defendiendo = true;
        recuperarMana(manaMaximo * MANA_RECUPERADO_AL_DEFENDER / PORCENTAJE_TOTAL);
    }

    public boolean estaDefendiendo() {
        return defendiendo;
    }

    public int getPorcentajeDeDañoRecibido() {
        return defendiendo ? REDUCCION_AL_DEFENDER : PORCENTAJE_TOTAL;
    }

    public EfectoEstado aplicarEfecto(EfectoEstado efecto) {
        EfectoEstado copia = efecto.copiar();
        efectosActivos.add(copia);
        copia.alAplicarse(this);
        return copia;
    }

    public void iniciarTurno() {
        defendiendo = false;
        reducirCooldowns();
    }

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

    public void limpiarEfectosTemporales() {
        efectosActivos.removeIf(efecto -> !efecto.persisteFueraDeCombate());
    }

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

    public List<Combatiente> alIniciarCombate() {
        manaActual = manaMaximo;
        reiniciarCooldowns();
        return List.of();
    }

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

    private int conModificadores(int valorBase, Estadistica estadistica) {
        int porcentaje = 0;
        for (EfectoEstado efecto : efectosActivos) {
            porcentaje += efecto.getModificadorPorcentual(estadistica);
        }
        int valor = valorBase * (PORCENTAJE_TOTAL + porcentaje) / PORCENTAJE_TOTAL;
        return Math.max(valor, 1);
    }

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

    public void reducirCooldowns() {
        for (Movimiento movimiento : movimientos) {
            movimiento.reducirCooldown();
        }
    }

    public void reiniciarCooldowns() {
        for (Movimiento movimiento : movimientos) {
            movimiento.reiniciarCooldown();
        }
    }

    protected final void agregarMovimiento(Movimiento movimiento) {
        movimientos.add(movimiento);
    }

    public List<Movimiento> getMovimientos() {
        return List.copyOf(movimientos);
    }

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
