/**
 * VISTA (la V de MVC): la ventana y las pantallas Swing.
 *
 * VentanaPrincipal (un JFrame con CardLayout) implementa VistaDelJuego, que
 * es lo unico que conocen los controladores. Las pantallas reciben DTOs, los
 * muestran y avisan los clicks al controlador con indices. No importan nada
 * del modelo.
 *
 * Pantallas: inicio, seleccion de la party, batalla, resultado, estado de la
 * party (con el inventario) y tienda.
 */
package ar.edu.uade.rpg.vista;
