package model;

import java.util.Map;

public class Carretera extends Construccion {

    public static final Map<Recurso, Integer> COSTO = Map.of(
            Recurso.MADERA, 1,
            Recurso.LADRILLO, 1
    );

    public Carretera(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 0; 
    }
}
