package logic;

public class GestorJuego {

    private Jugador[] jugadores;
    private int turnoActual;

    public GestorJuego(Jugador[] jugadores) {
        this.jugadores = jugadores;
        this.turnoActual = 0;
    }

    public Jugador getJugadorActual() {
        return jugadores[turnoActual];
    }

    public void siguienteTurno() {
        turnoActual = (turnoActual + 1) % jugadores.length;
    }

    public void jugarTurno() {
        Jugador actual = getJugadorActual();
        System.out.println("\nTurno de: " + actual.getNombre());

        // Tirar dado
        int dado = (int)(Math.random() * 6) + 1;
        System.out.println("Sacó: " + dado);

        // Simular que recibe recursos
        actual.modificarRecurso(dado % 5, 1);

        actual.mostrarInventario();
        System.out.println("Puntos de " + actual.getNombre() + ": " + actual.getPuntos());

        // Intentar construir
        System.out.println("Intentando construir...");
        intentarConstruir(actual);

        siguienteTurno();
    }

    public void intentarConstruir(Jugador jugador) {

        int[] costo = {1, 1, 0, 0, 0}; // madera + ladrillo

        if (jugador.tieneRecursos(costo)) {
            for (int i = 0; i < 5; i++) {
                jugador.modificarRecurso(i, -costo[i]);
            }
            jugador.sumarPuntos(1);
            System.out.println(jugador.getNombre() + " construyó y ganó 1 punto!");
        } else {
            System.out.println("No tiene recursos para construir");
        }
    }
}
