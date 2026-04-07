package com.mycompany.catan;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Juego {

    private MapaCatan mapa;
    private GestorTurnos<Jugador> turnos;
    private Dado dado1;
    private Dado dado2;
    private ArregloDinamico<Jugador> jugadores;
    private Scanner scanner;

    
    private Map<Recurso, Integer> costoAldea() {

        Map<Recurso, Integer> costo = new HashMap<>();

        costo.put(Recurso.MADERA, 1);
        costo.put(Recurso.LADRILLO, 1);
        costo.put(Recurso.TRIGO, 1);
        costo.put(Recurso.OVEJA, 1);

        return costo;
    }
    
    private Map<Recurso, Integer> costoCarretera() {

        Map<Recurso, Integer> costo = new HashMap<>();

        costo.put(Recurso.MADERA, 1);
        costo.put(Recurso.LADRILLO, 1);

        return costo;
    }
    
    private Map<Recurso, Integer> costoCiudad() {

        Map<Recurso, Integer> costo = new HashMap<>();

        costo.put(Recurso.TRIGO, 2);
        costo.put(Recurso.PIEDRA, 3);

        return costo;
    }
        
    public Juego() {

        mapa = new MapaCatan();
        turnos = new GestorTurnos<>();
        jugadores = new ArregloDinamico<Jugador>(10);        
        dado1 = new Dado();
        dado2 = new Dado();
        scanner = new Scanner(System.in);
    }
    
    public Hexagono obtenerTileAleatorio() {

        List<Hexagono> tiles = new ArrayList<>(mapa.getMapa().values());

        Random rand = new Random();

        return tiles.get(rand.nextInt(tiles.size()));
    }
    
    public void agregarJugador(Jugador j) {

        jugadores.append(j);
        turnos.insertarFinal(j);
    }
    
    public void prueba() {

        for (int i = 0; i < jugadores.size(); i++) {

            Jugador j = jugadores.get(i);

            System.out.println(j.getNombre());
        }
    }
    
    public void jugarTurno() {

        Jugador actual = turnos.jugadorActual();

        System.out.println("\nTurno de: " + actual.getNombre());

        System.out.println("Asentamiento construido");

        int resultado = dado1.lanzar() + dado2.lanzar();

        System.out.println("Resultado de dados: " + resultado);

        if (resultado == 7) {

            System.out.println("¡Se activa el ladrón!");

            Hexagono tile = mapa.getMapa().values().iterator().next();

            mapa.moverLadronYRobar(actual, tile);

        } else {
            mapa.producirRecursos(resultado);
        }

        turnos.pasarTurno();
    }
    
    public void construirAldea(Jugador jugador, Vertice v) {

    Map<Recurso, Integer> costo = costoAldea();

        if (!jugador.tieneRecursos(costo)) {
            System.out.println("No tienes recursos para construir una aldea.");
            return;
        }

        if (v.getConstruccion() != null) {
            System.out.println("El vértice ya está ocupado.");
            return;
        }

        v.setConstruccion(new Aldea(jugador));
        jugador.gastarRecursos(costo);

        System.out.println("Aldea construida.");
    }
    
    public void construirCarretera(Jugador jugador, Arista a) {

    Map<Recurso, Integer> costo = costoCarretera();

        if (!jugador.tieneRecursos(costo)) {
            System.out.println("No tienes recursos para construir carretera.");
            return;
        }

        if (a.getConstruccion() != null) {
            System.out.println("La arista ya está ocupada.");
            return;
        }

        a.setConstruccion(new Carretera(jugador));
        jugador.gastarRecursos(costo);

        System.out.println("Carretera construida.");
    }
    
    public void construirCiudad(Jugador jugador, Vertice v) {

        Map<Recurso, Integer> costo = costoCiudad();

        if (!jugador.tieneRecursos(costo)) {
            System.out.println("No tienes recursos para construir ciudad.");
            return;
        }

        if (!(v.getConstruccion() instanceof Aldea)) {
            System.out.println("Debe haber una aldea primero.");
            return;
        }

        if (v.getConstruccion().getPropietario() != jugador) {
            System.out.println("No es tu aldea.");
            return;
        }

        v.setConstruccion(new Ciudad(jugador));
        jugador.gastarRecursos(costo);

        System.out.println("Ciudad construida.");
    }
    
    public void iniciarJuego(int cantidadTurnos) {

        for (int i = 0; i < cantidadTurnos; i++) {
            jugarTurno();
        }
    }

    public void iniciarJuego() {

        while (true) {

            menuTurno();

            if (hayGanador()) {
                break;
            }
        }
    }

    public void menuTurno() {

        boolean turnoActivo = true;

        while (turnoActivo) {

            Jugador actual = turnos.jugadorActual();

            System.out.println("\n--- Turno de: " + actual.getNombre() + " ---");
            System.out.println("1. Lanzar dados");
            System.out.println("2. Construir");
            System.out.println("3. Ver recursos");
            System.out.println("4. Pasar turno");

            int opcion = scanner.nextInt();

            switch (opcion) {

                case 1:
                    lanzarDados(actual);
                    break;

                case 2:
                    menuConstruccion(actual);
                    break;

                case 3:
                    mostrarRecursos(actual);
                    break;

                case 4:
                    turnos.pasarTurno();
                    turnoActivo = false;
                    break;

                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void lanzarDados(Jugador jugador) {

        int resultado = dado1.lanzar() + dado2.lanzar();
    
        System.out.println("Resultado: " + resultado);

        if (resultado == 7) {
            System.out.println("¡Ladrón activado!");
            Hexagono tile = mapa.obtenerTileAleatorio();
            mapa.moverLadronYRobar(jugador, tile);
        } else {
            mapa.producirRecursos(resultado);
        }
    }

    private void mostrarRecursos(Jugador jugador) {
        System.out.println("Recursos de " + jugador.getNombre() + ":");
        System.out.println(jugador.getRecursos());
    }

    private void menuConstruccion(Jugador jugador) {

        System.out.println("\n--- Construcción ---");
        System.out.println("1. Aldea");
        System.out.println("2. Carretera");
        System.out.println("3. Ciudad");

        int opcion = scanner.nextInt();

        switch (opcion) {

            case 1:
                construirAldea(jugador);
                break;

            case 2:
                construirCarretera(jugador);
                break;

            case 3:
                construirCiudadDemo(jugador);
                break;

            default:
                System.out.println("Opción inválida");
        }
    }
    
    private void construirAldea(Jugador jugador) {

        mapa.mostrarVertices();

        System.out.print("Seleccione vértice: ");
        int index = scanner.nextInt();

        Vertice v = mapa.getVertice(index);

        if (v.getConstruccion() != null) {
            System.out.println("Ya está ocupado");
            return;
        }

        if (v.tieneVecinoOcupado()) {
            System.out.println("No puedes construir aquí (regla de distancia)");
            return;
        }

        Aldea aldea = new Aldea(jugador);
        v.setConstruccion(aldea);

        System.out.println("Aldea construida correctamente");
    }

    private void construirCarretera(Jugador jugador) {

        mapa.mostrarAristas();

        System.out.print("Seleccione arista: ");
        int index = scanner.nextInt();

        Arista a = mapa.getArista(index);

        if (a.getConstruccion() != null) {
            System.out.println("Ya está ocupada");
            return;
        }

        if (!a.estaConectadaAJugador(jugador)) {
            System.out.println("Debe estar conectada a tu red");
            return;
        }

        Carretera c = new Carretera(jugador);
        a.setConstruccion(c);

        System.out.println("Carretera construida");
    }

    private void construirCiudadDemo(Jugador jugador) {

        Vertice v = mapa.getVertices().get(0);

        construirCiudad(jugador, v);
    }
    
    public boolean hayGanador() {

        for (Jugador j : turnos.obtenerElementos()) {
            if (j.getPuntosVictoria() >= 10) {
                System.out.println("Ganador: " + j.getNombre());
                return true;
            }
        }

        return false;
    }
}
