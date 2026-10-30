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

    public static int getNumeroDeTienda(int numeroDeEtapa) {
        for (int i = 0; i < ETAPAS_DE_TIENDA.length; i++) {
            if (ETAPAS_DE_TIENDA[i] == numeroDeEtapa) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("La etapa " + numeroDeEtapa + " no es de tienda");
    }

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
