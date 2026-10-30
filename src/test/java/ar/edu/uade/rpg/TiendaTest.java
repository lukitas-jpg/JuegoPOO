package ar.edu.uade.rpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ar.edu.uade.rpg.modelo.combatientes.ClaseDePersonaje;
import ar.edu.uade.rpg.modelo.combatientes.Combatiente;
import ar.edu.uade.rpg.modelo.combatientes.Guerrero;
import ar.edu.uade.rpg.modelo.combatientes.Mago;
import ar.edu.uade.rpg.modelo.combatientes.TipoDaño;
import ar.edu.uade.rpg.modelo.efectos.Veneno;
import ar.edu.uade.rpg.modelo.items.CatalogoDeConsumibles;
import ar.edu.uade.rpg.modelo.items.CatalogoDeEquipables;
import ar.edu.uade.rpg.modelo.items.Consumible;
import ar.edu.uade.rpg.modelo.items.Equipable;
import ar.edu.uade.rpg.modelo.items.Inventario;
import ar.edu.uade.rpg.modelo.items.Rareza;
import ar.edu.uade.rpg.modelo.movimientos.AccionUsarConsumible;
import ar.edu.uade.rpg.modelo.tienda.GeneradorDeStock;
import ar.edu.uade.rpg.modelo.tienda.MaquinaDeSlots;
import ar.edu.uade.rpg.modelo.tienda.ResultadoTirada;
import ar.edu.uade.rpg.modelo.tienda.Simbolo;
import ar.edu.uade.rpg.modelo.tienda.Tienda;

class TiendaTest {
    private static final List<ClaseDePersonaje> PARTY =
            List.of(ClaseDePersonaje.GUERRERO, ClaseDePersonaje.MAGO, ClaseDePersonaje.CURADOR);

    @Test
    void hay36EquipablesSeisPorClase() {
        List<Equipable> todos = CatalogoDeEquipables.catalogoCompleto();
        assertEquals(36, todos.size());

        for (ClaseDePersonaje clase : ClaseDePersonaje.values()) {
            int deEsaClase = (int) todos.stream()
                    .filter(equipable -> equipable.getClaseRequerida() == clase).count();
            assertEquals(6, deEsaClase, clase.name());
        }
    }

    @Test
    void ningunEquipableEsDeRarezaComun() {
        for (Equipable equipable : CatalogoDeEquipables.catalogoCompleto()) {
            assertFalse(equipable.getRareza() == Rareza.COMUN, equipable.getNombre());
        }
    }

    @Test
    void losCincoConsumiblesSonComunes() {
        List<Consumible> catalogo = CatalogoDeConsumibles.catalogoCompleto();
        assertEquals(5, catalogo.size());
        for (Consumible consumible : catalogo) {
            assertEquals(Rareza.COMUN, consumible.getRareza(), consumible.getNombre());
        }
    }

    @Test
    void elCatalogoDevuelveInstanciasNuevasCadaVez() {
        assertFalse(CatalogoDeConsumibles.pocionDeVida() == CatalogoDeConsumibles.pocionDeVida(),
                "dos pociones distintas no pueden ser el mismo objeto");
    }

    @Test
    void laPocionCuraUnPorcentajeDeLaVidaMaxima() {
        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.recibirDaño(100, TipoDaño.FISICO);

        CatalogoDeConsumibles.pocionDeVida().usarSobre(guerrero);

        assertEquals(86, guerrero.getVidaActual());
    }

    @Test
    void laPocionNoSirveSobreUnCaidoYElPolvoSoloSobreUnCaido() {
        Guerrero vivo = new Guerrero("Lucas");
        Guerrero caido = new Guerrero("Otro");
        caido.recibirDaño(500, TipoDaño.FISICO);

        assertTrue(CatalogoDeConsumibles.pocionDeVida().puedeUsarseSobre(vivo));
        assertFalse(CatalogoDeConsumibles.pocionDeVida().puedeUsarseSobre(caido));

        assertFalse(CatalogoDeConsumibles.polvoDeRenacer().puedeUsarseSobre(vivo));
        assertTrue(CatalogoDeConsumibles.polvoDeRenacer().puedeUsarseSobre(caido));
    }

