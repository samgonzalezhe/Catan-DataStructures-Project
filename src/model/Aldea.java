package com.mycompany.catan;

public class Aldea extends Construccion {

    public static final Map<Recurso, Integer> COSTO = Map.of(
            Recurso.MADERA, 1,
            Recurso.LADRILLO, 1,
            Recurso.TRIGO, 1,
            Recurso.OVEJA, 1
    );
    
    public Aldea(Jugador propietario) {
        super(propietario);
    }

    @Override
    public int getPuntosVictoria() {
        return 1;
    }
}
