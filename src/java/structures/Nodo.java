package structures;

public class Nodo <T>{
    private T dato;     //Tipo de dato generico
    private Nodo<T> siguiente;  //Apuntador al siguiente

    //Constructor: Solo se conoce el nodo del dato, no se sabe a cual apunta despues
    public Nodo(T dato){
        this.dato = dato;
        this.siguiente = null;
    }

    //Getters y Setters
    public T getDato() {
        return dato;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