    @Test
    void elPolvoDeRenacerLevantaAlCaidoConLaMitadDeSuVida() {
        Guerrero caido = new Guerrero("Lucas");
        caido.recibirDaño(500, TipoDaño.FISICO);

        CatalogoDeConsumibles.polvoDeRenacer().usarSobre(caido);

        assertTrue(caido.estaVivo());
        assertEquals(60, caido.getVidaActual());
    }

    @Test
    void elAntidotoQuitaElVenenoYNoSeDesperdiciaSiNoHayNada() {
        Guerrero guerrero = new Guerrero("Lucas");
        Consumible antidoto = CatalogoDeConsumibles.antidoto();

        assertFalse(antidoto.puedeUsarseSobre(guerrero), "sin veneno no hay nada que curar");

        guerrero.aplicarEfecto(new Veneno(6, 3));
        assertTrue(antidoto.puedeUsarseSobre(guerrero));

        antidoto.usarSobre(guerrero);
        assertTrue(guerrero.getEfectosActivos().isEmpty());
    }

    @Test
    void laPiedraDeAfilarSubeElAtaqueMasAltoDeCadaUno() {
        Guerrero guerrero = new Guerrero("Lucas");
        Mago mago = new Mago("Giovanni");

        CatalogoDeConsumibles.piedraDeAfilar().usarSobre(guerrero);
        CatalogoDeConsumibles.piedraDeAfilar().usarSobre(mago);

        assertEquals(23, guerrero.getAtaqueFisicoEfectivo());
        assertEquals(4, guerrero.getAtaqueMagicoEfectivo());
        assertEquals(26, mago.getAtaqueMagicoEfectivo());
        assertEquals(4, mago.getAtaqueFisicoEfectivo());
    }

    @Test
    void elInventarioRespetaElTopeDeCadaObjeto() {
        Inventario inventario = new Inventario();
        Consumible referencia = CatalogoDeConsumibles.polvoDeRenacer();

        assertTrue(inventario.agregar(CatalogoDeConsumibles.polvoDeRenacer()));
        assertTrue(inventario.agregar(CatalogoDeConsumibles.polvoDeRenacer()));
        assertTrue(inventario.agregar(CatalogoDeConsumibles.polvoDeRenacer()));
        assertFalse(inventario.agregar(CatalogoDeConsumibles.polvoDeRenacer()), "el cuarto no entra");

        assertEquals(3, inventario.contar(referencia.getNombre()));
        assertTrue(inventario.estaLleno(referencia));
    }

    @Test
    void elTopeEsPorObjetoYNoParaTodoElInventario() {
        Inventario inventario = new Inventario();
        for (int i = 0; i < 3; i++) {
            inventario.agregar(CatalogoDeConsumibles.polvoDeRenacer());
        }
        assertTrue(inventario.agregar(CatalogoDeConsumibles.pocionDeVida()),
                "el tope del polvo no deberia frenar a la pocion");
        assertEquals(2, inventario.getResumen().size());
    }

    @Test
    void usarUnConsumibleLoSacaDelInventario() {
        Inventario inventario = new Inventario();
        Consumible pocion = CatalogoDeConsumibles.pocionDeVida();
        inventario.agregar(pocion);
        inventario.agregar(CatalogoDeConsumibles.pocionDeVida());

        Guerrero guerrero = new Guerrero("Lucas");
        guerrero.recibirDaño(100, TipoDaño.FISICO);
        new AccionUsarConsumible(guerrero, pocion, guerrero, inventario).ejecutar();

        assertEquals(1, inventario.contar("Pocion de Vida"));
        assertEquals(86, guerrero.getVidaActual());
    }

    @Test
    void usarUnConsumibleSobreElObjetivoEquivocadoNoLoGasta() {
        Inventario inventario = new Inventario();
        Consumible polvo = CatalogoDeConsumibles.polvoDeRenacer();
        inventario.agregar(polvo);
        Guerrero vivo = new Guerrero("Lucas");

        new AccionUsarConsumible(vivo, polvo, vivo, inventario).ejecutar();

        assertEquals(1, inventario.contar("Polvo de Renacer"), "no se deberia haber gastado");
    }

    @Test
    void laTiendaSoloOfreceObjetosDeLasClasesDeLaParty() {
        GeneradorDeStock generador = new GeneradorDeStock(new Random(5));
        for (int numero = 1; numero <= 4; numero++) {
            Tienda tienda = new Tienda(numero, PARTY, generador);
            for (Equipable equipable : tienda.getSlots()) {
                assertNotNull(equipable);
                assertTrue(PARTY.contains(equipable.getClaseRequerida()),
                        equipable.getNombre() + " no lo puede usar nadie de la party");
            }
        }
    }

