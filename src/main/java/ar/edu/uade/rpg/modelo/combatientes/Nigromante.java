package ar.edu.uade.rpg.modelo.combatientes;

import java.util.ArrayList;
import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoAleatorio;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AccionInvocar;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueMagico;

/**
 * Poca vida pero llena el campo de esqueletos. Entra con cinco antes de la
 * primera ronda y vuelve a completar el tope cada 3 rondas.
 *
 * El punto interesante del diseño esta en COMO entran esos esqueletos. El
 * Nigromante no conoce al Combate: los devuelve, en alIniciarCombate() y dentro
 * de una AccionInvocar, y es Combate quien decide incorporarlos. Si fuera al
 * reves, cada enemigo necesitaria una referencia al combate que lo esta
 * jugando y tendriamos una dependencia circular entre los dos paquetes.
 *
 * Matarlo es la jugada correcta: cuando cae, sus esqueletos se desarman solos.
 */
public final class Nigromante extends Enemigo implements ObjetivoAleatorio {

    private final int rondasEntreInvocaciones = 3;
    private final int maximoEsqueletosVivos = 5;
    private int rondasDesdeUltimaInvocacion;
    private final List<Esqueleto> invocados = new ArrayList<>();

    public Nigromante() {
        super("Nigromante");
        vidaMaxima = 45; vidaActual = 45;
        ataqueFisico = 4; defensaFisica = 5;
        ataqueMagico = 14; defensaMagica = 10;
        velocidad = 8; suerte = 6;
        rondasDesdeUltimaInvocacion = 0;
        agregarMovimiento(new AtaqueMagico("Toque Marchito", "Un roce que seca lo que toca.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 9));
    }

    /** Entra al combate con el tope de esqueletos ya puesto. */
    @Override
    public List<Combatiente> alIniciarCombate() {
        super.alIniciarCombate();
        rondasDesdeUltimaInvocacion = 0;
        return new ArrayList<>(invocarHastaElTope());
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        rondasDesdeUltimaInvocacion++;
        if (tocaInvocar()) {
            rondasDesdeUltimaInvocacion = 0;
            List<Esqueleto> nuevos = invocarHastaElTope();
            if (!nuevos.isEmpty()) {
                return new AccionInvocar(this, new ArrayList<>(nuevos));
            }
        }
        return usar(getMovimientos().get(0),
                List.of(elegirObjetivo(filtrarVivos(party))), calculador);
    }

    private boolean tocaInvocar() {
        return rondasDesdeUltimaInvocacion >= rondasEntreInvocaciones
                && contarEsqueletosVivos() < maximoEsqueletosVivos;
    }

    /** Crea los que falten para llegar al tope y los devuelve. */
    private List<Esqueleto> invocarHastaElTope() {
        List<Esqueleto> nuevos = new ArrayList<>();
        int faltan = maximoEsqueletosVivos - contarEsqueletosVivos();
        for (int i = 0; i < faltan; i++) {
            Esqueleto esqueleto = new Esqueleto(this, random);
            invocados.add(esqueleto);
            nuevos.add(esqueleto);
        }
        return nuevos;
    }

    private int contarEsqueletosVivos() {
        int vivos = 0;
        for (Esqueleto esqueleto : invocados) {
            if (esqueleto.estaVivo()) {
                vivos++;
            }
        }
        return vivos;
    }

    public int getMaximoEsqueletosVivos() {
        return maximoEsqueletosVivos;
    }
}
