package logic;

import structures.ArregloDinamico;

public class CartaMonopolio extends CartaDesarrollo{
    public CartaMonopolio() {
        super("Monopolio", "Elige un recurso; todos los jugadores deben darte todas sus cartas de ese tipo.");
    }


    public void realizarAccion(Jugador jugadorActivo, Recurso recursoElegido, ArregloDinamico<Jugador> todosLosJugadores) {
        int totalRobado = 0;
        for (int i = 0; i < todosLosJugadores.len(); i++) {
            Jugador oponente = todosLosJugadores.get(i);
            if (oponente != jugadorActivo) {
                int cantidadOponente = oponente.getRecursos().getOrDefault(recursoElegido, 0);
                oponente.agregarRecurso(recursoElegido, -cantidadOponente);
                totalRobado += cantidadOponente;
            }
        }
        jugadorActivo.agregarRecurso(recursoElegido, totalRobado);
        System.out.println("¡MONOPOLIO! " + jugadorActivo.getNombre() + " ha obtenido " + totalRobado + " de " + recursoElegido);
        this.setUsada(true);
    }

    @Override
    public void realizarAccion(Jugador jugador, Object contexto) {

    }
}
