package ar.edu.uade.rpg.vista;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.controlador.VistaDelJuego;
import ar.edu.uade.rpg.dto.ClaseDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.MaquinaDTO;
import ar.edu.uade.rpg.dto.PartyDTO;
import ar.edu.uade.rpg.dto.ResultadoDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;
import ar.edu.uade.rpg.dto.TiradaDTO;

/**
 * La unica ventana del juego. Cada pantalla es una "carta" de un CardLayout y
 * se muestra una por vez.
 *
 * Implementa VistaDelJuego: es lo unico que los controladores conocen de la
 * vista. No importa nada del modelo.
 */
public final class VentanaPrincipal extends JFrame implements VistaDelJuego {

    private static final long serialVersionUID = 1L;
    private static final int DURACION_DEL_AVISO_MS = 2600;

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Aviso aviso = new Aviso();
    private final Timer ocultarAviso = new Timer(DURACION_DEL_AVISO_MS, e -> aviso.setVisible(false));

    private final PantallaInicio inicio;
    private final PantallaSeleccionParty seleccion;
    private final PantallaBatalla batalla;
    private final PantallaResultado resultado;
    private final PantallaEstadoParty estadoParty;
    private final PantallaTienda tienda;
    private final PantallaTragamonedas tragamonedas;

    public VentanaPrincipal(ControladorJuego controlador) {
        super("RPG por turnos - Grupo 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 720));
        setSize(1100, 760);
        setLocationRelativeTo(null);

        inicio = new PantallaInicio(controlador);
        seleccion = new PantallaSeleccionParty(controlador);
        batalla = new PantallaBatalla(controlador.getControladorCombate());
        resultado = new PantallaResultado(controlador);
        estadoParty = new PantallaEstadoParty(controlador);
        tienda = new PantallaTienda(controlador.getControladorTienda());
        tragamonedas = new PantallaTragamonedas(controlador.getControladorTienda());

        contenedor.add(inicio, "inicio");
        contenedor.add(seleccion, "seleccion");
        contenedor.add(batalla, "batalla");
        contenedor.add(resultado, "resultado");
        contenedor.add(estadoParty, "estado");
        contenedor.add(tienda, "tienda");
        contenedor.add(tragamonedas, "tragamonedas");
        setContentPane(contenedor);

        getLayeredPane().add(aviso, JLayeredPane.POPUP_LAYER);
        aviso.setVisible(false);
        ocultarAviso.setRepeats(false);
    }

    @Override
    public void mostrarInicio(boolean hayPartidaGuardada) {
        inicio.mostrar(hayPartidaGuardada);
        cartas.show(contenedor, "inicio");
    }

    @Override
    public void mostrarSeleccionDeParty(List<ClaseDTO> clases, int tamañoDeLaParty) {
        seleccion.mostrar(clases, tamañoDeLaParty);
        cartas.show(contenedor, "seleccion");
    }

    @Override
    public void mostrarBatalla(EstadoCombateDTO estado) {
        batalla.mostrar(estado);
        cartas.show(contenedor, "batalla");
    }

    @Override
    public void mostrarResultado(ResultadoDTO datos) {
        resultado.mostrar(datos);
        cartas.show(contenedor, "resultado");
    }

    @Override
    public void mostrarEstadoDeLaParty(PartyDTO party) {
        estadoParty.mostrar(party);
        cartas.show(contenedor, "estado");
    }

    @Override
    public void mostrarTienda(TiendaDTO datos) {
        tienda.mostrar(datos);
        cartas.show(contenedor, "tienda");
    }

    @Override
    public void mostrarMaquina(MaquinaDTO maquina) {
        tragamonedas.mostrar(maquina);
        cartas.show(contenedor, "tragamonedas");
    }

    @Override
    public void mostrarTirada(TiradaDTO tirada, MaquinaDTO maquina) {
        tragamonedas.animar(tirada, maquina);
    }

    /** Un cartel abajo al centro que se va solo, sin ventanas que haya que cerrar. */
    @Override
    public void avisar(String mensaje) {
        aviso.setTexto(mensaje);
        Dimension tamaño = aviso.getPreferredSize();
        aviso.setBounds((getLayeredPane().getWidth() - tamaño.width) / 2,
                getLayeredPane().getHeight() - tamaño.height - 120, tamaño.width, tamaño.height);
        aviso.setVisible(true);
        ocultarAviso.restart();
    }
}
