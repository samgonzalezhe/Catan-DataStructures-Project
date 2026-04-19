package structures;

public class ListaCircular<T> extends Lista<T>{
    private Nodo<T> actual;
    private int rondas;

    public ListaCircular(){
        super();
        this.actual = null;
        this.rondas = 0;
    }

    @Override
    public void insertarFinal(T dato){
        super.insertarFinal(dato);
        last.setSiguiente(head);
        if (actual == null) {
            actual = head;
        }
    }

    public T pasarTurno(){
        if(isEmpty()) return null;
        if(actual == last){
            rondas++;
        }
        actual = actual.getSiguiente();
        return actual.getDato();
    }

    public T jugadorActual(){
        if (actual == null) return null;
        return actual.getDato();
    }

    public T Reverse() {
        if (isEmpty()) return null;

        Nodo<T> anterior = head;
        while (anterior.getSiguiente() != actual) {
            anterior = anterior.getSiguiente();
        }
        actual = anterior;
        return actual.getDato();
    }

    public boolean esUltimo() {
        return actual == last;
    }

    public boolean esPrimero() {
        return actual == head;
    }

    public int getRondas() {
        return rondas;
    }
}
