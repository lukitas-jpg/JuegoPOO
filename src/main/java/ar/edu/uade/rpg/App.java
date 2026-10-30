package ar.edu.uade.rpg;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.modelo.partida.Juego;
import ar.edu.uade.rpg.vista.VentanaPrincipal;

public class App {
    public static void main(String[] args) {
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
