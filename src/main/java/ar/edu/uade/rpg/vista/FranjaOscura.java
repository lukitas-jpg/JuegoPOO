package ar.edu.uade.rpg.vista;

import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

final class FranjaOscura extends JPanel {
    private static final long serialVersionUID = 1L;

    FranjaOscura(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Estilo.PANEL);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Estilo.BORDE);
        g.drawLine(0, 0, getWidth(), 0);
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        super.paintComponent(g);
    }
}
