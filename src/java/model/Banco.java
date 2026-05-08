package com.mycompany.catan;

import java.util.HashMap;
import java.util.Map;
import logic.Jugador;

public class Banco {

    private Map<Recurso, Integer> recursos;

    public Banco() {
        recursos = new HashMap<>();
        
        for (Recurso r : Recurso.values()) {
            recursos.put(r, 19);
        }
    }

    public boolean tieneRecursos(Recurso r, int cantidad) {
        return recursos.getOrDefault(r, 0) >= cantidad;
    }

    public void entregarRecurso(Recurso r, int cantidad) {
        recursos.put(r, recursos.get(r) - cantidad);
    }

    public void recibirRecurso(Recurso r, int cantidad) {
        recursos.put(r, recursos.get(r) + cantidad);
    }

    public boolean intercambiar4x1(Jugador jugador, Recurso entrega, Recurso recibe) {

        // Verificar que el jugador tenga al menos 4 recursos
        if (jugador.getRecursos().getOrDefault(entrega, 0) < 4) {
            return false;
        }

        // Verificar que el banco tenga el recurso solicitado
        if (!tieneRecursos(recibe, 1)) {
            return false;
        }

        // El jugador entrega 4 recursos al banco
        jugador.getRecursos().put(entrega,
                jugador.getRecursos().get(entrega) - 4);

        recibirRecurso(entrega, 4);

        // El banco entrega 1 recurso al jugador
        entregarRecurso(recibe, 1);

        jugador.agregarRecurso(recibe, 1);

        return true;

    public void imprimirRecursos() {
        System.out.println("Banco: " + recursos);
    }
}