    @Test
    void laPrimeraTiendaSoloTieneObjetosRaros() {
        GeneradorDeStock generador = new GeneradorDeStock(new Random(11));
        for (int intento = 0; intento < 50; intento++) {
            for (Equipable equipable : new Tienda(1, PARTY, generador).getSlots()) {
                assertEquals(Rareza.RARO, equipable.getRareza());
            }
        }
    }

    @Test
    void unaVisitaNoOfreceDosVecesElMismoObjeto() {
        List<ClaseDePersonaje> tres = List.of(ClaseDePersonaje.GUERRERO, ClaseDePersonaje.MAGO,
                ClaseDePersonaje.CURADOR);
        for (int semilla = 0; semilla < 200; semilla++) {
            GeneradorDeStock generador = new GeneradorDeStock(new Random(semilla));
            List<String> nombres = new ArrayList<>();
            for (Equipable equipable : generador.sortearStock(1, tres, Tienda.CANTIDAD_DE_SLOTS)) {
                assertFalse(nombres.contains(equipable.getNombre()), "repitio " + equipable.getNombre());
                nombres.add(equipable.getNombre());
            }
        }
    }

    @Test
    void loLegendarioAparecePorPrimeraVezEnLaTiendaTres() {
        GeneradorDeStock generador = new GeneradorDeStock(new Random(3));
        assertEquals(0, GeneradorDeStock.getPesos(1)[2]);
        assertEquals(0, GeneradorDeStock.getPesos(2)[2]);
        assertTrue(GeneradorDeStock.getPesos(3)[2] > 0);

        boolean salioAlguno = false;
        for (int intento = 0; intento < 200 && !salioAlguno; intento++) {
            salioAlguno = generador.sortearRareza(3) == Rareza.LEGENDARIO;
        }
        assertTrue(salioAlguno, "en la tienda 3 deberia salir legendario alguna vez");
    }

    @Test
    void laUltimaTiendaYaNoOfreceObjetosRaros() {
        assertEquals(0, GeneradorDeStock.getPesos(4)[0]);
        GeneradorDeStock generador = new GeneradorDeStock(new Random(9));
        for (int intento = 0; intento < 200; intento++) {
            assertFalse(generador.sortearRareza(4) == Rareza.RARO);
        }
    }

    @Test
    void comprarUnSlotLoDejaVacioYNoLoRefresca() {
        Tienda tienda = new Tienda(2, PARTY, new GeneradorDeStock(new Random(1)));

        Equipable comprado = tienda.comprarSlot(0);

        assertNotNull(comprado);
        assertNull(tienda.getSlot(0), "el slot deberia quedar vacio hasta el proximo reroll");
        assertNull(tienda.comprarSlot(0), "no se puede comprar dos veces el mismo slot");
    }

    @Test
    void elRerollVuelveALlenarLosTresSlots() {
        Tienda tienda = new Tienda(2, PARTY, new GeneradorDeStock(new Random(1)));
        tienda.comprarSlot(0);
        tienda.comprarSlot(1);

        tienda.rerollear();

        for (Equipable equipable : tienda.getSlots()) {
            assertNotNull(equipable);
        }
    }

    @Test
    void elRerollSeEncareceDentroDeLaMismaVisita() {
        Tienda tienda = new Tienda(1, PARTY, new GeneradorDeStock(new Random(1)));

        assertEquals(20, tienda.getCostoDelProximoReroll());
        tienda.rerollear();
        assertEquals(40, tienda.getCostoDelProximoReroll());
        tienda.rerollear();
        assertEquals(60, tienda.getCostoDelProximoReroll());
    }

    @Test
    void conElOroDeLaPrimeraTiendaNoSePuedeRerollearDiezVeces() {
        Tienda tienda = new Tienda(1, PARTY, new GeneradorDeStock(new Random(1)));
        int oro = 135;

        int rerolls = 0;
        while (tienda.puedeRerollear(oro)) {
            oro -= tienda.getCostoDelProximoReroll();
            tienda.rerollear();
            rerolls++;
        }
        assertTrue(rerolls < 10, "con 135 de oro no deberia llegar a 10 rerolls, hizo " + rerolls);
    }

