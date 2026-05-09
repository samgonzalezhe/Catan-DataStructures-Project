package logic.bot;

//Nodo interno, tiene hijos
public class NodoCondicion extends NodoDecision {
    private String descripcion;
    private java.util.function.Predicate<EstadoBot> condicion;

    public NodoCondicion(String descripcion, java.util.function.Predicate<EstadoBot>
                                 condicion, NodoDecision hijoSi, NodoDecision hijoNo) {
        super(hijoSi, hijoNo);
        this.descripcion = descripcion;
        this.condicion = condicion;
    }

    @Override
    public NodoDecision evaluar(EstadoBot estado) {
        System.out.println("Bot evaluando: " + descripcion);
        return condicion.test(estado) ? hijoSi : hijoNo;
    }

    public String getDescripcion() { return descripcion; }
}
