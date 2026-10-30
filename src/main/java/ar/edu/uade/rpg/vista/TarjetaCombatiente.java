package ar.edu.uade.rpg.vista;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.EfectoDTO;

final class TarjetaCombatiente extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TAMAÑO_ICONO = 34;

    enum Formato { COMPLETA, VERTICAL, CHICA }

    private final boolean tieneElTurno;

    TarjetaCombatiente(CombatienteDTO combatiente, Formato formato) {
        super(new BorderLayout(8, 2));
        this.tieneElTurno = combatiente.tieneElTurno();
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 10));
        boolean compacta = formato != Formato.COMPLETA;

        JLabel dibujo = dibujo(combatiente, formato == Formato.CHICA);
        if (compacta) {
            dibujo.setHorizontalAlignment(SwingConstants.CENTER);
            add(dibujo, BorderLayout.NORTH);
        } else {
            add(dibujo, BorderLayout.WEST);
        }

        JPanel datos = new JPanel();
        datos.setOpaque(false);
        datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));
        datos.add(titulo(combatiente));
        int altoBarra = compacta ? 12 : 17;
        datos.add(new Barra("Vida", combatiente.vidaActual(), combatiente.vidaMaxima(), Estilo.VIDA, altoBarra));
        if (!compacta && combatiente.manaMaximo() > 0) {
            datos.add(separacion());
            datos.add(new Barra("Maná", combatiente.manaActual(), combatiente.manaMaximo(), Estilo.MANA, altoBarra));
        }
        if (!compacta && combatiente.esPersonaje()) {
            datos.add(separacion());
            datos.add(new Barra("Exp", combatiente.experiencia(), combatiente.experienciaParaSubir(),
                    Estilo.EXPERIENCIA, 13));
        }
        datos.add(efectos(combatiente, compacta));

        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setOpaque(false);
        GridBagConstraints relleno = new GridBagConstraints();
        relleno.fill = GridBagConstraints.HORIZONTAL;
        relleno.weightx = 1;
        centrado.add(datos, relleno);
        add(centrado, BorderLayout.CENTER);
    }

    private JLabel dibujo(CombatienteDTO combatiente, boolean aLaMitad) {
        BufferedImage imagen = aLaMitad
                ? CargadorDeImagenes.escalada(combatiente.imagen(), 0.5)
                : CargadorDeImagenes.imagen(combatiente.imagen());
        ImageIcon icono = combatiente.vivo()
                ? new ImageIcon(imagen)
                : new ImageIcon(GrayFilter.createDisabledImage(imagen));
        JLabel etiqueta = new JLabel(icono);
        etiqueta.setVerticalAlignment(SwingConstants.BOTTOM);
        return etiqueta;
    }

    private JPanel titulo(CombatienteDTO combatiente) {
        JPanel fila = filaIzquierda();
        String nombre = combatiente.nombre();
        if (combatiente.esPersonaje()) {
            nombre += "  ·  nivel " + combatiente.nivel();
        }
        fila.add(Estilo.texto(nombre, Estilo.NEGRITA, tieneElTurno ? Estilo.DORADO : Estilo.TEXTO));
        if (!combatiente.vivo()) {
            fila.add(Estilo.texto("  CAÍDO", Estilo.NEGRITA, Estilo.ROJO));
        }
        return fila;
    }

    private JPanel efectos(CombatienteDTO combatiente, boolean compacta) {
        JPanel fila = filaIzquierda();
        int tamaño = compacta ? TAMAÑO_ICONO / 2 : TAMAÑO_ICONO;
        if (combatiente.defendiendo()) {
            fila.add(icono("estados/defendiendo", "Defendiendo: recibe la mitad de daño hasta su próximo turno", tamaño));
        }
        for (EfectoDTO efecto : combatiente.efectos()) {
            fila.add(icono(efecto.imagen(),
                    efecto.nombre() + " (" + efecto.turnosRestantes() + " turnos)", tamaño));
        }
        if (fila.getComponentCount() == 0) {
            fila.add(Estilo.texto(" ", Estilo.CHICA, Estilo.TEXTO));
        }
        return fila;
    }

    private JLabel icono(String clave, String descripcion, int tamaño) {
        BufferedImage imagen = CargadorDeImagenes.imagen(clave);
        JLabel etiqueta = new JLabel(new ImageIcon(
                CargadorDeImagenes.redimensionar(imagen, tamaño, tamaño)));
        etiqueta.setToolTipText(descripcion);
        return etiqueta;
    }

    private static JPanel filaIzquierda() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 1));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        return fila;
    }

    private static JPanel separacion() {
        JPanel espacio = new JPanel();
        espacio.setOpaque(false);
        espacio.setMaximumSize(new java.awt.Dimension(10, 3));
        return espacio;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Estilo.PANEL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        if (tieneElTurno) {
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(Estilo.DORADO);
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
        } else {
            g2.setColor(new Color(Estilo.BORDE.getRed(), Estilo.BORDE.getGreen(), Estilo.BORDE.getBlue(), 120));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
