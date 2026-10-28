package ar.edu.uade.rpg.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

import ar.edu.uade.rpg.controlador.ControladorCombate;
import ar.edu.uade.rpg.dto.CombatienteDTO;
import ar.edu.uade.rpg.dto.EstadoCombateDTO;
import ar.edu.uade.rpg.dto.ItemDTO;
import ar.edu.uade.rpg.dto.MovimientoDTO;

/**
 * Pantalla de batalla (minima de la consigna).
 *
 * Arriba la etapa y la ronda; en el medio la party a la izquierda y los
 * enemigos a la derecha, sobre el fondo de la zona; abajo las acciones y el
 * registro.
 *
 * No calcula nada: muestra el EstadoCombateDTO y le devuelve indices al
 * controlador. Cuando no le toca al jugador, espera PAUSA_MS y pide el turno
 * siguiente, asi cada ataque enemigo se ve por separado.
 */
final class PantallaBatalla extends PanelConFondo {

    private static final long serialVersionUID = 1L;
    private static final int PAUSA_MS = 800;
    private static final int LINEAS_DEL_REGISTRO = 40;

    private final ControladorCombate controlador;
    private final JLabel etapa = Estilo.texto("", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel ronda = Estilo.texto("", Estilo.SUBTITULO, Estilo.DORADO);
    private final JPanel aliados = new JPanel();
    private final JPanel enemigos = new JPanel();
    private final JLabel indicacion = Estilo.texto(" ", Estilo.SUBTITULO, Estilo.DORADO);
    private final JPanel botones = new JPanel(new GridLayout(0, 3, 8, 8));
    private final JTextArea registro = new JTextArea();
    private final Timer turnoAutomatico;
    private EstadoCombateDTO estado;

    PantallaBatalla(ControladorCombate controlador) {
        super(new BorderLayout(), "fondos/mazmorra", 30);
        this.controlador = controlador;
        turnoAutomatico = new Timer(PAUSA_MS, e -> controlador.avanzarTurnoAutomatico());
        turnoAutomatico.setRepeats(false);

        add(armarEncabezado(), BorderLayout.NORTH);
        add(armarCampo(), BorderLayout.CENTER);
        add(armarParteDeAbajo(), BorderLayout.SOUTH);
    }

    private JPanel armarEncabezado() {
        JPanel encabezado = new FranjaOscura(new BorderLayout());
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        encabezado.add(etapa, BorderLayout.WEST);
        encabezado.add(ronda, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel armarCampo() {
        JPanel campo = new JPanel(new GridLayout(1, 2, 80, 0));
        campo.setOpaque(false);
        campo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        aliados.setOpaque(false);
        aliados.setLayout(new BoxLayout(aliados, BoxLayout.Y_AXIS));
        enemigos.setOpaque(false);
        campo.add(aliados);
        campo.add(enemigos);
        return campo;
    }

    private JPanel armarParteDeAbajo() {
        JPanel acciones = new JPanel(new BorderLayout(0, 8));
        acciones.setOpaque(false);
        botones.setOpaque(false);
        acciones.add(indicacion, BorderLayout.NORTH);
        acciones.add(botones, BorderLayout.CENTER);

        registro.setEditable(false);
        registro.setLineWrap(true);
        registro.setWrapStyleWord(true);
        registro.setFont(Estilo.CHICA);
        registro.setForeground(Estilo.TEXTO);
        registro.setBackground(Estilo.FONDO);
        JScrollPane desplazable = new JScrollPane(registro);
        desplazable.setPreferredSize(new Dimension(390, 150));
        desplazable.setBorder(BorderFactory.createLineBorder(Estilo.BORDE));

        JPanel abajo = new FranjaOscura(new BorderLayout(16, 0));
        abajo.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        abajo.add(acciones, BorderLayout.CENTER);
        abajo.add(desplazable, BorderLayout.EAST);
        return abajo;
    }

    // ------------------------------------------------------------ actualizar

    void mostrar(EstadoCombateDTO nuevo) {
        this.estado = nuevo;
        cambiarFondo(Fondos.paraEtapa(nuevo.numeroEtapa()));
        etapa.setText(nuevo.descripcionEtapa());
        ronda.setText("Ronda " + nuevo.numeroRonda());
        mostrarCombatientes();
        mostrarRegistro();

        if (nuevo.turnoDelJugador()) {
            mostrarMenuPrincipal();
        } else {
            indicacion.setText("Turno de " + nuevo.nombreTurnoActual() + "...");
            limpiarBotones();
            turnoAutomatico.restart();
        }
        revalidate();
        repaint();
    }

    private void mostrarCombatientes() {
        aliados.removeAll();
        for (CombatienteDTO aliado : estado.aliados()) {
            aliados.add(new TarjetaCombatiente(aliado, false));
            aliados.add(espacio());
        }

        // Con muchos enemigos (el Nigromante llega con cinco esqueletos) las
        // tarjetas se achican y se acomodan en mas columnas.
        int cantidad = estado.enemigos().size();
        boolean compactas = cantidad > 4;
        int columnas = cantidad <= 3 ? 1 : cantidad <= 8 ? 2 : 3;
        enemigos.removeAll();
        enemigos.setLayout(new GridLayout(0, columnas, 8, 8));
        for (CombatienteDTO enemigo : estado.enemigos()) {
            enemigos.add(new TarjetaCombatiente(enemigo, compactas));
        }
        // Relleno para que pocas tarjetas no se estiren a todo el alto.
        for (int i = cantidad; i < 3; i++) {
            enemigos.add(espacio());
        }
    }

    private void mostrarRegistro() {
        List<String> lineas = estado.registro();
        List<String> ultimas = lineas.subList(Math.max(0, lineas.size() - LINEAS_DEL_REGISTRO), lineas.size());
        registro.setText(String.join("\n", ultimas));
        registro.setCaretPosition(registro.getDocument().getLength());
    }

    // --------------------------------------------------------------- menues

    /** Los 4 movimientos (los que no se pueden usar, apagados), Defender y Objeto. */
    private void mostrarMenuPrincipal() {
        indicacion.setText("Turno de " + estado.nombreTurnoActual() + ": elegi una accion");
        limpiarBotones();
        List<MovimientoDTO> movimientos = estado.movimientos();
        for (int i = 0; i < movimientos.size(); i++) {
            MovimientoDTO movimiento = movimientos.get(i);
            int indice = i;
            String texto = movimiento.nombre();
            if (!movimiento.disponible()) {
                texto += "  ·  " + movimiento.motivo();
            } else if (movimiento.costoMana() > 0) {
                texto += "  ·  " + movimiento.costoMana() + " mana";
            }
            Boton boton = new Boton(texto);
            boton.setToolTipText(movimiento.descripcion());
            boton.setEnabled(movimiento.disponible());
            boton.addActionListener(e -> elegirMovimiento(indice, movimiento));
            botones.add(boton);
        }
        Boton defender = new Boton("Defender");
        defender.setToolTipText("Recibe la mitad de daño hasta su proximo turno y recupera algo de mana.");
        defender.addActionListener(e -> controlador.defender());
        botones.add(defender);

        Boton objeto = new Boton("Objeto (" + contarObjetos() + ")");
        objeto.setEnabled(!estado.inventario().isEmpty());
        objeto.addActionListener(e -> mostrarInventario());
        botones.add(objeto);
        refrescarBotones();
    }

    private void elegirMovimiento(int indice, MovimientoDTO movimiento) {
        if (!movimiento.eligeObjetivo()) {
            controlador.usarMovimiento(indice);
            return;
        }
        List<CombatienteDTO> objetivos = controlador.getObjetivosDeMovimiento(indice);
        mostrarObjetivos(movimiento.nombre() + ": elegi el objetivo", objetivos,
                objetivo -> controlador.usarMovimiento(indice, objetivo));
    }

    private void mostrarInventario() {
        indicacion.setText("Elegi un objeto");
        limpiarBotones();
        List<ItemDTO> inventario = estado.inventario();
        for (int i = 0; i < inventario.size(); i++) {
            ItemDTO item = inventario.get(i);
            int indice = i;
            Boton boton = new Boton(item.nombre() + "  x" + item.cantidad());
            boton.setToolTipText(item.descripcion());
            boton.addActionListener(e -> elegirConsumible(indice, item));
            botones.add(boton);
        }
        botones.add(botonVolver());
        refrescarBotones();
    }

    private void elegirConsumible(int indice, ItemDTO item) {
        List<CombatienteDTO> objetivos = controlador.getObjetivosDeConsumible(indice);
        if (objetivos.isEmpty()) {
            indicacion.setText(item.nombre() + ": no hay nadie sobre quien usarlo");
            return;
        }
        mostrarObjetivos(item.nombre() + ": ¿sobre quien?", objetivos,
                objetivo -> controlador.usarConsumible(indice, objetivo));
    }

    private void mostrarObjetivos(String pregunta, List<CombatienteDTO> objetivos, AlElegir alElegir) {
        indicacion.setText(pregunta);
        limpiarBotones();
        for (int i = 0; i < objetivos.size(); i++) {
            CombatienteDTO objetivo = objetivos.get(i);
            int indice = i;
            Boton boton = new Boton(objetivo.nombre() + "  " + objetivo.vidaActual() + "/" + objetivo.vidaMaxima());
            boton.addActionListener(e -> alElegir.elegido(indice));
            botones.add(boton);
        }
        botones.add(botonVolver());
        refrescarBotones();
    }

    /** Que hacer con el objetivo elegido: usar un movimiento o un objeto sobre el. */
    private interface AlElegir {
        void elegido(int indiceObjetivo);
    }

    private Boton botonVolver() {
        Boton volver = new Boton("Volver");
        volver.addActionListener(e -> mostrarMenuPrincipal());
        return volver;
    }

    private int contarObjetos() {
        int total = 0;
        for (ItemDTO item : estado.inventario()) {
            total += item.cantidad();
        }
        return total;
    }

    private void limpiarBotones() {
        botones.removeAll();
        refrescarBotones();
    }

    private void refrescarBotones() {
        botones.revalidate();
        botones.repaint();
    }

    private static JPanel espacio() {
        JPanel espacio = new JPanel();
        espacio.setOpaque(false);
        espacio.setPreferredSize(new Dimension(10, 8));
        espacio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        return espacio;
    }
}
