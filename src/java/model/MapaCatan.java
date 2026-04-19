package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;
import ui.TableroUtils;

public class MapaCatan {  //Atributos
    
    private Map<HexCoord, Hexagono> mapa = new HashMap<>();
    // Cola para mezclar terrenos
    private Queue<TipoTerreno> bolsaTerrenos = new LinkedList<>();
    // Cola para números
    private Queue<Integer> bolsaNumeros = new LinkedList<>();
    private List<Vertice> vertices = new ArrayList<>();
    private Set<Arista> aristas = new HashSet<>();
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
        
        System.out.println("Vertices reales: " + vertices.size());
        System.out.println("Aristas reales: " + aristas.size());
        System.out.println("Hexagonos: " + mapa.size());
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

        double[] centro = TableroUtils.hexToPixel(tile.coord);

        for (int i = 0; i < 6; i++) {

            double angle = Math.toRadians(60 * i);

            double x = centro[0] + TableroUtils.TAMAÑO * Math.cos(angle);
            double y = centro[1] + TableroUtils.TAMAÑO * Math.sin(angle);

            VertexKey key = new VertexKey(x, y);

            Vertice v = mapaVertices.get(key);

            if (v == null) {
                v = new Vertice(id++);
                v.setPosicionPixeles(x, y);
                mapaVertices.put(key, v);
                vertices.add(v);
            }

            v.agregarTile(tile);
            tile.agregarVertice(v);
        }
    }
}
    
    private void generarAristasReales() {
        int id = 0;
        

        List<Vertice> lista = new ArrayList<>(mapaVertices.values());

        for (int i = 0; i < lista.size(); i++) {
            Vertice v1 = lista.get(i);

            for (int j = i + 1; j < lista.size(); j++) {
                Vertice v2 = lista.get(j);

            // 1. Calcular distancia (SIEMPRE fuera de condiciones)
                double dx = v1.getX() - v2.getX();
                double dy = v1.getY() - v2.getY();
                double distancia = Math.sqrt(dx * dx + dy * dy);

            // 2. Contar hexágonos compartidos
                int comunes = 0;
                for (Hexagono t : v1.getTilesAdyacentes()) {
                    if (v2.getTilesAdyacentes().contains(t)) {
                        comunes++;
                    }
                }

            // 3. Clasificar tipo de arista
                boolean esInterna = comunes == 2;
                boolean esBorde = comunes == 1;

            // 4. Validación final
                if ((esInterna || esBorde) &&
                    distancia < TableroUtils.TAMAÑO * 1.1) {

                // 5. Evitar duplicados
                    boolean existe = false;

                    for (Arista a : aristas) {
                        if ((a.getV1() == v1 && a.getV2() == v2) ||
                            (a.getV1() == v2 && a.getV2() == v1)) {
                            existe = true;
                            break;
                        }
                    }

                    if (!existe) {
                        Arista nueva = new Arista(id++, v1, v2);
                        aristas.add(nueva);

                    // 6. Vecinos (una sola vez)
                        v1.agregarVecino(v2);
                        v2.agregarVecino(v1);

                    // 7. Asociar a hexágonos
                        for (Hexagono tile : mapa.values()) {
                            if (tile.getVertices().contains(v1) &&
                                tile.getVertices().contains(v2)) {

                                tile.agregarArista(nueva);
                            }
                        }
                    }
                }
            }
        }
    }

    public void mostrarVertices() {

        for (int i = 0; i < vertices.size(); i++) {

            Vertice v = vertices.get(i);

            System.out.print(i + ": ");

            if (v.getConstruccion() == null) {
                System.out.println("Libre");
            } else {
                System.out.println("Ocupado por " + v.getConstruccion().getPropietario().getNombre());
            }
        }
    }

    public void mostrarAristas() {

        int i = 0;

        for (Arista a : aristas) {

            System.out.print(i + ": ");

            if (a.getConstruccion() == null) {
                System.out.println("Libre");
            } else {
                System.out.println("Ocupada");
            }

            i++;
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
    
    public Set<Arista> getAristas(){
        return aristas;
    }
    
    public List<Vertice> getVertices() {
        return vertices;
    }
    
    public Vertice getVertice(int index) {

        if (index >= 0 && index < vertices.size()) {
            return vertices.get(index);
        }

        return null;
    }

    public Arista getArista(int index) {
        return new ArrayList<>(aristas).get(index);
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
