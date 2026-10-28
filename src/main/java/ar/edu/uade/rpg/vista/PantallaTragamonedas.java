package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import ar.edu.uade.rpg.controlador.ControladorTienda;
import ar.edu.uade.rpg.dto.MaquinaDTO;
import ar.edu.uade.rpg.dto.TiradaDTO;

/**
 * La maquina de slots de las etapas 9 y 19. Aparece antes de la tienda.
 *
 * El resultado ya viene decidido por el modelo: la pantalla solo hace girar
 * los rodillos un rato con un Timer y los va frenando, de a uno, sobre los
 * simbolos que salieron.
 */
final class PantallaTragamonedas extends PanelConFondo {

    private static final long serialVersionUID = 1L;
    private static final int CUADRO_MS = 70;
    /** En que cuadro de la animacion frena cada rodillo. */
    private static final int[] FRENA_EN = {14, 22, 30};

    private final ControladorTienda controlador;
    private final JLabel titulo = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel oro = Estilo.texto("", Estilo.SUBTITULO, Estilo.DORADO);
    private final JLabel mensaje = Estilo.texto(" ", Estilo.TITULO.deriveFont(26f), Estilo.DORADO);
    private final Boton tirar = new Boton("Tirar");
    private final Boton pasar = new Boton("Pasar a la tienda");
    private final DibujoDeLaMaquina maquina = new DibujoDeLaMaquina();
    private final Timer animacion = new Timer(CUADRO_MS, e -> siguienteCuadro());
    private final Random azarDeLaAnimacion = new Random();

    private List<String> simbolosPosibles = List.of();
    private TiradaDTO tiradaEnCurso;
    private MaquinaDTO despuesDeLaTirada;
    private int cuadro;

    PantallaTragamonedas(ControladorTienda controlador) {
        super(new BorderLayout(), "fondos/tienda", 120);
        this.controlador = controlador;

        JPanel encabezado = new FranjaOscura(new BorderLayout());
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(oro, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        JPanel conMaquina = new JPanel(new GridBagLayout());
        conMaquina.setOpaque(false);
        conMaquina.add(maquina);
        centro.add(conMaquina, BorderLayout.CENTER);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        centro.add(mensaje, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        tirar.addActionListener(e -> controlador.tirar());
        pasar.addActionListener(e -> controlador.pasarALaTienda());
        JPanel abajo = new FranjaOscura(new FlowLayout(FlowLayout.CENTER, 16, 10));
        abajo.add(Estilo.texto("La primera tirada es gratis. Si pasás a la tienda, no podés volver.",
                Estilo.NORMAL, Estilo.TEXTO_SUAVE));
        abajo.add(tirar);
        abajo.add(pasar);
        add(abajo, BorderLayout.SOUTH);
    }

    void mostrar(MaquinaDTO datos) {
        animacion.stop();
        simbolosPosibles = datos.simbolos();
        titulo.setText("Máquina de slots  ·  etapa " + datos.numeroEtapa() + " de 20");
        mensaje.setText(" ");
        maquina.mostrar(List.of(), false);
        actualizarBotones(datos);
    }

    /** El controlador ya tiro: aca solo se anima y se muestra lo que salio. */
    void animar(TiradaDTO tirada, MaquinaDTO despues) {
        this.tiradaEnCurso = tirada;
        this.despuesDeLaTirada = despues;
        this.cuadro = 0;
        tirar.setEnabled(false);
        pasar.setEnabled(false);
        mensaje.setText(" ");
        animacion.start();
    }

    private void siguienteCuadro() {
        cuadro++;
        String[] visibles = new String[3];
        for (int rodillo = 0; rodillo < 3; rodillo++) {
            visibles[rodillo] = cuadro >= FRENA_EN[rodillo]
                    ? tiradaEnCurso.simbolos().get(rodillo)
                    : simbolosPosibles.get(azarDeLaAnimacion.nextInt(simbolosPosibles.size()));
        }
        boolean termino = cuadro >= FRENA_EN[2];
        maquina.mostrar(List.of(visibles), !termino);
        if (termino) {
            animacion.stop();
            mensaje.setText(tiradaEnCurso.oroGanado() > 0
                    ? "¡x" + tiradaEnCurso.multiplicador() + "!  Ganaste " + tiradaEnCurso.oroGanado() + " de oro"
                    : "Nada esta vez");
            actualizarBotones(despuesDeLaTirada);
        }
    }

    private void actualizarBotones(MaquinaDTO datos) {
        oro.setText(datos.oro() + " de oro");
        tirar.setText(datos.costoDeLaTirada() == 0 ? "Tirar  ·  gratis" : "Tirar  ·  " + datos.costoDeLaTirada() + " de oro");
        tirar.setPreferredSize(new Dimension(170, 32));
        tirar.setEnabled(datos.puedeTirar());
        pasar.setEnabled(true);
    }

    /** La imagen de la maquina con los tres simbolos encima, en las ventanas de los rodillos. */
    private static final class DibujoDeLaMaquina extends JComponent {

        private static final long serialVersionUID = 1L;
        /** 1.5 deja cada pixel del dibujo en 3 pixeles de pantalla: sigue nitido. */
        private static final double ESCALA = 1.5;
        private static final int[] X_DE_LOS_RODILLOS = {56, 152, 248};
        private static final int Y_DE_LOS_RODILLOS = 118;
        private static final int LADO_DEL_SIMBOLO = 68;

        private List<String> simbolos = List.of();
        private boolean girando;

        DibujoDeLaMaquina() {
            BufferedImage imagen = CargadorDeImagenes.imagen("slots/maquina");
            setPreferredSize(new Dimension((int) (imagen.getWidth() * ESCALA), (int) (imagen.getHeight() * ESCALA)));
        }

        void mostrar(List<String> nuevos, boolean estaGirando) {
            this.simbolos = nuevos;
            this.girando = estaGirando;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.scale(ESCALA, ESCALA);
            g2.drawImage(CargadorDeImagenes.imagen(girando ? "slots/maquina_palanca_abajo" : "slots/maquina"),
                    0, 0, null);
            for (int i = 0; i < simbolos.size(); i++) {
                g2.drawImage(CargadorDeImagenes.imagen(simbolos.get(i)),
                        X_DE_LOS_RODILLOS[i], Y_DE_LOS_RODILLOS, LADO_DEL_SIMBOLO, LADO_DEL_SIMBOLO, null);
            }
            g2.dispose();
        }
    }
}
