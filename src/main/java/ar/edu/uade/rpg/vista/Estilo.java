package ar.edu.uade.rpg.vista;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

/** Colores y letras de todas las pantallas, en un solo lugar. */
final class Estilo {

    static final Color FONDO = new Color(24, 22, 30);
    static final Color PANEL = new Color(18, 16, 24, 215);
    static final Color PANEL_CLARO = new Color(44, 40, 54, 230);
    static final Color BORDE = new Color(92, 80, 64);
    static final Color DORADO = new Color(232, 190, 92);
    static final Color TEXTO = new Color(236, 230, 218);
    static final Color TEXTO_SUAVE = new Color(170, 162, 150);
    static final Color VIDA = new Color(196, 58, 58);
    static final Color MANA = new Color(64, 112, 210);
    static final Color EXPERIENCIA = new Color(200, 164, 50);
    static final Color VERDE = new Color(96, 180, 96);
    static final Color ROJO = new Color(220, 90, 80);

    static final Font TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 34);
    static final Font SUBTITULO = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    static final Font NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    static final Font NEGRITA = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    static final Font CHICA = new Font(Font.SANS_SERIF, Font.PLAIN, 11);

    private Estilo() {
    }

    static JLabel texto(String contenido, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(contenido);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        return etiqueta;
    }

    /** El color de cada rareza, para el nombre de los objetos. */
    static Color colorDeRareza(String rareza) {
        return switch (rareza) {
            case "raro" -> new Color(90, 150, 240);
            case "epico" -> new Color(184, 110, 240);
            case "legendario" -> new Color(250, 160, 50);
            default -> TEXTO;
        };
    }
}
