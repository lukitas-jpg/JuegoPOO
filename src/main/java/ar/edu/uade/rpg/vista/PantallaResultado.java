package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;

/**
 * Pantalla de resultado (minima de la consigna): como termino la pelea, que se
 * gano y como quedo la party. Tambien muestra la victoria final y la derrota.
 */
final class PantallaResultado extends PanelConFondo {

    private static final long serialVersionUID = 1L;

    private final JLabel titulo = Estilo.texto("", Estilo.TITULO.deriveFont(44f), Estilo.DORADO);
    private final JLabel detalle = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JPanel party = new JPanel(new GridLayout(1, 3, 16, 0));
    private final Boton continuar = new Boton("Continuar");

    PantallaResultado(ControladorJuego controlador) {
        super(new BorderLayout(0, 24), "fondos/mazmorra", 170);
        setBorder(BorderFactory.createEmptyBorder(70, 40, 50, 40));

        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        detalle.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel arriba = new JPanel(new GridLayout(2, 1, 0, 10));
        arriba.setOpaque(false);
        arriba.add(titulo);
        arriba.add(detalle);
        add(arriba, BorderLayout.NORTH);

        party.setOpaque(false);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(party, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        continuar.addActionListener(e -> controlador.continuar());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        abajo.setOpaque(false);
        abajo.add(continuar);
        add(abajo, BorderLayout.SOUTH);
    }

    void mostrar(ResultadoDTO resultado) {
        cambiarFondo(Fondos.paraEtapa(resultado.numeroEtapa()));
        if (resultado.runGanada()) {
            titulo.setText("¡Ganaste la run!");
            detalle.setText("El Señor de las Sombras cayo. Superaste las 20 etapas.");
            continuar.setText("Volver al inicio");
        } else if (resultado.runTerminada()) {
            titulo.setForeground(Estilo.ROJO);
            titulo.setText("Derrota");
            detalle.setText("Cayo toda la party en la etapa " + resultado.numeroEtapa()
                    + ". La partida guardada se borro.");
            continuar.setText("Volver al inicio");
        } else {
            titulo.setForeground(Estilo.DORADO);
            titulo.setText("Victoria");
            detalle.setText("Etapa " + resultado.numeroEtapa() + " superada:  +" + resultado.experiencia()
                    + " de experiencia para cada uno  ·  +" + resultado.oro() + " de oro");
            continuar.setText("Continuar");
        }
        party.removeAll();
        for (CombatienteDTO miembro : resultado.party()) {
            party.add(new TarjetaCombatiente(miembro, false));
        }
        revalidate();
        repaint();
    }
}
