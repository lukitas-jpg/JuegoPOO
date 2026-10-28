package ar.edu.uade.rpg.modelo.partida;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import ar.edu.uade.rpg.modelo.combatientes.Acechador;
import ar.edu.uade.rpg.modelo.combatientes.Acolito;
import ar.edu.uade.rpg.modelo.combatientes.Berserker;
import ar.edu.uade.rpg.modelo.combatientes.Bruto;
import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Emponzoñador;
import ar.edu.uade.rpg.modelo.combatientes.Esqueleto;
import ar.edu.uade.rpg.modelo.combatientes.Enemigo;
import ar.edu.uade.rpg.modelo.combatientes.Guardian;
import ar.edu.uade.rpg.modelo.combatientes.Hechicero;
import ar.edu.uade.rpg.modelo.combatientes.Nigromante;
import ar.edu.uade.rpg.modelo.combatientes.SeñorDeLasSombras;
import ar.edu.uade.rpg.modelo.combatientes.Verdugo;
import ar.edu.uade.rpg.modelo.tienda.GeneradorDeStock;
import ar.edu.uade.rpg.modelo.tienda.MaquinaDeSlots;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

/**
 * Arma la etapa numero N. Es una Pure Fabrication: concentra todo el azar de la
 * progresion en un solo lugar.
 *
 * Las etapas 4, 9, 14 y 19 son de tienda; la 10 es el minijefe y la 20 el jefe
 * final. La maquina de slots solo esta en la 9 y en la 19.
 *
 * Los enemigos se eligen al azar de un pool que se va abriendo, y sus
 * estadisticas se escalan segun lo lejos que este la etapa. El Esqueleto deja de
 * aparecer suelto desde la 11 porque a esa altura es un estorbo sin amenaza:
 * desde ahi solo lo trae el Nigromante.
 */
public class GeneradorDeEtapas {

    private static final int[] ETAPAS_DE_TIENDA = {4, 9, 14, 19};
    private static final int[] ETAPAS_CON_MAQUINA = {9, 19};
    private static final int ETAPA_DEL_MINIJEFE = 10;
    private static final int ETAPA_DEL_JEFE_FINAL = 20;

    private final Random random;
    private final GeneradorDeStock generadorDeStock;

    public GeneradorDeEtapas() {
        this(new Random());
    }

    public GeneradorDeEtapas(Random random) {
        this.random = random;
        this.generadorDeStock = new GeneradorDeStock(random);
    }

    public Etapa crearEtapa(int numero, List<ClaseDePersonaje> clasesDeLaParty) {
        return esEtapaDeTienda(numero)
                ? crearEtapaDeTienda(numero, clasesDeLaParty)
                : crearEtapaDeCombate(numero);
    }

    // ------------------------------------------------------------ que etapa

    public static boolean esEtapaDeTienda(int numero) {
        for (int etapa : ETAPAS_DE_TIENDA) {
            if (etapa == numero) {
                return true;
            }
        }
        return false;
    }

    public static boolean llevaMaquinaDeSlots(int numero) {
        for (int etapa : ETAPAS_CON_MAQUINA) {
            if (etapa == numero) {
                return true;
            }
        }
        return false;
    }

    public static TipoEtapa determinarTipo(int numero) {
        if (numero == ETAPA_DEL_JEFE_FINAL) {
            return TipoEtapa.JEFE_FINAL;
        }
        if (numero == ETAPA_DEL_MINIJEFE) {
            return TipoEtapa.MINIJEFE;
        }
        return TipoEtapa.NORMAL;
    }

    /** Que numero de tienda es: la de la etapa 4 es la 1, la de la 19 es la 4. */
    public static int getNumeroDeTienda(int numeroDeEtapa) {
        for (int i = 0; i < ETAPAS_DE_TIENDA.length; i++) {
            if (ETAPAS_DE_TIENDA[i] == numeroDeEtapa) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("La etapa " + numeroDeEtapa + " no es de tienda");
    }

    // ------------------------------------------------------------ construir

    private EtapaDeTienda crearEtapaDeTienda(int numero, List<ClaseDePersonaje> clases) {
        Tienda tienda = new Tienda(getNumeroDeTienda(numero), clases, generadorDeStock);
        MaquinaDeSlots maquina = llevaMaquinaDeSlots(numero) ? new MaquinaDeSlots(random) : null;
        return new EtapaDeTienda(numero, tienda, maquina);
    }

    private EtapaDeCombate crearEtapaDeCombate(int numero) {
        TipoEtapa tipo = determinarTipo(numero);
        List<Enemigo> enemigos = elegirEnemigos(numero, tipo);
        for (Enemigo enemigo : enemigos) {
            escalarEstadisticas(enemigo, numero);
        }
        return new EtapaDeCombate(numero, tipo, enemigos);
    }

    private List<Enemigo> elegirEnemigos(int numero, TipoEtapa tipo) {
        List<Enemigo> enemigos = new ArrayList<>();
        if (tipo == TipoEtapa.MINIJEFE) {
            enemigos.add(new Verdugo());
            return enemigos;
        }
        if (tipo == TipoEtapa.JEFE_FINAL) {
            enemigos.add(new SeñorDeLasSombras());
            return enemigos;
        }
        List<Supplier<Enemigo>> pool = getPool(numero);
        int cantidad = getCantidadDeEnemigos(numero);
        for (int i = 0; i < cantidad; i++) {
            enemigos.add(pool.get(random.nextInt(pool.size())).get());
        }
        return enemigos;
    }

    /**
     * El pool se va abriendo: cada tramo suma enemigos nuevos.
     *
     * El Esqueleto solo aparece suelto en las tres primeras etapas. Desde la 11
     * es un estorbo sin amenaza y deja de salir solo; a partir de ahi lo trae
     * unicamente el Nigromante.
     *
     * El Nigromante entra recien en la 15 porque llega con cinco esqueletos y
     * multiplica los enemigos en pantalla: antes de que la party tenga sus
     * definitivas, es injusto.
     */
    private List<Supplier<Enemigo>> getPool(int numero) {
        List<Supplier<Enemigo>> pool = new ArrayList<>();
        if (numero <= 3) {
            pool.add(() -> new Esqueleto(null, random));
            pool.add(Bruto::new);
            pool.add(Acechador::new);
            return pool;
        }
        pool.add(Bruto::new);
        pool.add(Acechador::new);
        pool.add(Guardian::new);
        pool.add(Hechicero::new);
        pool.add(Emponzoñador::new);
        if (numero <= 8) {
            pool.add(() -> new Esqueleto(null, random));
            return pool;
        }
        pool.add(Acolito::new);
        pool.add(Berserker::new);
        if (numero >= 15) {
            pool.add(Nigromante::new);
        }
        return pool;
    }

    private int getCantidadDeEnemigos(int numero) {
        if (numero <= 3) {
            return 2;
        }
        if (numero <= 8) {
            return 2 + random.nextInt(2);
        }
        return 3 + random.nextInt(2);
    }

    private void escalarEstadisticas(Enemigo enemigo, int numero) {
        enemigo.escalarParaEtapa(numero);
    }
}
