package com.mycompany.catan;

public abstract class Construccion {

    protected Jugador propietario;

    public Construccion(Jugador propietario) {
        this.propietario = propietario;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public abstract int getPuntosVictoria();

    public Jugador getJugador() {
        return propietario;
    }

}
