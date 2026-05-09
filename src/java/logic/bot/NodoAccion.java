package logic.bot;

public class NodoAccion extends NodoDecision{
    private String descripcion;
    private java.util.function.Consumer<EstadoBot> accion;

    public NodoAccion(String descripcion,
                      java.util.function.Consumer<EstadoBot> accion) {
        super(null, null);
        this.descripcion = descripcion;
        this.accion = accion;
    }

    @Override
    public NodoDecision evaluar(EstadoBot estado) {
        System.out.println("Bot ejecuta: " + descripcion);
        accion.accept(estado);
        return null; // hoja
    }

    public String getDescripcion() { return descripcion; }
}
