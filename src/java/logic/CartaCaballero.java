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
        this.setUsada(true);
    }
}
