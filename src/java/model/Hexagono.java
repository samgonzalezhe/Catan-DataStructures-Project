package model;

import logic.Recurso;
import logic.TipoTerreno;

import java.util.ArrayList;
import java.util.List;

public class Hexagono {

    private TipoTerreno tipo;
    public int numero;
    boolean tieneLadron;
    public HexCoord coord;
    private List<Vertice> vertices;
    private List<Arista> aristas;

    public Hexagono(TipoTerreno tipo, HexCoord coord) {
        this.tipo = tipo;
        this.coord = coord;
        this.numero = 0;
        this.tieneLadron = false;
        this.vertices = new ArrayList<>();
        this.aristas = new ArrayList<>();
    }

    @Override
    public String toString() {
        return coord + " -> " + tipo +
                (tipo != TipoTerreno.DESIERTO ? " (" + numero + ")" : " [LADRÓN]");
    }

    public boolean tieneLadron() {
        return tieneLadron;
    }

    public void agregarVertice(Vertice v) { this.vertices.add(v); }
    public void agregarArista(Arista a) { this.aristas.add(a); }

    //Getters y setters
    public TipoTerreno getTipo() {
        return tipo;
    }

    public List<Vertice> getVertices() { return vertices; }
    public List<Arista> getAristas() { return aristas; }

    public Recurso getRecurso() {

        switch (tipo) {
            case BOSQUE:
                return Recurso.MADERA;
            case COLINA:
                return Recurso.LADRILLO;
            case CAMPO:
                return Recurso.TRIGO;
            case PASTO:
                return Recurso.OVEJA;
            case MONTAÑA:
                return Recurso.PIEDRA;
            default:
                return null; // desierto
        }
    }

    public void setLadron(boolean valor) {
        this.tieneLadron = valor;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
