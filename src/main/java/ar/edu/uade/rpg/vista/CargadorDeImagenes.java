package ar.edu.uade.rpg.vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Carga las imagenes de src/main/resources/imagenes/ y las guarda en memoria
 * para no leer el mismo archivo dos veces.
 *
 * Si una imagen no existe devuelve un cuadrado de color con la inicial, en vez
 * de romper la pantalla. Asi el juego anda aunque falte algun dibujo.
 */
final class CargadorDeImagenes {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private CargadorDeImagenes() {
    }

    /** Ej: imagen("enemigos/bruto") carga /imagenes/enemigos/bruto.png */
    static BufferedImage imagen(String clave) {
        return CACHE.computeIfAbsent(clave, CargadorDeImagenes::cargar);
    }

    /**
     * La imagen de clave si existe; si no, la de respaldo. La usan los
     * equipables: cada uno puede tener su dibujo, y si falta se usa el
     * generico de su rareza.
     */
    static BufferedImage imagen(String clave, String respaldo) {
        boolean existe = CargadorDeImagenes.class.getResource("/imagenes/" + clave + ".png") != null;
        return imagen(existe ? clave : respaldo);
    }

    /**
     * La misma imagen agrandada o achicada, sin suavizar: el pixel art se ve
     * con bordes nitidos en vez de borroso.
     */
    static BufferedImage escalada(String clave, double factor) {
        BufferedImage original = imagen(clave);
        int ancho = Math.max(1, (int) Math.round(original.getWidth() * factor));
        int alto = Math.max(1, (int) Math.round(original.getHeight() * factor));
        return CACHE.computeIfAbsent(clave + "@" + factor, c -> redimensionar(original, ancho, alto));
    }

    static BufferedImage redimensionar(BufferedImage original, int ancho, int alto) {
        BufferedImage nueva = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = nueva.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(original, 0, 0, ancho, alto, null);
        g.dispose();
        return nueva;
    }

    private static BufferedImage cargar(String clave) {
        try (InputStream entrada = CargadorDeImagenes.class.getResourceAsStream("/imagenes/" + clave + ".png")) {
            if (entrada != null) {
                BufferedImage imagen = ImageIO.read(entrada);
                if (imagen != null) {
                    return imagen;
                }
            }
        } catch (IOException e) {
            // cae al reemplazo de abajo
        }
        return reemplazo(clave);
    }

    private static BufferedImage reemplazo(String clave) {
        boolean esFondo = clave.startsWith("fondos/");
        int ancho = esFondo ? 960 : 64;
        int alto = esFondo ? 540 : 64;
        String nombre = clave.substring(clave.indexOf('/') + 1);

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.getHSBColor((clave.hashCode() & 0xFF) / 255f, 0.45f, 0.55f));
        g.fillRoundRect(0, 0, ancho, alto, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        String inicial = nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase();
        FontMetrics medidas = g.getFontMetrics();
        g.drawString(inicial, (ancho - medidas.stringWidth(inicial)) / 2, (alto + medidas.getAscent()) / 2 - 4);
        g.dispose();
        return imagen;
    }
}
