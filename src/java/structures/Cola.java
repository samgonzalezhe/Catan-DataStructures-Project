package structures;

public class Cola<T> extends Lista<T>{

    public Cola(){
        super();
    }

    public void append(T dato){
        insertarFinal(dato);
    }

    public T removeFirst(){
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
