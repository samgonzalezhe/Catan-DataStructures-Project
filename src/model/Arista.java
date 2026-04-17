package model;

import logic.Jugador;

import java.util.List;

public class Arista {

    private int id;
    private Vertice v1;
    private Vertice v2;
    private Carretera carretera;
    private Construccion construccion;

    public Arista(int id, Vertice v1, Vertice v2) {
        this.id = id;
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean tieneCarretera() {
        return carretera != null;
    }

    public void construirCarretera(Carretera c) {
        if (this.carretera == null) {
            this.carretera = c;
        } else {
            System.out.println("Ya hay una carretera en esta arista");
        }
    }

    public boolean construirCarreteraInicial(Jugador jugador) {

        if (this.carretera != null) {
            return false;
        }
        // Debe estar conectada a la aldea recién puesta
        if (v1.tieneConstruccionDe(jugador) ||
                v2.tieneConstruccionDe(jugador)) {

            this.carretera = new Carretera(jugador);
            return true;
        }
        return false;
    }

    public boolean puedeConstruirCarretera(Jugador jugador, List<Arista> aristas) {

        // 1. Debe estar libre
        if (this.carretera != null) {
            return false;
        }

        // 2. Conectada a construcción del jugador
        if (v1.tieneConstruccionDe(jugador) || v2.tieneConstruccionDe(jugador)) {
            return true;
        }

        // 3. Conectada a otra carretera del jugador
        for (Arista a : aristas) {

            if (a.carretera != null &&
                    a.carretera.getPropietario() == jugador) {

                // Si comparte vértice con esta arista
                if (a.v1 == v1 || a.v1 == v2 ||
                        a.v2 == v1 || a.v2 == v2) {

                    return true;
                }
            }
        }

        return false;
    }


    //Getters y setters

    public void setConstruccion(Construccion construccion) {
        this.construccion = construccion;
    }

    public int getId() {
        return id;
    }

    public Vertice getV1() {
        return v1;
    }

    public Vertice getV2() {
        return v2;
    }

    public Construccion getConstruccion() {
        return construccion;
    }

    public Carretera getCarretera() {return carretera; }
}

