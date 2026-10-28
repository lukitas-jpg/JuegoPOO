package ar.edu.uade.rpg.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/** Barra de vida, mana o experiencia con el numero adentro: "Vida 85 / 120". */
final class Barra extends JComponent {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int actual;
    private final int maximo;
    private final Color color;

    Barra(String nombre, int actual, int maximo, Color color, int alto) {
        this.nombre = nombre;
        this.actual = actual;
        this.maximo = Math.max(1, maximo);
        this.color = color;
        setPreferredSize(new Dimension(150, alto));
        setMinimumSize(new Dimension(60, alto));
        setFont(alto >= 16 ? Estilo.NEGRITA : Estilo.CHICA);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int ancho = getWidth();
        int alto = getHeight();
        g2.setColor(new Color(10, 10, 14));
        g2.fillRect(0, 0, ancho, alto);
        int lleno = (int) Math.round((ancho - 2) * Math.min(1.0, Math.max(0, actual) / (double) maximo));
        g2.setColor(color);
        g2.fillRect(1, 1, lleno, alto - 2);
        g2.setColor(color.brighter());
        g2.drawLine(1, 1, lleno, 1);
        g2.setColor(Estilo.BORDE);
        g2.drawRect(0, 0, ancho - 1, alto - 1);

        String texto = nombre + " " + Math.max(0, actual) + " / " + maximo;
        g2.setFont(getFont());
        FontMetrics medidas = g2.getFontMetrics();
        int x = (ancho - medidas.stringWidth(texto)) / 2;
        int y = (alto + medidas.getAscent() - medidas.getDescent()) / 2;
        g2.setColor(Color.BLACK);
        g2.drawString(texto, x + 1, y + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, x, y);
        g2.dispose();
    }
}
