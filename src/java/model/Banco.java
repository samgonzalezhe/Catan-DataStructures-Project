package com.mycompany.catan;

import java.util.HashMap;
import java.util.Map;

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

    public void imprimirRecursos() {
        System.out.println("Banco: " + recursos);
    }
}
