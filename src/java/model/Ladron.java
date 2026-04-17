package com.mycompany.catan;

public class Ladron {

    private Hexagono posicion;

    public Ladron(Hexagono posicionInicial) {
        this.posicion = posicionInicial;
    }

    public Hexagono getPosicion() {
        return posicion;
    }

    public void mover(Hexagono nuevoTile) {
        this.posicion = nuevoTile;
    }
}
