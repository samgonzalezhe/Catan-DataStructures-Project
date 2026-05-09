package logic.bot;

import logic.*;
import model.*;

import java.util.*;

public class BotJugador {

    private NodoDecision raiz;
    private EstadoBot estado;

    public BotJugador(Jugador jugador, MapaCatan mapa, GestorTurnos gestor,
                      List<Vertice> vertices, List<Arista> aristas) {
        this.estado = new EstadoBot(jugador, mapa, gestor, vertices, aristas);
        this.raiz = construirArbol();
    }

    private NodoDecision construirArbol() {
        // Hojas - acciones
        NodoAccion finalizarTurno = new NodoAccion(
                "Finalizar turno",
                e -> e.turnoTerminado = true
        );

        NodoAccion comprarCarta = new NodoAccion(
                "Comprar carta de desarrollo",
                e -> {
                    CartaDesarrollo carta = e.mapa.comprarCartaDesarrollo(e.jugador);
                    if (carta != null) e.jugador.agregarCartaDesarrollo(carta);
                }
        );

        NodoAccion construirCamino = new NodoAccion(
                "Construir camino",
                e -> {
                    Arista mejor = encontrarMejorArista(e);
                    if (mejor != null) {
                        mejor.construirCarretera(new Carretera(e.jugador));
                        e.jugador.gastarRecursos(Carretera.COSTO);
                    }
                }
        );

        NodoAccion construirAldea = new NodoAccion(
                "Construir aldea",
                e -> {
                    Vertice mejor = encontrarMejorVertice(e);
                    if (mejor != null) {
                        mejor.construirAldeaDirecto(e.jugador);
                        e.jugador.gastarRecursos(Aldea.COSTO);
                    }
                }
        );

        NodoAccion construirCiudad = new NodoAccion(
                "Construir ciudad",
                e -> {
                    Vertice aldea = encontrarAldea(e);
                    if (aldea != null) {
                        aldea.mejorarACiudad(e.jugador);
                        e.jugador.gastarRecursos(Ciudad.COSTO);
                    }
                }
        );

        NodoAccion usarCarta = new NodoAccion(
                "Usar carta de desarrollo",
                e -> {
                    List<CartaDesarrollo> cartas = e.jugador.getCartasDesarrollo();
                    for (CartaDesarrollo carta : cartas) {
                        if (!carta.isUsada()) {
                            carta.realizarAccion(e.jugador, null);
                            break;
                        }
                    }
                }
        );

        // Árbol de decisiones construido de abajo hacia arriba
        NodoDecision nodoComprarOFinalizar = new NodoCondicion(
                "¿Tengo recursos para carta?",
                e -> e.jugador.tieneRecursos(CartaDesarrollo.COSTO),
                comprarCarta,
                finalizarTurno
        );

        NodoDecision nodoCaminoOCarta = new NodoCondicion(
                "¿Tengo recursos para camino?",
                e -> e.jugador.tieneRecursos(Carretera.COSTO),
                construirCamino,
                nodoComprarOFinalizar
        );

        NodoDecision nodoAldeaOCamino = new NodoCondicion(
                "¿Tengo recursos para aldea?",
                e -> e.jugador.tieneRecursos(Aldea.COSTO) &&
                        encontrarMejorVertice(e) != null,
                construirAldea,
                nodoCaminoOCarta
        );

        NodoDecision nodoCiudadOAldea = new NodoCondicion(
                "¿Tengo recursos para ciudad?",
                e -> e.jugador.tieneRecursos(Ciudad.COSTO) &&
                        encontrarAldea(e) != null,
                construirCiudad,
                nodoAldeaOCamino
        );

        NodoDecision nodoUsarCartaOConstruir = new NodoCondicion(
                "¿Tengo cartas sin usar?",
                e -> e.jugador.getCartasDesarrollo().stream()
                        .anyMatch(c -> !c.isUsada()),
                usarCarta,
                nodoCiudadOAldea
        );

        NodoDecision nodoVictoria = new NodoCondicion(
                "¿Puedo ganar ahora?",
                e -> e.mapa.calcularPuntos(e.jugador) >= 9,
                construirCiudad, // intenta lo que da más puntos
                nodoUsarCartaOConstruir
        );

        return nodoVictoria;
    }

