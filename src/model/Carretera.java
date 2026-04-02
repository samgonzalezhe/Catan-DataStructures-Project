package com.mycompany.catan;

public class Carretera extends Construccion {

    public Carretera(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 0; 
    }
}
