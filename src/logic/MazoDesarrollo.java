package logic;

import model.Hexagono;
import model.Ladron;
import structures.ArregloDinamico;
import structures.Pila;
import java.util.Random;

public class MazoDesarrollo {

    private Pila<CartaDesarrollo> cartas;

    public MazoDesarrollo() {
        this.cartas = new Pila<>();
        inicializarMazo();
    }

    private void inicializarMazo() {
        ArregloDinamico<CartaDesarrollo> temporal = new ArregloDinamico<>(25);

        //Agregar las cartas según las reglas de Catan
        for(int i=0; i<14; i++) temporal.append(new CartaCaballero());
        for(int i=0; i<5; i++)  temporal.append(new CartaPuntoVictoria());
        for(int i=0; i<2; i++)  temporal.append(new CartaMonopolio());

        barajar(temporal);

        //Pasar del Arreglo a la Pila
        for(int i=0; i < temporal.len(); i++) {
            cartas.push(temporal.get(i));
        }
    }

    public CartaDesarrollo robarCarta() {
        if (cartas.isEmpty()) return null;
        return cartas.extraerPrimero();
    }

    private void barajar(ArregloDinamico<CartaDesarrollo> v) {
        Random rand = new Random();
        int n = v.len();

        // Algoritmo Fisher-Yates
        for (int i = n - 1; i > 0; i--) {
            // Elegimos un índice aleatorio entre 0 e i
            int j = rand.nextInt(i + 1);

            // Intercambiamos los elementos en las posiciones i y j
            CartaDesarrollo tempI = v.get(i);
            CartaDesarrollo tempJ = v.get(j);

            v.insert(i, tempJ);
            v.insert(j, tempI);
        }
    }
}
