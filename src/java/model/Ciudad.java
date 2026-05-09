package model;

import logic.Jugador;
import logic.Recurso;

import java.util.Map;

public class Ciudad extends Construccion {

    public static final Map<Recurso, Integer> COSTO = Map.of(
            Recurso.TRIGO, 2,
            Recurso.PIEDRA, 3
    );

    public Ciudad(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 2;
    }
}
