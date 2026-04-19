package model;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Jugador {

    private String nombre;
    private Map<Recurso, Integer> recursos;
    private int puntosVictoria;
    private String color;

    public Jugador(String nombre) {
        this.nombre = nombre;
        recursos = new HashMap<>();

        // Inicializar todos en 0
        for (Recurso r : Recurso.values()) {
            recursos.put(r, 0);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void agregarRecurso(Recurso r, int cantidad) {

        int actual = recursos.getOrDefault(r, 0);
        recursos.put(r, actual + cantidad);
    }

    public void imprimirRecursos() {
        for (Recurso r : recursos.keySet()) {
            System.out.println(r + ": " + recursos.get(r));
        }
    }

    public boolean tieneRecursos(Map<Recurso, Integer> costo) {

        for (Recurso r : costo.keySet()) {

            int disponible = recursos.getOrDefault(r, 0);

            if (disponible < costo.get(r)) {
                return false;
            }
        }

        return true;
    }

    public void gastarRecursos(Map<Recurso, Integer> costo) {

        for (Recurso r : costo.keySet()) {

            int actual = recursos.get(r);

            recursos.put(r, actual - costo.get(r));
        }
    }

    public Recurso quitarRecursoAleatorio() {

        List<Recurso> disponibles = new ArrayList<>();

        for (Recurso r : recursos.keySet()) {
            if (recursos.get(r) > 0) {
                disponibles.add(r);
            }
        }

        if (disponibles.isEmpty()) {
            return null;
        }

        Random rand = new Random();
        Recurso elegido = disponibles.get(rand.nextInt(disponibles.size()));

        recursos.put(elegido, recursos.get(elegido) - 1);

        return elegido;
    }

    public void agregarPuntos(int puntos) {
        puntosVictoria += puntos;
    }

    public int getPuntosVictoria() {
        return puntosVictoria;
    }

    public Map<Recurso, Integer> getRecursos() {
        if (recursos == null) {
            recursos = new HashMap<>();
            for (Recurso r : Recurso.values()) {
                recursos.put(r, 0);
            }
        }
        return recursos;
    }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return nombre;
    }
}
