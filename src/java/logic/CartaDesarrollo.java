package logic;

public abstract class CartaDesarrollo {
    private String nombre;
    private String descripcion;
    private boolean usada;

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
