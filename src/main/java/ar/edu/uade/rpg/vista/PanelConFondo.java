package ar.edu.uade.rpg.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Un panel que dibuja una imagen de fondo cubriendo todo, sin deformarla, y
 * opcionalmente la oscurece para que el texto de arriba se lea.
 */
class PanelConFondo extends JPanel {

    private static final long serialVersionUID = 1L;

    private BufferedImage fondo;
    private final int oscuridad;

    /** oscuridad: 0 la deja como esta, 255 la tapa entera de negro. */
    PanelConFondo(LayoutManager layout, String claveDeFondo, int oscuridad) {
        super(layout);
        this.fondo = CargadorDeImagenes.imagen(claveDeFondo);
        this.oscuridad = oscuridad;
    }

    final void cambiarFondo(String claveDeFondo) {
        fondo = CargadorDeImagenes.imagen(claveDeFondo);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Estilo.FONDO);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double escala = Math.max(getWidth() / (double) fondo.getWidth(), getHeight() / (double) fondo.getHeight());
        int ancho = (int) Math.ceil(fondo.getWidth() * escala);
        int alto = (int) Math.ceil(fondo.getHeight() * escala);
        g2.drawImage(fondo, (getWidth() - ancho) / 2, (getHeight() - alto) / 2, ancho, alto, null);
        if (oscuridad > 0) {
            g2.setColor(new Color(0, 0, 0, oscuridad));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}
