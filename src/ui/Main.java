package ui;

import logic.*;

public class    Main {
    public static void main(String[] args) {

        Jugador j1 = new Jugador("Juan");
        Jugador j2 = new Jugador("Ana");

        Jugador[] jugadores = {j1, j2};

        GestorJuego juego = new GestorJuego(jugadores);

        for (int i = 0; i < 6; i++) {
            juego.jugarTurno();
        }
    }
}
