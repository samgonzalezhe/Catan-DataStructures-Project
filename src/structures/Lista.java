package structures;

public class Lista <T> {
    protected Nodo<T> head;
    protected Nodo<T> last;
    protected int length;

    public Lista() {
        this.head = null;
        this.last = null;
        this.length = 0;
    }

    public void insertarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (isEmpty()){
            head = nuevo;
            last = nuevo;
        } else {
            nuevo.setSiguiente(head);
            head = nuevo;
        }
        length++;
    }

    public void insertarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (isEmpty()){
            head = nuevo;
            last = nuevo;
        } else {
            last.setSiguiente(nuevo);
            last = nuevo;
        }
        length++;
    }

    public void borrarInicio(){
        if(!isEmpty()) {
            Nodo<T> aux = head;
            head = head.getSiguiente();
            aux.setSiguiente(null);
            length--;
            if (isEmpty()) {
                last = null;
            }
        }
    }

    public boolean isEmpty () {
        return head == null;
    }

    public int getLength() {
        return length;
    }

    public void mostrarLista(){
        System.out.println("Longitud: " + length);
        if (isEmpty()){
            System.out.println("Lista vacia");
        }
        Nodo<T> aux = head;
        while (aux != last){
            System.out.print(aux.getDato() + ", ");
            aux = aux.getSiguiente();
        }
        System.out.print(aux.getDato());
        System.out.println();
    }
}