    @Test
    void elRerollTieneUnTopePorVisita() {
        Tienda tienda = new Tienda(4, PARTY, new GeneradorDeStock(new Random(1)));
        for (int i = 0; i < Tienda.MAXIMO_DE_REROLLS; i++) {
            tienda.rerollear();
        }
        assertFalse(tienda.puedeRerollear(100000));
    }

    @Test
    void laTiendaSiempreTieneLosCincoConsumibles() {
        for (int numero = 1; numero <= 4; numero++) {
            Tienda tienda = new Tienda(numero, PARTY, new GeneradorDeStock(new Random(numero)));
            assertEquals(5, tienda.getConsumiblesALaVenta().size());
        }
    }

    @Test
    void elRetornoDeLaMaquinaEs205De216() {
        int suma = 0;
        int tiradasQuePagan = 0;
        for (Simbolo a : Simbolo.values()) {
            for (Simbolo b : Simbolo.values()) {
                for (Simbolo c : Simbolo.values()) {
                    int multiplicador = MaquinaDeSlots.calcularMultiplicador(List.of(a, b, c));
                    suma += multiplicador;
                    if (multiplicador > 0) {
                        tiradasQuePagan++;
                    }
                }
            }
        }
        assertEquals(205, suma, "el retorno deberia ser 205/216 = 94.9%");
        assertEquals(80, tiradasQuePagan, "deberia pagar 80 de las 216 veces = 37%");
    }

    @Test
    void laTablaDePremiosDaLosNumerosQueDiceElDocumento() {
        assertEquals(15, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.DIAMANTE, Simbolo.DIAMANTE, Simbolo.DIAMANTE)));
        assertEquals(10, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.CEREZA, Simbolo.CEREZA, Simbolo.CEREZA)));
        assertEquals(0, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.LIMON, Simbolo.LIMON, Simbolo.LIMON)));
        assertEquals(4, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.DIAMANTE, Simbolo.DIAMANTE, Simbolo.MONEDA)));
        assertEquals(2, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.MONEDA, Simbolo.CEREZA, Simbolo.MONEDA)));
        assertEquals(1, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.TREBOL, Simbolo.TREBOL, Simbolo.DIAMANTE)));
        assertEquals(0, MaquinaDeSlots.calcularMultiplicador(
                List.of(Simbolo.MONEDA, Simbolo.CEREZA, Simbolo.DIAMANTE)));
    }

    @Test
    void laPrimeraTiradaDeCadaMaquinaEsGratis() {
        MaquinaDeSlots maquina = new MaquinaDeSlots(new Random(4));

        assertEquals(0, maquina.getCostoDeLaProximaTirada());
        assertTrue(maquina.puedeTirar(0), "con la gratis se puede tirar sin oro");

        ResultadoTirada primera = maquina.tirar();
        assertTrue(primera.fueGratis());

        assertEquals(MaquinaDeSlots.APUESTA, maquina.getCostoDeLaProximaTirada());
        assertFalse(maquina.puedeTirar(0));
    }

    @Test
    void laMaquinaAbandonadaNoSePuedeVolverAUsar() {
        MaquinaDeSlots maquina = new MaquinaDeSlots(new Random(4));
        maquina.abandonar();

        assertTrue(maquina.estaAbandonada());
        assertFalse(maquina.puedeTirar(100000));
    }

    @Test
    void cadaTiradaDevuelveTresSimbolos() {
        MaquinaDeSlots maquina = new MaquinaDeSlots(new Random(4));
        ResultadoTirada tirada = maquina.tirar();

        assertEquals(3, tirada.getSimbolos().size());
        assertEquals(tirada.getMultiplicador() * MaquinaDeSlots.APUESTA, tirada.getOroGanado());
    }

    @Test
    void aLaLargaLaMaquinaDevuelveMenosDeLoQueRecibe() {
        MaquinaDeSlots maquina = new MaquinaDeSlots(new Random(2024));
        int apostado = 0;
        int ganado = 0;
        for (int tirada = 0; tirada < 20000; tirada++) {
            apostado += MaquinaDeSlots.APUESTA;
            ganado += maquina.tirar().getOroGanado();
        }
        double retorno = ganado * 100.0 / apostado;
        assertTrue(retorno > 90 && retorno < 100,
                "el retorno deberia rondar el 95%, dio " + retorno);
    }
}
