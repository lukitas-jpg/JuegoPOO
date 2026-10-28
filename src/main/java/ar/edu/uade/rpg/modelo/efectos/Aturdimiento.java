package ar.edu.uade.rpg.modelo.efectos;

import ar.edu.uade.rpg.modelo.combatientes.Combatiente;

/**
 * Le hace perder el turno al afectado. Es el unico efecto que sobrescribe
 * impideActuar(), y Combatiente lo consulta por ese metodo en vez de buscar un
 * efecto llamado "Aturdimiento": el dia que haya otro que paralice, alcanza
 * con que devuelva true.
 *
 * Es el efecto mas delicado del juego para el balance, porque Andanada
 * Cegadora lo tira sobre todos los enemigos a la vez.
 */
public class Aturdimiento extends EfectoEstado {

    public Aturdimiento(int duracionEnTurnos) {
        super("Aturdimiento", duracionEnTurnos);
    }

    @Override
    public void alAplicarse(Combatiente objetivo) {
        // Alcanza con estar en la lista de efectos activos.
    }

    @Override
    public void alTerminarTurno(Combatiente objetivo) {
        // No hace daño: su efecto es que el turno no se juega.
    }

    @Override
    public void alExpirar(Combatiente objetivo) {
        // El afectado vuelve a actuar sin mas.
    }

    @Override
    public boolean impideActuar() {
        return true;
    }

    @Override
    public boolean persisteFueraDeCombate() {
        return false;
    }

    @Override
    public EfectoEstado copiar() {
        return new Aturdimiento(duracionEnTurnos);
    }
}
