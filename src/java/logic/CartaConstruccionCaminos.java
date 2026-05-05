package logic;

public class CartaConstruccionCaminos extends CartaDesarrollo {
    //VERIFICAR CONSTRUIR DOBLE CAMINO
    public CartaConstruccionCaminos() {
        super("Construcción de Caminos", "Construye 2 caminos gratis.");
    }

    @Override
    public void realizarAccion(Jugador jugador, Object contexto) {
        this.setUsada(true);
    }
}
