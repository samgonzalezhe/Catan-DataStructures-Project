package logic;

import structures.ListaCircular;

public class GestorTurnos {

    private ListaCircular<Jugador> colaTurnos;

    public GestorTurnos() {
        this.colaTurnos = new ListaCircular<>();
    }

    public void registrarJugador(Jugador j) {
        colaTurnos.insertarFinal(j);
    }

    public Jugador obtenerTurnoActual() {
        return colaTurnos.jugadorActual();
    }

    public Jugador pasarTurno() {
        return colaTurnos.pasarTurno();
    }

    public int getRondas() {
        return colaTurnos.getRondas();
    }

    public void verOrdenDeTurnos() {
        colaTurnos.mostrarLista();
    }
}
