package model;

import logic.Jugador;

public abstract class Construccion {

    protected Jugador propietario;

    public Construccion(Jugador propietario) {
        this.propietario = propietario;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public abstract int getPuntosVictoria();
}
