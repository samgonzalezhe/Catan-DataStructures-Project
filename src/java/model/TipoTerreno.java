package com.mycompany.catan;

public enum TipoTerreno {
    BOSQUE("Madera"),
    COLINA("Ladrillo"),
    CAMPO("Trigo"),
    PASTO("Oveja"),
    MONTAÑA("Piedra"),
    DESIERTO("Ninguno");

    private String recurso;

    TipoTerreno(String recurso) {
        this.recurso = recurso;
    }

    public String getRecurso() {
        return recurso;
    }
}