    public void ejecutarTurno() {
        estado.turnoTerminado = false;

        // Recorrer el árbol hasta llegar a una hoja
        NodoDecision nodo = raiz;
        while (nodo != null) {
            nodo = nodo.evaluar(estado);
        }
    }

    public int lanzarDados() {
        estado.dadoLanzado = false;
        estado.turnoTerminado = false;

        Dado dado = new Dado();
        int resultado = dado.lanzar();
        estado.ultimoDado = resultado;
        estado.dadoLanzado = true;
        return resultado;
    }

    public void moverLadronInteligente() {
        // Mover al hex del jugador con más puntos
        Jugador rival = encontrarRivalMasFuerte();
        if (rival != null) {
            for (model.Hexagono hex : estado.mapa.getMapa().values()) {
                if (!hex.tieneLadron()) {
                    for (model.Vertice v : estado.vertices) {
                        if (v.getConstruccion() != null &&
                                v.getConstruccion().getPropietario() == rival &&
                                v.getTilesAdyacentes().contains(hex)) {
                            estado.mapa.moverLadronYRobar(estado.jugador, hex);
                            return;
                        }
                    }
                }
            }
        }
    }

    private Jugador encontrarRivalMasFuerte() {
        List<Jugador> jugadores = estado.mapa.obtenerJugadores();
        return jugadores.stream()
                .filter(j -> j != estado.jugador)
                .max(Comparator.comparingInt(j -> estado.mapa.calcularPuntos(j)))
                .orElse(null);
    }

