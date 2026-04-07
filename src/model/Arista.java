package com.mycompany.catan;

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
    
    public boolean puedeConstruirCarretera(Jugador jugador, List<Arista> aristas) {

        if (this.carretera != null) {
            return false;
        }
        
        if (v1.tieneConstruccionDe(jugador) || v2.tieneConstruccionDe(jugador)) {
            return true;
        }

        for (Arista a : aristas) {

            if (a.carretera != null &&
                a.carretera.getPropietario() == jugador) {

                if (a.v1 == v1 || a.v1 == v2 ||
                    a.v2 == v1 || a.v2 == v2) {

                    return true;
                }
            }
        }

        return false;
    }
    
    public boolean construirCarretera(Jugador jugador, List<Arista> aristas) {

        if (puedeConstruirCarretera(jugador, aristas)) {
            this.carretera = new Carretera(jugador);
            return true;
        }

        return false;
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

    public boolean estaConectadaAJugador(Jugador jugador) {

        if (v1.getConstruccion() != null &&
            v1.getConstruccion().getPropietario() == jugador) {
            return true;
        }

        if (v2.getConstruccion() != null &&
            v2.getConstruccion().getPropietario() == jugador) {
            return true;
        }

        return false;
    }
    
    public void setConstruccion(Construccion construccion) {
        this.construccion = construccion;
    }
}
