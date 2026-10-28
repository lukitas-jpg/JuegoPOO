package ar.edu.uade.rpg.modelo.partida;

/**
 * Como termino (o no termino) una run.
 *
 * Es un enum y no un booleano porque hay que distinguir POR QUE se acabo:
 * GANADA es haber pasado la etapa 20 y PERDIDA es quedarse sin nadie en pie.
 * Son dos pantallas distintas y llegan por dos caminos distintos: la derrota la
 * informa el ResultadoCombate y la victoria la detecta avanzarEtapa().
 */
public enum EstadoPartida {
    EN_CURSO,
    GANADA,
    PERDIDA
}
