package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.edu.uade.rpg.controlador.ControladorTienda;
import ar.edu.uade.rpg.dto.EquipableEnVentaDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.TiendaDTO;

final class PantallaTienda extends PanelConFondo {
    private static final long serialVersionUID = 1L;

    private final ControladorTienda controlador;
    private final JLabel titulo = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel oro = Estilo.texto("", Estilo.SUBTITULO, Estilo.DORADO);
    private final JPanel consumibles = new JPanel(new GridLayout(0, 1, 0, 8));
    private final JPanel slots = new JPanel(new GridLayout(0, 1, 0, 8));

    PantallaTienda(ControladorTienda controlador) {
        super(new BorderLayout(), "fondos/tienda", 90);
        this.controlador = controlador;

        JPanel encabezado = new FranjaOscura(new BorderLayout());
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(oro, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 24, 0));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        consumibles.setOpaque(false);
        slots.setOpaque(false);
        centro.add(columna("Consumibles", consumibles));
        centro.add(columna("Equipo", slots));
        add(centro, BorderLayout.CENTER);

        Boton seguir = new Boton("Seguir a la próxima etapa");
        seguir.addActionListener(e -> controlador.salir());
        JPanel abajo = new FranjaOscura(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        abajo.add(Estilo.texto("Lo que compres se guarda al salir de la tienda.", Estilo.NORMAL,
                Estilo.TEXTO_SUAVE));
        abajo.add(seguir);
        add(abajo, BorderLayout.SOUTH);
    }

    private static JPanel columna(String nombre, JPanel contenido) {
        JPanel columna = new JPanel(new BorderLayout(0, 10));
        columna.setOpaque(false);
        columna.add(Estilo.texto(nombre, Estilo.SUBTITULO, Estilo.DORADO), BorderLayout.NORTH);
        columna.add(contenido, BorderLayout.CENTER);
        return columna;
    }

    void mostrar(TiendaDTO tienda) {
        titulo.setText("Tienda  ·  etapa " + tienda.numeroEtapa() + " de 20");
        oro.setText(tienda.oro() + " de oro");

        consumibles.removeAll();
        for (int i = 0; i < tienda.consumibles().size(); i++) {
            consumibles.add(filaDeConsumible(tienda.consumibles().get(i), i));
        }

        slots.removeAll();
        for (int i = 0; i < tienda.slots().size(); i++) {
            slots.add(tarjetaDeSlot(tienda.slots().get(i), i));
        }
        Boton reroll = new Boton("Cambiar el equipo  ·  " + tienda.costoDelReroll() + " de oro");
        reroll.setToolTipText("Vuelve a sortear los 3 slots. Cada vez sale más caro.");
        reroll.setEnabled(tienda.puedeRerollear());
        reroll.addActionListener(e -> controlador.rerollear());
        JPanel filaReroll = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaReroll.setOpaque(false);
        filaReroll.add(reroll);
        slots.add(filaReroll);

        revalidate();
        repaint();
    }

    private JPanel filaDeConsumible(ItemDTO item, int indice) {
        JPanel fila = new Recuadro(new BorderLayout(10, 0));
        fila.add(new JLabel(new ImageIcon(CargadorDeImagenes.escalada(item.imagen(), 0.75))), BorderLayout.WEST);

        JPanel texto = new JPanel(new GridLayout(2, 1));
        texto.setOpaque(false);
        texto.add(Estilo.texto(item.nombre() + "   (tenés " + item.cantidad() + " de " + item.tope() + ")",
                Estilo.NEGRITA, Estilo.TEXTO));
        texto.add(Estilo.texto(item.descripcion(), Estilo.CHICA, Estilo.TEXTO_SUAVE));
        fila.add(texto, BorderLayout.CENTER);

        Boton comprar = new Boton(item.precio() + " de oro");
        comprar.setEnabled(item.disponible());
        comprar.addActionListener(e -> controlador.comprarConsumible(indice));
        fila.add(centrado(comprar), BorderLayout.EAST);
        return fila;
    }

    private JPanel tarjetaDeSlot(EquipableEnVentaDTO slot, int indice) {
        JPanel tarjeta = new Recuadro(new BorderLayout(10, 0));
        if (slot.vendido()) {
            tarjeta.add(Estilo.texto("Vendido", Estilo.SUBTITULO, Estilo.TEXTO_SUAVE), BorderLayout.CENTER);
            return tarjeta;
        }
        ItemDTO item = slot.item();

        tarjeta.add(new JLabel(new ImageIcon(CargadorDeImagenes.redimensionar(
                CargadorDeImagenes.imagen(item.imagen(), item.imagenDeRespaldo()), 68, 68))), BorderLayout.WEST);

        JPanel texto = new JPanel(new GridLayout(0, 1));
        texto.setOpaque(false);
        texto.add(Estilo.texto(item.nombre(), Estilo.NEGRITA, Estilo.colorDeRareza(item.rareza())));
        texto.add(Estilo.texto(rarezaConTilde(item.rareza()) + "  ·  para " + slot.destinatario(),
                Estilo.CHICA, Estilo.TEXTO_SUAVE));
        texto.add(Estilo.texto(slot.bonificaciones(), Estilo.CHICA, Estilo.TEXTO));
        if (slot.reemplaza() != null) {
            texto.add(Estilo.texto("Reemplaza a " + slot.reemplaza() + ", que se descarta.",
                    Estilo.CHICA, Estilo.ROJO));
        }
        tarjeta.add(texto, BorderLayout.CENTER);

        Boton comprar = new Boton(item.precio() + " de oro");
        comprar.setEnabled(item.disponible());
        comprar.addActionListener(e -> controlador.comprarEquipable(indice));
        tarjeta.add(centrado(comprar), BorderLayout.EAST);
        return tarjeta;
    }

    private static String rarezaConTilde(String rareza) {
        return switch (rareza) {
            case "epico" -> "Épico";
            case "raro" -> "Raro";
            case "legendario" -> "Legendario";
            default -> rareza;
        };
    }

    private static JPanel centrado(Boton boton) {
        JPanel panel = new JPanel(new java.awt.GridBagLayout());
        panel.setOpaque(false);
        panel.add(boton);
        return panel;
    }

    private static final class Recuadro extends JPanel {
        private static final long serialVersionUID = 1L;

        Recuadro(BorderLayout layout) {
            super(layout);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Estilo.PANEL);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.setColor(Estilo.BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
