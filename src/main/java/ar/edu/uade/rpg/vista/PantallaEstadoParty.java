package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.edu.uade.rpg.controlador.ControladorJuego;
import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.PartyDTO;

/**
 * Estado de la party entre etapas (minima de la consigna): nivel, experiencia,
 * vida, mana, equipo, oro e inventario. Desde aca se usan los objetos fuera de
 * combate y se sigue a la proxima etapa.
 */
final class PantallaEstadoParty extends PanelConFondo {

    private static final long serialVersionUID = 1L;

    private final ControladorJuego controlador;
    private final JLabel titulo = Estilo.texto("", Estilo.TITULO, Estilo.DORADO);
    private final JLabel proxima = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JPanel miembros = new JPanel(new GridLayout(1, 3, 16, 0));
    private final JPanel inventario = new JPanel(new GridLayout(0, 1, 0, 6));
    private PartyDTO party;

    PantallaEstadoParty(ControladorJuego controlador) {
        super(new BorderLayout(0, 18), "fondos/mazmorra", 160);
        this.controlador = controlador;
        setBorder(BorderFactory.createEmptyBorder(26, 36, 26, 36));

        JPanel arriba = new JPanel(new GridLayout(2, 1, 0, 6));
        arriba.setOpaque(false);
        arriba.add(titulo);
        arriba.add(proxima);
        add(arriba, BorderLayout.NORTH);

        miembros.setOpaque(false);
        inventario.setOpaque(false);
        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        centro.add(miembros, BorderLayout.NORTH);
        centro.add(inventario, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        Boton salir = new Boton("Volver al inicio");
        salir.setToolTipText("La partida ya quedo guardada al terminar la etapa.");
        salir.addActionListener(e -> controlador.volverAlInicio());
        Boton siguiente = new Boton("Siguiente etapa");
        siguiente.addActionListener(e -> controlador.siguienteEtapa());
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        abajo.setOpaque(false);
        abajo.add(salir);
        abajo.add(siguiente);
        add(abajo, BorderLayout.SOUTH);
    }

    void mostrar(PartyDTO nuevo) {
        this.party = nuevo;
        cambiarFondo(Fondos.paraEtapa(nuevo.proximaEtapa()));
        titulo.setText("Tu party  ·  " + nuevo.oro() + " de oro");
        proxima.setText("Proxima: " + nuevo.descripcionProximaEtapa()
                + "   La vida y el mana no se recuperan solos entre etapas.");

        miembros.removeAll();
        for (CombatienteDTO miembro : nuevo.miembros()) {
            miembros.add(conEquipo(miembro));
        }
        mostrarInventario();
        revalidate();
        repaint();
    }

    private JPanel conEquipo(CombatienteDTO miembro) {
        JPanel columna = new JPanel(new BorderLayout(0, 4));
        columna.setOpaque(false);
        columna.add(new TarjetaCombatiente(miembro, false), BorderLayout.CENTER);
        JPanel equipo = new JPanel(new GridLayout(2, 1));
        equipo.setOpaque(false);
        equipo.add(Estilo.texto("Arma: " + nombreONada(miembro.arma()), Estilo.NORMAL, Estilo.TEXTO_SUAVE));
        equipo.add(Estilo.texto("Armadura: " + nombreONada(miembro.armadura()), Estilo.NORMAL, Estilo.TEXTO_SUAVE));
        columna.add(equipo, BorderLayout.SOUTH);
        return columna;
    }

    private void mostrarInventario() {
        inventario.removeAll();
        inventario.add(Estilo.texto("Inventario", Estilo.SUBTITULO, Estilo.DORADO));
        List<ItemDTO> objetos = party.inventario();
        if (objetos.isEmpty()) {
            inventario.add(Estilo.texto("Vacio. Las pociones se compran en las tiendas (etapas 4, 9, 14 y 19).",
                    Estilo.NORMAL, Estilo.TEXTO_SUAVE));
        }
        for (int i = 0; i < objetos.size(); i++) {
            inventario.add(filaDeObjeto(objetos.get(i), i));
        }
        inventario.revalidate();
        inventario.repaint();
    }

    private JPanel filaDeObjeto(ItemDTO item, int indice) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fila.setOpaque(false);
        fila.add(new JLabel(new ImageIcon(CargadorDeImagenes.escalada(item.imagen(), 0.5))));
        fila.add(Estilo.texto(item.nombre() + "  x" + item.cantidad(), Estilo.NEGRITA, Estilo.TEXTO));
        fila.add(Estilo.texto(item.descripcion(), Estilo.NORMAL, Estilo.TEXTO_SUAVE));
        Boton usar = new Boton("Usar");
        usar.addActionListener(e -> elegirObjetivo(fila, item, indice));
        fila.add(usar);
        return fila;
    }

    /** Reemplaza el boton "Usar" por un boton por cada miembro sobre el que sirve. */
    private void elegirObjetivo(JPanel fila, ItemDTO item, int indice) {
        List<CombatienteDTO> objetivos = controlador.getObjetivosDeConsumible(indice);
        fila.remove(fila.getComponentCount() - 1);
        if (objetivos.isEmpty()) {
            fila.add(Estilo.texto("No hay nadie sobre quien usarlo.", Estilo.NEGRITA, Estilo.ROJO));
        }
        for (int i = 0; i < objetivos.size(); i++) {
            int objetivo = i;
            Boton boton = new Boton(objetivos.get(i).nombre());
            boton.addActionListener(e -> controlador.usarConsumible(indice, objetivo));
            fila.add(boton);
        }
        Boton cancelar = new Boton("Cancelar");
        cancelar.addActionListener(e -> mostrarInventario());
        fila.add(cancelar);
        fila.revalidate();
        fila.repaint();
    }

    private static String nombreONada(String nombre) {
        return nombre == null ? "nada" : nombre;
    }
}
