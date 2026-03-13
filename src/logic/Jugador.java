package logic;

public class Jugador {
    private String nombre;
    private int[] recursos; // [0]:Madera, [1]:Ladrillo, [2]:Lana, [3]:Trigo, [4]:Piedra
    private int puntosVictoria;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.recursos = new int[5]; // Inicializa todos los recursos en 0
        this.puntosVictoria = 0;
    }

    // Método para sumar o restar recursos
    public void modificarRecurso(int tipo, int cantidad) {
        if (tipo >= 0 && tipo < 5) {
            recursos[tipo] += cantidad;
        }
    }

    // Método para verificar si el jugador tiene suficientes recursos
    public boolean tieneRecursos(int[] costo) {
        for (int i = 0; i < 5; i++) {
            if (this.recursos[i] < costo[i]) return false;
        }
        return true;
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getPuntos() { return puntosVictoria; }

    public void mostrarInventario() {
        String[] nombres = {"Madera", "Ladrillo", "Lana", "Trigo", "Piedra"};
        System.out.print("Inventario de " + nombre + ": ");
        for (int i = 0; i < 5; i++) {
            System.out.print(nombres[i] + ": " + recursos[i] + " | ");
        }
        System.out.println();
    }
}