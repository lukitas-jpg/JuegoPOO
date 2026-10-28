package ar.edu.uade.rpg.vista;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;

/**
 * Boton con el estilo del juego: oscuro con borde dorado. Deshabilitado se ve
 * apagado. Se dibuja a mano porque el boton de Swing por defecto no deja
 * cambiar bien los colores en todos los sistemas.
 */
final class Boton extends JButton {

    private static final long serialVersionUID = 1L;

    Boton(String texto) {
        super(texto);
        setFont(Estilo.NEGRITA);
        setForeground(Estilo.TEXTO);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        FontMetrics medidas = getFontMetrics(getFont());
        setPreferredSize(new Dimension(Math.max(90, medidas.stringWidth(texto) + 28), 32));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        boolean activo = isEnabled();
        boolean encima = activo && getModel().isRollover();
        boolean apretado = activo && getModel().isPressed();

        g2.setColor(apretado ? Estilo.BORDE : encima ? Estilo.PANEL_CLARO.brighter() : Estilo.PANEL_CLARO);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setColor(activo ? Estilo.DORADO : Estilo.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

        g2.setFont(getFont());
        g2.setColor(activo ? Estilo.TEXTO : Estilo.TEXTO_SUAVE);
        FontMetrics medidas = g2.getFontMetrics();
        int x = (getWidth() - medidas.stringWidth(getText())) / 2;
        int y = (getHeight() + medidas.getAscent() - medidas.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
