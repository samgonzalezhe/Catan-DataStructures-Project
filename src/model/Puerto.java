package com.mycompany.catan;

public class Puerto {

    private TipoTerreno tipoEspecial; 
    private int tasaIntercambio; 

    public Puerto(int tasaIntercambio) {
        this.tasaIntercambio = tasaIntercambio;
    }

    public int getTasaIntercambio() {
        return tasaIntercambio;
    }
}
