package ar.edu.uade.rpg.vista;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import ar.edu.uade.rpg.controlador.ControladorJuego;

/** Pantalla inicial (minima de la consigna): nueva partida o seguir la guardada. */
final class PantallaInicio extends PanelConFondo {

    private static final long serialVersionUID = 1L;

    private final Boton cargar = new Boton("Continuar partida");

    PantallaInicio(ControladorJuego controlador) {
        super(new GridBagLayout(), "fondos/mazmorra", 120);
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = 0;
        posicion.insets = new Insets(6, 0, 6, 0);

        add(Estilo.texto("RPG por turnos", Estilo.TITULO.deriveFont(52f), Estilo.DORADO), posicion);
        add(Estilo.texto("Veinte etapas, tres heroes, un Señor de las Sombras", Estilo.SUBTITULO,
                Estilo.TEXTO), posicion);
        posicion.insets = new Insets(40, 0, 6, 0);

        Boton nueva = new Boton("Nueva partida");
        nueva.addActionListener(e -> controlador.nuevaPartida());
        cargar.addActionListener(e -> controlador.cargarPartida());
        Boton salir = new Boton("Salir");
        salir.addActionListener(e -> System.exit(0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        botones.setOpaque(false);
        botones.add(nueva);
        botones.add(cargar);
        botones.add(salir);
        add(botones, posicion);

        posicion.insets = new Insets(60, 0, 0, 0);
        add(Estilo.texto("Paradigma Orientado a Objetos · UADE · Grupo 3", Estilo.CHICA,
                Estilo.TEXTO_SUAVE), posicion);
    }

    void mostrar(boolean hayPartidaGuardada) {
        cargar.setEnabled(hayPartidaGuardada);
    }
}
