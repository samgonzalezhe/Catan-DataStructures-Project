package logic;

import java.util.Map;

public abstract class CartaDesarrollo {
    private String nombre;
    private String descripcion;
    private boolean usada;

    //Costo cartas
    public static final Map<Recurso, Integer> COSTO = Map.of(
            Recurso.TRIGO, 1,
            Recurso.OVEJA, 1,
            Recurso.PIEDRA, 1
    );

    public CartaDesarrollo(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usada = false;
    }

    public abstract void realizarAccion(Jugador jugador, Object contexto);

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isUsada() { return usada; }
    public void setUsada(boolean usada) { this.usada = usada; }

    @Override
    public String toString() {
        return "[" + nombre + "]: " + descripcion;
    }
}

