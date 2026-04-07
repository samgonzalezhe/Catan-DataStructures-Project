package com.mycompany.catan;

import java.util.ArrayList;
import java.util.List;

public class Vertice {
    
    private List<Vertice> vecinos = new ArrayList<>();

    private int id;
    private Construccion construccion;
    private List<Hexagono> tilesAdyacentes;

    public Vertice(int id) {
        this.id = id;
        this.tilesAdyacentes = new ArrayList<>();
        this.vecinos = new ArrayList<>();

    }

    public void agregarTile(Hexagono tile) {
        tilesAdyacentes.add(tile);
    }

    public List<Hexagono> getTilesAdyacentes() {
        return tilesAdyacentes;
    }

    public int getId() {
        return id;
    }
    
    public Construccion getConstruccion() {
        return construccion;
    }
    
    public void agregarVecino(Vertice v) {
    if (!vecinos.contains(v)) {
        vecinos.add(v);
    }
}

    public List<Vertice> getVecinos() {
        return vecinos;
    } 
    
    public boolean puedeConstruirAldea() {
    // 1. Debe estar vacío
    if (this.construccion != null) {
        return false;
    }
    // 2. Ningún vecino puede tener construcción
    for (Vertice vecino : vecinos) {
        if (vecino.getConstruccion() != null) {
            return false;
        }
    }
    return true;
    }
    
    public boolean construirAldea(Jugador jugador) {

    if (puedeConstruirAldea()) {
        this.construccion = new Aldea(jugador);
        return true;
    }
    return false;
    }
    
    public boolean mejorarACiudad(Jugador jugador) {

    if (this.construccion instanceof Aldea &&
        this.construccion.getPropietario() == jugador) {

        this.construccion = new Ciudad(jugador);
        return true;
    }
    return false;
    }
    
    public boolean tieneConstruccionDe(Jugador jugador) {
        return construccion != null &&
               construccion.getPropietario() == jugador;
    }
    
    public boolean construirAldeaInicial(Jugador jugador) {
    // Solo validar que esté libre y distancia
        if (this.construccion != null) {
            return false;
        }
        for (Vertice vecino : vecinos) {
            if (vecino.getConstruccion() != null) {
                return false;
            }
        }
        this.construccion = new Aldea(jugador);
        return true;
    }

    public boolean tieneVecinoOcupado() {

        for (Vertice v : vecinos) {
            if (v.getConstruccion() != null) {
                return true;
            }
        }

        return false;
    }
    
    public void setConstruccion(Construccion construccion) {
        this.construccion = construccion;
    }
}
