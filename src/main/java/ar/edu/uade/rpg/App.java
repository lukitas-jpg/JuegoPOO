package ar.edu.uade.rpg;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.vista.VentanaPrincipal;

/**
 * Punto de entrada. Es la unica clase que conoce a las tres capas de MVC a la
 * vez: crea el modelo (Juego), el controlador y la ventana, y los conecta.
 */
public class App {

    public static void main(String[] args) {
        // Swing exige crear y tocar la interfaz desde su propio hilo (EDT).
        SwingUtilities.invokeLater(App::iniciar);
    }

    private static void iniciar() {
        ToolTipManager.sharedInstance().setInitialDelay(300);
        Juego juego = new Juego();
        ControladorJuego controlador = new ControladorJuego(juego);
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        controlador.setVista(ventana);
        controlador.iniciar();
        ventana.setVisible(true);
    }
}
