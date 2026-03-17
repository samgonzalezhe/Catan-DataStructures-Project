package structures;

public class Pila<T> extends Lista<T> {

    public Pila() {
        super();
    }

    public void push(T dato){
        insertarInicio(dato);
    }

    public T extraerPrimero(){
        if(isEmpty()) return null;
        T dato = head.getDato();
        borrarInicio();
        return dato;
    }

    public T verPrimero() {
        if (isEmpty()) return null;
        else return head.getDato();
    }
}
