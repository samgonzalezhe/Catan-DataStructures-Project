package model;

import logic.Jugador;

import java.util.ArrayList;
import java.util.List;

public class Vertice {
    //Aristas adyacentes
    private List<Vertice> vecinos = new ArrayList<>();
    private int id;
    private Construccion construccion;
    private List<Hexagono> tilesAdyacentes;
    private double pixelX;
    private double pixelY;

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

    public void agregarVecino(Vertice v) {
        if (!vecinos.contains(v)) {
            vecinos.add(v);
        }
    }

    public boolean puedeConstruirAldea(Jugador jugador) {
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
        return jugador.tieneRecursos(Aldea.COSTO);
    }

    public boolean construirAldea(Jugador jugador) {

        if (puedeConstruirAldea(jugador)) {
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

    public double getX() { return pixelX; }
    public double getY() { return pixelY; }

    public boolean construirAldeaDirecto(Jugador jugador) {
        if (this.construccion != null) return false;
        this.construccion = new Aldea(jugador);
        return true;
    }

    //Getters y setters

    public void setConstruccion(Construccion construccion) {
        this.construccion = construccion;
    }

    public void setPosicionPixeles(double x, double y) {
        this.pixelX = x;
        this.pixelY = y;
    }

    public int getId() {
        return id;
    }

    public Construccion getConstruccion() {
        return construccion;
    }

    public List<Vertice> getVecinos() {
        return vecinos;
    }
}
