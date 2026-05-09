package model;

import logic.Jugador;
import logic.Recurso;

import java.util.Map;

public class Carretera extends Construccion {

    public Carretera(Jugador propietario) {
        super(propietario);
    }

    public static final Map<Recurso, Integer> COSTO = Map.of(
            Recurso.MADERA, 1,
            Recurso.LADRILLO, 1
    );

    @Override
    public int getPuntosVictoria() {
        return 0;
    }
}