    private Vertice encontrarMejorVertice(EstadoBot e) {
        Vertice mejor = null;
        int mejorPuntaje = -1;

        for (Vertice v : e.vertices) {
            if (v.getConstruccion() != null) continue;
            if (!v.esPosicionValida()) continue;

            // Verificar carretera propia adyacente
            boolean tieneCarretera = e.aristas.stream()
                    .anyMatch(a -> a.tieneCarretera() &&
                            a.getCarretera().getPropietario() == e.jugador &&
                            (Math.hypot(a.getV1().getX() - v.getX(),
                                    a.getV1().getY() - v.getY()) < 1.0 ||
                                    Math.hypot(a.getV2().getX() - v.getX(),
                                            a.getV2().getY() - v.getY()) < 1.0));

            if (!tieneCarretera) continue;

            // Puntaje = suma de probabilidades de los hexágonos adyacentes
            int puntaje = v.getTilesAdyacentes().stream()
                    .mapToInt(t -> probabilidad(t.numero))
                    .sum();

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = v;
            }
        }
        return mejor;
    }

    private Arista encontrarMejorArista(EstadoBot e) {
        for (Arista a : e.aristas) {
            if (a.tieneCarretera()) continue;
            if (a.puedeConstruirCarretera(e.jugador, e.aristas)) {
                return a;
            }
        }
        return null;
    }

    private Vertice encontrarAldea(EstadoBot e) {
        return e.vertices.stream()
                .filter(v -> v.getConstruccion() instanceof Aldea &&
                        v.getConstruccion().getPropietario() == e.jugador)
                .findFirst().orElse(null);
    }

    private int probabilidad(int numero) {
        // Probabilidad relativa de cada número en 2 dados
        int[] prob = {0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
        if (numero < 0 || numero > 12) return 0;
        return prob[numero];
    }

    public Vertice encontrarMejorVerticeInicial() {
        Vertice mejor = null;
        int mejorPuntaje = -1;

        for (Vertice v : estado.vertices) {
            if (v.getConstruccion() != null) continue;
            if (!v.esPosicionValida()) continue;

            int puntaje = v.getTilesAdyacentes().stream()
                    .mapToInt(t -> probabilidad(t.numero))
                    .sum();

            if (puntaje > mejorPuntaje) {
                mejorPuntaje = puntaje;
                mejor = v;
            }
        }
        return mejor;
    }

    public Arista encontrarAristaInicialAdyacente(Vertice vertice) {
        if (vertice == null) return null;
        for (Arista a : estado.aristas) {
            if (a.tieneCarretera()) continue;
            if (Math.hypot(a.getV1().getX() - vertice.getX(),
                    a.getV1().getY() - vertice.getY()) < 1.0 ||
                    Math.hypot(a.getV2().getX() - vertice.getX(),
                            a.getV2().getY() - vertice.getY()) < 1.0) {
                return a;
            }
        }
        return null;
    }

    public int getUltimoDado() { return estado.ultimoDado; }
    public boolean isTurnoTerminado() { return estado.turnoTerminado; }
    public Jugador getJugador() { return estado.jugador; }

    public void setUltimoDado(int resultado) {
        estado.ultimoDado = resultado;
        estado.dadoLanzado = true;
    }

    public boolean evaluarIntercambio(Map<Recurso, Integer> recibe, Map<Recurso, Integer> entrega) {
        if (!estado.jugador.tieneRecursos(entrega)) return false;
        int valorRecibe = calcularValorRecursos(recibe);
        int valorEntrega = calcularValorRecursos(entrega);

        if (valorEntrega > valorRecibe) return false;

        int utilidadAntes = calcularUtilidad(estado.jugador.getRecursos());

        Map<Recurso, Integer> recursosSimulados =
                new HashMap<>(estado.jugador.getRecursos());
        for (Map.Entry<Recurso, Integer> e : entrega.entrySet()) {
            recursosSimulados.merge(e.getKey(), -e.getValue(), Integer::sum);
        }
        for (Map.Entry<Recurso, Integer> e : recibe.entrySet()) {
            recursosSimulados.merge(e.getKey(), e.getValue(), Integer::sum);
        }

        int utilidadDespues = calcularUtilidadSimulada(recursosSimulados);

        // Aceptar si mejora su situación
        return utilidadDespues >= utilidadAntes;
    }

    private int calcularValorRecursos(Map<Recurso, Integer> recursos) {
        // Piedra y trigo valen más porque son para ciudad
        int valor = 0;
        for (Map.Entry<Recurso, Integer> e : recursos.entrySet()) {
            switch (e.getKey()) {
                case PIEDRA: valor += e.getValue() * 3; break;
                case TRIGO:  valor += e.getValue() * 2; break;
                case MADERA: valor += e.getValue() * 1; break;
                case LADRILLO: valor += e.getValue() * 1; break;
                case OVEJA:  valor += e.getValue() * 1; break;
            }
        }
        return valor;
    }

    private int calcularUtilidad(Map<Recurso, Integer> recursos) {
        return calcularUtilidadSimulada(new HashMap<>(recursos));
    }

    private int calcularUtilidadSimulada(Map<Recurso, Integer> recursos) {
        int utilidad = 0;

        // Cuánto le falta para ciudad (máxima prioridad)
        int faltaTrigoCiudad = Math.max(0, 2 - recursos.getOrDefault(Recurso.TRIGO, 0));
        int faltaPiedraCiudad = Math.max(0, 3 - recursos.getOrDefault(Recurso.PIEDRA, 0));
        utilidad += (5 - faltaTrigoCiudad - faltaPiedraCiudad) * 3;

        // Cuánto le falta para aldea
        int faltaMadera = Math.max(0, 1 - recursos.getOrDefault(Recurso.MADERA, 0));
        int faltaLadrillo = Math.max(0, 1 - recursos.getOrDefault(Recurso.LADRILLO, 0));
        int faltaTrigoAldea = Math.max(0, 1 - recursos.getOrDefault(Recurso.TRIGO, 0));
        int faltaOveja = Math.max(0, 1 - recursos.getOrDefault(Recurso.OVEJA, 0));
        utilidad += (4 - faltaMadera - faltaLadrillo - faltaTrigoAldea - faltaOveja) * 2;

        // Cuánto le falta para camino
        int faltaMaderaCamino = Math.max(0, 1 - recursos.getOrDefault(Recurso.MADERA, 0));
        int faltaLadrilloCamino = Math.max(0, 1 - recursos.getOrDefault(Recurso.LADRILLO, 0));
        utilidad += (2 - faltaMaderaCamino - faltaLadrilloCamino);

        return utilidad;
    }
}
