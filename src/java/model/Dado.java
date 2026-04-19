package logic;

import java.util.Random;

public class Dado {
    private Random random;
    private int dado1;
    private int dado2;

    public Dado(){
        random = new Random();
    }

    public int lanzar(){
        dado1 = random.nextInt(6) + 1;
        dado2 = random.nextInt(6) + 1;
        return dado1 + dado2;
    }

    public int getDado1() { return dado1; }
    public int getDado2() { return dado2; }
}
