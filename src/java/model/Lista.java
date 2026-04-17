package com.mycompany.catan;

public class Lista<T> {

    protected Nodo<T> head;
    protected Nodo<T> last;

    public Lista() {
        head = null;
        last = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void insertarFinal(T dato) {

        Nodo<T> nuevo = new Nodo<>(dato);

        if (isEmpty()) {
            head = nuevo;
            last = nuevo;
        } else {
            last.setSiguiente(nuevo);
            last = nuevo;
        }
    }
}
