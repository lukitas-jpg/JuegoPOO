package ar.edu.uade.rpg;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del juego.
 *
 * Aca se va a armar la aplicacion completa: se crea el modelo (Juego),
 * se crean las vistas y se conectan con sus controladores. Es la unica
 * clase que conoce a las tres capas de MVC a la vez.
 *
 * Por ahora solo abre una ventana vacia para verificar que el proyecto
 * compila y que Swing funciona en la maquina de cada integrante.
 */
public class App {

    public static void main(String[] args) {
        // Swing exige crear y tocar la interfaz desde su propio hilo (EDT)
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void iniciar() {
        JFrame ventana = new JFrame("TPO POO - RPG por turnos");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.add(new JLabel("Proyecto configurado correctamente", SwingConstants.CENTER));
        ventana.setSize(800, 600);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
