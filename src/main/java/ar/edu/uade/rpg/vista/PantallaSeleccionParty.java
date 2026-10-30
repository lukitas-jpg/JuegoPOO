package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.dto.ClaseDTO;

final class PantallaSeleccionParty extends PanelConFondo {
    private static final long serialVersionUID = 1L;

    private final ControladorJuego controlador;
    private final JPanel grilla = new JPanel(new GridLayout(2, 3, 16, 16));
    private final JLabel contador = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final Boton confirmar = new Boton("Empezar la aventura");
    private final List<Integer> elegidas = new ArrayList<>();
    private final List<CartaDeClase> cartas = new ArrayList<>();
    private int tamañoDeLaParty;

    PantallaSeleccionParty(ControladorJuego controlador) {
        super(new BorderLayout(0, 16), "fondos/mazmorra", 150);
        this.controlador = controlador;
        setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        JLabel titulo = Estilo.texto("Armá tu party", Estilo.TITULO, Estilo.DORADO);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        grilla.setOpaque(false);
        add(grilla, BorderLayout.CENTER);

        Boton volver = new Boton("Volver");
        volver.addActionListener(e -> controlador.volverAlInicio());
        confirmar.addActionListener(e -> controlador.confirmarParty(new ArrayList<>(elegidas)));
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        abajo.setOpaque(false);
        abajo.add(volver);
        abajo.add(contador);
        abajo.add(confirmar);
        add(abajo, BorderLayout.SOUTH);
    }

    void mostrar(List<ClaseDTO> clases, int tamaño) {
        this.tamañoDeLaParty = tamaño;
        elegidas.clear();
        cartas.clear();
        grilla.removeAll();
        for (int i = 0; i < clases.size(); i++) {
            CartaDeClase carta = new CartaDeClase(clases.get(i), i);
            cartas.add(carta);
            grilla.add(carta);
        }
        actualizar();
        revalidate();
        repaint();
    }

    private void tocar(int indice) {
        if (elegidas.contains(indice)) {
            elegidas.remove(Integer.valueOf(indice));
        } else if (elegidas.size() < tamañoDeLaParty) {
            elegidas.add(indice);
        }
        actualizar();
    }

    private void actualizar() {
        contador.setText("Elegidos: " + elegidas.size() + " / " + tamañoDeLaParty);
        confirmar.setEnabled(elegidas.size() == tamañoDeLaParty);
        for (CartaDeClase carta : cartas) {
            carta.repaint();
        }
    }

    private final class CartaDeClase extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int indice;

        CartaDeClase(ClaseDTO clase, int indice) {
            super(new BorderLayout(14, 0));
            this.indice = indice;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            add(new JLabel(new ImageIcon(CargadorDeImagenes.escalada(clase.imagen(), 1.5))), BorderLayout.WEST);

            JPanel texto = new JPanel(new GridLayout(0, 1, 0, 2));
            texto.setOpaque(false);
            texto.add(Estilo.texto(clase.nombre(), Estilo.SUBTITULO, Estilo.DORADO));
            JLabel descripcion = Estilo.texto("<html>" + clase.descripcion() + "</html>", Estilo.NORMAL, Estilo.TEXTO);
            texto.add(descripcion);
            texto.add(Estilo.texto("Vida " + clase.vida() + "  ·  Maná " + clase.mana()
                    + "  ·  Vel. " + clase.velocidad(), Estilo.CHICA, Estilo.TEXTO_SUAVE));
            add(texto, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    tocar(CartaDeClase.this.indice);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean elegida = elegidas.contains(indice);
            g2.setColor(elegida ? new Color(60, 52, 30, 230) : Estilo.PANEL);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(elegida ? Estilo.DORADO : Estilo.BORDE);
            g2.setStroke(new java.awt.BasicStroke(elegida ? 3f : 1f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
