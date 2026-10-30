package ar.edu.uade.rpg.modelo.combatientes;

import java.util.List;

import ar.edu.uade.rpg.modelo.combate.CalculadorDeDaño;
import ar.edu.uade.rpg.modelo.combatientes.objetivos.ObjetivoAleatorio;
import ar.edu.uade.rpg.modelo.efectos.Proteccion;
import ar.edu.uade.rpg.modelo.movimientos.Accion;
import ar.edu.uade.rpg.modelo.movimientos.AlcanceMovimiento;
import ar.edu.uade.rpg.modelo.movimientos.AtaqueFisico;

public final class Guardian extends Enemigo implements ObjetivoAleatorio {
    private static final int PROBABILIDAD_DE_CUBRIR = 35;

    private final int reduccionOtorgada = 30;

    public Guardian() {
        super("Guardian");
        vidaMaxima = 95; vidaActual = 95;
        ataqueFisico = 10; defensaFisica = 22; defensaMagica = 15;
        velocidad = 5; suerte = 4;
        agregarMovimiento(new AtaqueFisico("Embestida", "Se tira encima con todo el escudo.",
                0, AlcanceMovimiento.OBJETIVO_UNICO, 1, false, 8));
    }

    @Override
    public Accion elegirAccion(List<Combatiente> party, List<Enemigo> aliados,
                               CalculadorDeDaño calculador) {
        if (random.nextInt(100) < PROBABILIDAD_DE_CUBRIR) {
            protegerAliados(aliados);
            return null;
        }
        return usar(getMovimientos().get(0),
                List.of(elegirObjetivo(filtrarVivos(party))), calculador);
    }

    private void protegerAliados(List<Enemigo> aliados) {
        Proteccion escudo = new Proteccion(reduccionOtorgada, 1);
        for (Enemigo aliado : aliados) {
            if (aliado != this && aliado.estaVivo()) {
                aliado.aplicarEfecto(escudo);
            }
        }
    }

    public int getReduccionOtorgada() {
        return reduccionOtorgada;
    }
}
