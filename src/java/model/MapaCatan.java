package model;

import javafx.geometry.Point2D;
import logic.*;
import structures.ArregloDinamico;
import ui.TableroUtils;

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

    private MazoDesarrollo mazo = new MazoDesarrollo();

    public MapaCatan() {   //Constructor
        inicializarTerrenos();
        inicializarNumeros();
        generarMapaHexagonal();

        generarVerticesReales();
        generarAristasReales();

        inicializarLadron();
        System.out.println("Total hexágonos en mapa: " + mapa.size());
        System.out.println("Total hexágonos en lista: " + new ArrayList<>(mapa.values()).size());
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
        List<Vertice> listaGlobal = new ArrayList<>();

        for (Hexagono tile : mapa.values()) {
            double[] centro = TableroUtils.hexToPixel(tile.coord);

            for (int i = 0; i < 6; i++) {
                Point2D p = TableroUtils.getEsquinaHexagono(centro[0], centro[1], i);

                // BÚSQUEDA POR PROXIMIDAD (Mucho más fiable que redondear Strings)
                Vertice existente = null;
                for (Vertice v : listaGlobal) {
                    if (Math.hypot(v.getX() - p.getX(), v.getY() - p.getY()) < 1.0) {
                        existente = v;
                        break;
                    }
                }

                if (existente == null) {
                    Vertice nuevo = new Vertice(listaGlobal.size());
                    nuevo.setPosicionPixeles(p.getX(), p.getY());
                    listaGlobal.add(nuevo);
                    existente = nuevo;
                }

                existente.agregarTile(tile);
                tile.agregarVertice(existente);
            }
        }
        this.vertices = listaGlobal;
    }

    private void generarAristasReales() {
        this.aristas.clear();
        int idArista = 0;

        // En lugar de comparar todos contra todos, recorremos cada hexágono
        // y conectamos sus vértices adyacentes (0 con 1, 1 con 2... 5 con 0)
        for (Hexagono hex : mapa.values()) {
            List<Vertice> vHex = hex.getVertices();

            for (int i = 0; i < 6; i++) {
                Vertice v1 = vHex.get(i);
                Vertice v2 = vHex.get((i + 1) % 6); // El siguiente vértice (circular)

                // Verificar si esta arista ya fue creada por otro hexágono vecino
                Arista existente = null;
                for (Arista a : aristas) {
                    if ((a.getV1() == v1 && a.getV2() == v2) || (a.getV1() == v2 && a.getV2() == v1)) {
                        existente = a;
                        break;
                    }
                }

                if (existente == null) {
                    Arista nueva = new Arista(idArista++, v1, v2);
                    this.aristas.add(nueva);
                    hex.agregarArista(nueva);
                    // Establecer vecindad entre vértices
                    v1.agregarVecino(v2);
                    v2.agregarVecino(v1);
                } else {
                    // Si ya existe, solo la vinculamos a este hexágono también
                    hex.agregarArista(existente);
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

    public String producirRecursos(int numero) {
        StringBuilder resumen = new StringBuilder();

        for (Hexagono tile : mapa.values()) {
            if (tile.numero == numero && tile != ladron.getPosicion()) {
                for (Vertice v : vertices) {
                    if (v.getTilesAdyacentes().contains(tile)) {
                        Construccion c = v.getConstruccion();
                        if (c != null) {
                            Jugador jugador = c.getPropietario();
                            int cantidad = 0;
                            if (c instanceof Aldea) cantidad = 1;
                            else if (c instanceof Ciudad) cantidad = 2;

                            Recurso recurso = tile.getRecurso();
                            if (recurso != null) {
                                jugador.agregarRecurso(recurso, cantidad);
                                resumen.append(jugador.getNombre())
                                        .append(" recibe ")
                                        .append(cantidad)
                                        .append(" ")
                                        .append(recurso)
                                        .append("\n");
                            }
                        }
                    }
                }
            }
        }

        return resumen.length() > 0 ? resumen.toString() : "Nadie recibe recursos";
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

    public String robarRecurso(Jugador ladronJugador, Hexagono tile) {
        List<Jugador> jugadores = getJugadoresEnTile(tile);
        jugadores.remove(ladronJugador);

        if (jugadores.isEmpty()) return ladronJugador.getNombre() + " movió el ladrón\n(nadie para robar)";

        Random rand = new Random();
        Jugador victima = jugadores.get(rand.nextInt(jugadores.size()));
        Recurso robado = victima.quitarRecursoAleatorio();

        if (robado != null) {
            ladronJugador.agregarRecurso(robado, 1);
            return "🗡 " + ladronJugador.getNombre() + " robó " + robado + " a " + victima.getNombre();
        } else {
            return "🗡 " + ladronJugador.getNombre() + " movió el ladrón\n(" + victima.getNombre() + " no tenía recursos)";
        }
    }

    public String moverLadronYRobar(Jugador jugador, Hexagono nuevoTile) {
        ladron.mover(nuevoTile);
        return robarRecurso(jugador, nuevoTile);
    }

    public Ladron getLadron() {
        return ladron;
    }

    public int calcularPuntos(Jugador jugador) {
        int puntos = 0;
        for (Vertice v : vertices) {
            if (v.getConstruccion() != null &&
                    v.getConstruccion().getPropietario() == jugador) {
                puntos += v.getConstruccion().getPuntosVictoria();
            }
        }
        return puntos;
    }

    public Jugador verificarVictoria() {
        for (Jugador jugador : obtenerJugadores()) {
            if (calcularPuntos(jugador) >= 10) {
                return jugador;
            }
        }
        return null;
    }

    public List<Jugador> obtenerJugadores() {
        List<Jugador> jugadores = new ArrayList<>();
        for (Vertice v : vertices) {
            if (v.getConstruccion() != null) {
                Jugador j = v.getConstruccion().getPropietario();
                if (!jugadores.contains(j)) {
                    jugadores.add(j);
                }
            }
        }
        return jugadores;
    }

    public MazoDesarrollo getMazo() { return mazo; }

    public CartaDesarrollo comprarCartaDesarrollo(Jugador jugador) {
        if (!jugador.tieneRecursos(CartaDesarrollo.COSTO)) {
            System.out.println("No tienes recursos para comprar carta");
            return null;
        }
        CartaDesarrollo carta = mazo.robarCarta();
        if (carta == null) {
            System.out.println("El mazo está vacío");
            return null;
        }
        jugador.gastarRecursos(CartaDesarrollo.COSTO);
        return carta;
    }
}
