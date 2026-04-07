package com.mycompany.catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;

public class MapaCatan {  //Atributos
    
    private Map<HexCoord, Hexagono> mapa = new HashMap<>();
    // Cola para mezclar terrenos
    private Queue<TipoTerreno> bolsaTerrenos = new LinkedList<>();
    // Cola para números
    private Queue<Integer> bolsaNumeros = new LinkedList<>();
    private List<Vertice> vertices = new ArrayList<>();
    private List<Arista> aristas = new ArrayList<>();
    private Map<VertexKey, Vertice> mapaVertices = new HashMap<>();
    
    private Ladron ladron;
    private ArregloDinamico<Jugador> afectados = new ArregloDinamico<>(5);    
    
    public MapaCatan() {   //Constructor
        inicializarTerrenos();
        inicializarNumeros();
        generarMapaHexagonal();        
        generarVerticesReales();
        generarAristasReales();           
        inicializarLadron();        
    }

    public Hexagono obtenerTileAleatorio() {

        List<Hexagono> tiles = new ArrayList<>(mapa.values());

        Random rand = new Random();

        return tiles.get(rand.nextInt(tiles.size()));
    }
            //Metodos
    private void inicializarTerrenos() {
        agregarTerrenos(TipoTerreno.BOSQUE, 4);
        agregarTerrenos(TipoTerreno.COLINA, 3);
        agregarTerrenos(TipoTerreno.CAMPO, 4);
        agregarTerrenos(TipoTerreno.PASTO, 4);
        agregarTerrenos(TipoTerreno.MONTAÑA, 3);
        agregarTerrenos(TipoTerreno.DESIERTO, 1);

        mezclarCola((LinkedList<TipoTerreno>) bolsaTerrenos);
    }

