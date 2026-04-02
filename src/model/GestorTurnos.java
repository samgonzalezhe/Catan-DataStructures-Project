package com.mycompany.catan;

import java.util.ArrayList;
import java.util.List;

public class GestorTurnos<T> extends Lista<T> {

    private Nodo<T> actual;
    private int rondas;

    public GestorTurnos() {
        super();
        this.actual = null;
        this.rondas = 0;
    }
    
    public List<T> obtenerElementos() {

        List<T> lista = new ArrayList<>();

        if (isEmpty()) return lista;

        Nodo<T> temp = head;

        do {
            lista.add(temp.getDato());
            temp = temp.getSiguiente();
        } while (temp != head);

        return lista;
    }

    @Override
    public void insertarFinal(T dato) {

        super.insertarFinal(dato);

        if (last != null) {
            last.setSiguiente(head);
        }

        if (actual == null) {
            actual = head;
        }
    }

    public void pasarTurno() {

        if (isEmpty()) return;

        if (actual == last) {
            rondas++;
        }

        actual = actual.getSiguiente();
    }

    public T jugadorActual() {

        if (actual == null) return null;

        return actual.getDato();
    }

    public int getRondas() {
        return rondas;
    }
}