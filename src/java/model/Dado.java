package com.mycompany.catan;

import java.util.Random;

public class Dado {

    private Random random;

    public Dado() {
        random = new Random();
    }

    public int lanzar() {
        return random.nextInt(6) + 1;
    }
}