    private void agregarTerrenos(TipoTerreno tipo, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            bolsaTerrenos.add(tipo);
        }
    }
    
    private void inicializarNumeros() {
        int[] numeros = {
            2, 3, 3, 4, 4, 5, 5,
            6, 6, 8, 8,
            9, 9, 10, 10, 11, 11, 12
        };

        for (int n : numeros) {
            bolsaNumeros.add(n);
        }

        mezclarCola((LinkedList<Integer>) bolsaNumeros);
    }
    
    private <T> void mezclarCola(LinkedList<T> lista) {
        Collections.shuffle(lista);
    }
        
    private List<HexCoord> generarCoordenadasHexagonales() {
        List<HexCoord> coords = new ArrayList<>();

        int radio = 2;

        for (int q = -radio; q <= radio; q++) {
            int r1 = Math.max(-radio, -q - radio);
            int r2 = Math.min(radio, -q + radio);

            for (int r = r1; r <= r2; r++) {
                coords.add(new HexCoord(q, r));
            }
        }

        return coords;
    }
        
    private void generarMapaHexagonal() {
        List<HexCoord> coords = generarCoordenadasHexagonales();

        for (HexCoord coord : coords) {
            TipoTerreno tipo = bolsaTerrenos.poll();
            Hexagono tt = new Hexagono(tipo, coord);

            if (tipo == TipoTerreno.DESIERTO) {
                tt.tieneLadron = true;
            } else {
                tt.numero = bolsaNumeros.poll();
            }

            mapa.put(coord, tt);
        }
    }
    
    private void generarVerticesReales() {
    int id = 0;

    for (Hexagono tile : mapa.values()) {

        int q = tile.coord.q;
        int r = tile.coord.r;

        // 6 posiciones alrededor del hexágono
        int[][] offsets = {
            {q, r}, {q+1, r}, {q, r+1},
            {q-1, r+1}, {q-1, r}, {q, r-1}
        };

        for (int[] pos : offsets) {
            VertexKey key = new VertexKey(pos[0], pos[1]);

            Vertice v = mapaVertices.get(key);

            if (v == null) {
                v = new Vertice(id++);
                mapaVertices.put(key, v);
                vertices.add(v);
            }

            v.agregarTile(tile);
        }
    }
}
    
    private void generarAristasReales() {
    int id = 0;

    List<Vertice> lista = new ArrayList<>(mapaVertices.values());

    for (Vertice v1 : lista) {
        for (Vertice v2 : lista) {

            if (v1 == v2) continue;

            // Si comparten EXACTAMENTE 2 tiles → son vecinos reales
            int comunes = 0;

            for (Hexagono t : v1.getTilesAdyacentes()) {
                if (v2.getTilesAdyacentes().contains(t)) {
                    comunes++;
                }
            }

            if (comunes == 2) {
                v1.agregarVecino(v2);

                // Evitar duplicados
                boolean existe = false;

                for (Arista a : aristas) {
                    if ((a.getV1() == v1 && a.getV2() == v2) ||
                        (a.getV1() == v2 && a.getV2() == v1)) {
                        existe = true;
                        break;
                    }
                }

                if (!existe) {
                    aristas.add(new Arista(id++, v1, v2));
                }
            }
        }
    }
}
    
    public void imprimirMapa() {
        System.out.println("MAPA HEXAGONAL DE CATAN:\n");

        for (Hexagono tt : mapa.values()) {
            System.out.println(tt);
        }
    }
    
    public void imprimirVertices() {
        for (Vertice v : vertices) {
            System.out.println("Vertice " + v.getId() +
                " vecinos: " + v.getVecinos().size());
    }
}

    public void imprimirAristas() {
        for (Arista a : aristas) {
            System.out.println("Arista " + a.getId() +
                ": V" + a.getV1().getId() +
                " - V" + a.getV2().getId());
        }
    }
    
    public Map<HexCoord, Hexagono> getMapa() {
        return mapa;
    }
    
    public List<Arista> getAristas() {
        return aristas;
    }
    
    public List<Vertice> getVertices() {
        return vertices;
    }
    
    public Vertice getVertice(int index) {
        return vertices.get(index);
    }

    public Arista getArista(int index) {
        return aristas.get(index);
    }
    
    private void inicializarLadron() {

        for (Hexagono t : mapa.values()) {
            if (t.getTipo() == TipoTerreno.DESIERTO) {
                ladron = new Ladron(t);
                break;
            }
        }
    }
        
    public void moverLadron(Hexagono nuevoTile) {
        ladron.mover(nuevoTile);
    }
    
    public void producirRecursos(int numero) {

        for (Hexagono tile : mapa.values()) {

            if (tile.numero == numero && tile != ladron.getPosicion()) {
                for (Vertice v : vertices) {

                    if (v.getTilesAdyacentes().contains(tile)) {

                        Construccion c = v.getConstruccion();

                        if (c != null) {

                            Jugador jugador = c.getPropietario();

                            int cantidad = 0;

                            if (c instanceof Aldea) {
                                cantidad = 1;
                            } else if (c instanceof Ciudad) {
                                cantidad = 2;
                            }

                            Recurso recurso = tile.getRecurso();

                            if (recurso != null) {
                                jugador.agregarRecurso(recurso, cantidad);

                                System.out.println(
                                    jugador.getNombre() +
                                    " recibe " + cantidad +
                                    " de " + recurso
        );
    }
                        }
                    }
                }
            }
        }
    }
    
    public List<Jugador> getJugadoresEnTile(Hexagono tile) {

        List<Jugador> jugadores = new ArrayList<>();

        for (Vertice v : vertices) {

            if (v.getTilesAdyacentes().contains(tile)) {

                Construccion c = v.getConstruccion();

                if (c != null) {
                    Jugador j = c.getPropietario();

                    if (!jugadores.contains(j)) {
                        jugadores.add(j);
                    }
                }
            }
        }

        return jugadores;
    }
    
    public void robarRecurso(Jugador ladrónJugador, Hexagono tile) {

        List<Jugador> jugadores = getJugadoresEnTile(tile);

    // Quitar al mismo jugador
        jugadores.remove(ladrónJugador);

        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores para robar.");
            return;
        }

    // Elegir víctima aleatoria
        Random rand = new Random();
        Jugador victima = jugadores.get(rand.nextInt(jugadores.size()));

        Recurso robado = victima.quitarRecursoAleatorio();

        if (robado != null) {
            ladrónJugador.agregarRecurso(robado, 1);

            System.out.println(
                ladrónJugador.getNombre() +
                " robó " + robado +
                " a " + victima.getNombre()
            );
        } else {
            System.out.println("La víctima no tenía recursos.");
        }
    }
        
    public void moverLadronYRobar(Jugador jugador, Hexagono nuevoTile) {

        ladron.mover(nuevoTile);

        robarRecurso(jugador, nuevoTile);
    }
}
