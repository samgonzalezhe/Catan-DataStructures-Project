package com.mycompany.catan;

public class Aldea extends Construccion {

    public Aldea(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 1;
    }
}