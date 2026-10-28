/**
 * CONTROLADOR (la C de MVC): conecta las pantallas con el modelo.
 *
 * ControladorJuego (inicio, seleccion, party entre etapas), ControladorCombate
 * (batalla) y ControladorTienda (tienda). Reciben los clicks
 * de la vista con indices, le hablan a Juego y le devuelven DTOs.
 *
 * VistaDelJuego es la interfaz que implementa la ventana: los controladores no
 * importan nada de Swing ni del paquete vista. ConversorDTO arma los DTOs.
 */
package ar.edu.uade.rpg.controlador;
