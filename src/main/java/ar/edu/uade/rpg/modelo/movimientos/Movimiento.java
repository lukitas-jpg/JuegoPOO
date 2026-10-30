package ar.edu.uade.rpg.modelo.movimientos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Personaje;

public abstract class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public abstract ResultadoAccion aplicar(Combatiente usuario, List<Combatiente> objetivos,
                                            CalculadorDeDaño calculador);

    public boolean estaDesbloqueado(Personaje personaje) {
        return personaje.getNivel() >= nivelDesbloqueo;
    }

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

    public void reiniciarCooldown() {
        cooldownRestante = 0;
    }

    public List<Combatiente> getObjetivosPosibles(List<Combatiente> aliados,
                                                  List<Combatiente> enemigos) {
        return switch (alcance) {
            case OBJETIVO_UNICO, TODOS_LOS_ENEMIGOS -> new ArrayList<>(enemigos);
            case ALIADO, TODA_LA_PARTY -> new ArrayList<>(aliados);
            case UNO_MISMO -> new ArrayList<>();
        };
    }

    public boolean requiereElegirObjetivo() {
        return alcance == AlcanceMovimiento.OBJETIVO_UNICO || alcance == AlcanceMovimiento.ALIADO;
    }

    protected List<Combatiente> resolverObjetivos(Combatiente usuario,
                                                   List<Combatiente> objetivos) {
        return alcance == AlcanceMovimiento.UNO_MISMO ? List.of(usuario) : objetivos;
    }

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
