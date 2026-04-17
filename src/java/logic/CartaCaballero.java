package logic;

import model.Hexagono;
import model.Ladron;

public class CartaCaballero extends CartaDesarrollo {

    private Ladron ladron;

    public CartaCaballero() {
        super("Caballero", "Mueve al ladrón y roba una carta a un oponente.");
    }

    @Override
    public void realizarAccion(Jugador jugador, Object destino) {
        if (destino instanceof Hexagono) {
                Hexagono nuevoTile = (Hexagono) destino;
                ladron.mover(nuevoTile);
                System.out.println("¡Caballero usado! Ladrón movido al hexágono " + ladron.getPosicion());
                this.setUsada(true);
        }
    }
}
