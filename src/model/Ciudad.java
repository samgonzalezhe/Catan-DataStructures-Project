package com.mycompany.catan;

public class Ciudad extends Construccion {

    public Ciudad(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 2;
    }
}
