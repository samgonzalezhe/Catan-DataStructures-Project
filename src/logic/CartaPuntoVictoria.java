package logic;

public class CartaPuntoVictoria extends CartaDesarrollo{
    public CartaPuntoVictoria() {
        super("Punto de Victoria", "Te otorga 1 punto invisible hacia el final del juego.");
    }

    @Override
    public void realizarAccion(Jugador jugador, Object contexto) {
        jugador.agregarPuntos(1);
        this.setUsada(true);
    }
}
