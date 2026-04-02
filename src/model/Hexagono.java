package com.mycompany.catan;

public class Hexagono {
    
    private TipoTerreno tipo;

        public TipoTerreno getTipo() {
            return tipo;
        }
        
        
    public Recurso getRecurso() {

        switch (tipo) {
            case BOSQUE:
                return Recurso.MADERA;
            case COLINA:
                return Recurso.LADRILLO;
            case CAMPO:
                return Recurso.TRIGO;
            case PASTO:
                return Recurso.OVEJA;
            case MONTAÑA:
                return Recurso.PIEDRA;
            default:
                return null; // desierto
        }
    }
    int numero; // 2–12 (excepto desierto)
    boolean tieneLadron;
    HexCoord coord;

    public Hexagono(TipoTerreno tipo, HexCoord coord) {
        this.tipo = tipo;
        this.coord = coord;
        this.numero = 0;
        this.tieneLadron = false;
    }

    @Override
    public String toString() {
        return coord + " -> " + tipo +
                (tipo != TipoTerreno.DESIERTO ? " (" + numero + ")" : " [LADRÓN]");
    }
    

    public boolean tieneLadron() {
        return tieneLadron;
    }

    public void setLadron(boolean valor) {
        this.tieneLadron = valor;
    }
    
}