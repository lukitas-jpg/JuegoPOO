package ar.edu.uade.rpg.vista;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

final class Aviso extends JComponent {
    private static final long serialVersionUID = 1L;

    private String texto = "";

    void setTexto(String texto) {
        this.texto = texto;
        FontMetrics medidas = getFontMetrics(Estilo.NEGRITA);
        setPreferredSize(new Dimension(medidas.stringWidth(texto) + 40, 40));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Estilo.PANEL_CLARO);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setColor(Estilo.DORADO);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        g2.setFont(Estilo.NEGRITA);
        g2.setColor(Estilo.TEXTO);
        FontMetrics medidas = g2.getFontMetrics();
        g2.drawString(texto, 20, (getHeight() + medidas.getAscent() - medidas.getDescent()) / 2);
        g2.dispose();
    }
}
