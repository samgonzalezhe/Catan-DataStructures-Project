package ui;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.effect.DropShadow;
import logic.GestorTurnos;
import logic.Jugador;
import model.*;
import logic.TipoTerreno;

import java.util.*;

public class MapaVisual {
    private final Pane canvas;
    private final Map<TipoTerreno, ImagePattern> texturas = new HashMap<>();
    private static final Double GROSOR_BORDE = 4.0;
    private static final Color COLOR_BORDE = Color.rgb(50, 50, 50);
    private GestorTurnos gestorTurnos;
    private JuegoView.ModoConstruccion modoActual = JuegoView.ModoConstruccion.NINGUNO;
    private List<Hexagono> ultimaListaHexagonos;
    private List<Vertice> ultimaListaVertices;
    private List<Arista> ultimaListaAristas;
    private Map<Vertice, Circle> nodosVertices = new HashMap<>();
    private Map<Arista, Line> nodosAristas = new HashMap<>();
    private JuegoView.CallbackConstruccion callback;
    private boolean faseInicial = true;


    public MapaVisual(Pane canvas, GestorTurnos gestor) {
        this.canvas = canvas;
        this.canvas.setStyle("-fx-background-color: #2c3e50;"); // Fondo oscuro (Cambiar a azul mar)
        this.gestorTurnos = gestor;
        cargarTexturas();
    }

    public void setCallback(JuegoView.CallbackConstruccion cb) {
        this.callback = cb;
    }

    public void setFaseInicial(boolean faseInicial) {
        this.faseInicial = faseInicial;
    }

    private void cargarTexturas() {
        try {
            texturas.put(TipoTerreno.BOSQUE, nuevoPattern("/bosqueCatan.png"));
            texturas.put(TipoTerreno.COLINA, nuevoPattern("/colinaCatan.png"));
            texturas.put(TipoTerreno.CAMPO, nuevoPattern("/campoCatan.png"));
            texturas.put(TipoTerreno.PASTO, nuevoPattern("/pastoCatan.png"));
            texturas.put(TipoTerreno.MONTAÑA, nuevoPattern("/montanaCatan.png"));
            texturas.put(TipoTerreno.DESIERTO, nuevoPattern("/desiertoCatan.png"));
        } catch (Exception e) {
            System.err.println("Error cargando imágenes. Asegúrate de que existen");
        }
    }

    private ImagePattern nuevoPattern(String path) {
        Image img = new Image(getClass().getResourceAsStream(path));
        return new ImagePattern(img);
    }

    public void renderizarMapa(List<Hexagono> hexagonos, List<Vertice> vertices, List<Arista> aristas) {
        System.out.println("DEBUG: Iniciando renderizado con " + hexagonos.size() + " hexágonos, "
                + vertices.size() + " vértices y " + aristas.size() + " aristas.");
        canvas.getChildren().clear();
        nodosVertices.clear();
        nodosAristas.clear();

        this.ultimaListaHexagonos = hexagonos;
        this.ultimaListaVertices = vertices;
        this.ultimaListaAristas = aristas;

        DropShadow sombraTablero = new DropShadow();
        sombraTablero.setRadius(15.0);
        sombraTablero.setColor(Color.rgb(0, 0, 0, 0.5));

        //DIBUJAR HEXAGONOS
        for (Hexagono hex : hexagonos) {
            double[] pos = TableroUtils.hexToPixel(hex.coord);
            double xReal = pos[0];
            double yReal = pos[1];
            Polygon hexShape = crearHexagono(xReal, yReal);

            // --- ESTILIZADO DEL HEXÁGONO ---
            //Rellenar con imagen
            ImagePattern textura = texturas.getOrDefault(hex.getTipo(), new ImagePattern(new Image("https://via.placeholder.com/150"))); // Placeholder por si acaso
            hexShape.setFill(textura);

            //2. Bordes
            hexShape.setStroke(COLOR_BORDE);
            hexShape.setStrokeWidth(GROSOR_BORDE);
            hexShape.setStrokeLineJoin(StrokeLineJoin.ROUND);

            //3. Sombra tablero
            hexShape.setEffect(sombraTablero);

            // Añadir eventos
            //hexShape.setOnMouseClicked(e -> System.out.println("Clic en: " + hex.getTipo()));

            canvas.getChildren().add(hexShape);

            // --- AÑADIR NÚMERO (Ficha) ---
            if (hex.getTipo() != TipoTerreno.DESIERTO) {
                // Dibujar círculo blanco debajo del número
                javafx.scene.shape.Circle ficha = new javafx.scene.shape.Circle(xReal, yReal, 18);
                ficha.setFill(Color.rgb(245, 245, 245, 0.9));
                ficha.setStroke(Color.BLACK);
                ficha.setStrokeWidth(1.0);
                canvas.getChildren().add(ficha);

                // Dibujar el número encima de la ficha
                Text numeroText = new Text(String.valueOf(hex.numero));
                numeroText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

                // Colores estándar de Catan: 6 y 8 en rojo, el resto en negro
                if (hex.numero == 6 || hex.numero == 8) {
                    numeroText.setFill(Color.RED);
                } else {
                    numeroText.setFill(Color.BLACK);
                }

                // Centrar el texto
                double textWidth = numeroText.getLayoutBounds().getWidth();
                double textHeight = numeroText.getLayoutBounds().getHeight();
                numeroText.setX(xReal - textWidth / 2);
                numeroText.setY(yReal + textHeight / 4); // Ajuste fino vertical

                canvas.getChildren().add(numeroText);
            }


        }

        //DIBUJAR ARISTAS
        Set<Arista> aristasDibujadas = new HashSet<>();
        for (Arista a : aristas) {
            if (!aristasDibujadas.contains(a)) {
                dibujarArista(a); // Asegúrate de que esta función añada al canvas
                aristasDibujadas.add(a);
            }
        }

        // 3. DIBUJAR VERTICES
        Set<Vertice> verticesDibujados = new HashSet<>();
        for (Vertice v : vertices) {
            if (!verticesDibujados.contains(v)) {
                dibujarVertice(v); // Asegúrate de que esta función añada al canvas
                verticesDibujados.add(v);
            }
        }
    }

    private Polygon crearHexagono(double x, double y) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            hex.getPoints().addAll(
                    x + TableroUtils.TAMAÑO * Math.cos(angle),
                    y + TableroUtils.TAMAÑO * Math.sin(angle)
            );
        }
        return hex;
    }

    //DIBUJAR VERTICES
    private void dibujarVertice(Vertice v) {
        Circle nodo = new Circle(8);
        nodo.setCenterX(v.getX());
        nodo.setCenterY(v.getY());

        actualizarEstiloVertice(nodo, v);

        nodo.setOnMouseEntered(e -> {
            if (modoActual != JuegoView.ModoConstruccion.NINGUNO)
                nodo.setRadius(nodo.getRadius() + 3);
        });
        nodo.setOnMouseExited(e -> {
            if (modoActual != JuegoView.ModoConstruccion.NINGUNO)
                nodo.setRadius(nodo.getRadius() - 3);
        });

        nodo.setOnMouseClicked(e -> {
            Jugador jugador = gestorTurnos.obtenerTurnoActual();
            if (jugador == null) return;

            boolean exito = false;
            if (modoActual == JuegoView.ModoConstruccion.ALDEA) {

                // En fase normal verificar carretera propia adyacente
                if (!faseInicial) {
                    boolean tieneCarreteraPropia = false;
                    for (Arista a : ultimaListaAristas) {
                        if (a.tieneCarretera() &&
                                a.getCarretera().getPropietario() == jugador) {
                            if (Math.hypot(a.getV1().getX() - v.getX(),
                                    a.getV1().getY() - v.getY()) < 1.0 ||
                                    Math.hypot(a.getV2().getX() - v.getX(),
                                            a.getV2().getY() - v.getY()) < 1.0) {
                                tieneCarreteraPropia = true;
                                break;
                            }
                        }
                    }
                    if (!tieneCarreteraPropia) {
                        System.out.println("Debes construir adyacente a tu carretera");
                        return;
                    }
                }

                exito = v.construirAldeaDirecto(jugador);
                if (exito && callback != null) callback.onAldeaColocada();

            } else if (modoActual == JuegoView.ModoConstruccion.CIUDAD) {
                exito = v.mejorarACiudad(jugador);
            }

            if (exito) actualizarEstiloVertice(nodo, v);
        });

        nodosVertices.put(v, nodo);
        canvas.getChildren().add(nodo);
    }

    private void manejarClicVertice(Vertice v) {
        Jugador actual = gestorTurnos.obtenerTurnoActual();

        if (v.getConstruccion() == null) {
            if (v.puedeConstruirAldea(actual)) { // Debes crear este método en Vertice
                v.construirAldea(actual);
                System.out.println("¡Aldea construida por " + actual.getNombre() + "!");
            } else {
                System.out.println("No se puede construir una aldea aquí (Reglas/Recursos).");
            }
        }
    }

    //DIBUJAR ARISTAS

    private void dibujarArista(Arista a) {
        double x1 = a.getV1().getX();
        double y1 = a.getV1().getY();
        double x2 = a.getV2().getX();
        double y2 = a.getV2().getY();

        Line linea = new Line(x1, y1, x2, y2);
        actualizarEstiloArista(linea, a);

        linea.setOnMouseEntered(e -> {
            if (modoActual == JuegoView.ModoConstruccion.CAMINO && !a.tieneCarretera())
                linea.setStrokeWidth(7);
        });
        linea.setOnMouseExited(e -> actualizarEstiloArista(linea, a));

        linea.setOnMouseClicked(e -> {
            Jugador jugador = gestorTurnos.obtenerTurnoActual();
            if (jugador == null) return;

            if (modoActual == JuegoView.ModoConstruccion.CAMINO && !a.tieneCarretera()) {

                // Buscar adyacencia por posición en lugar de por referencia
                boolean adyacenteAConstruccion = false;
                for (Vertice v : ultimaListaVertices) {
                    if (v.getConstruccion() != null &&
                            v.getConstruccion().getPropietario() == jugador) {
                        if (Math.hypot(v.getX() - a.getV1().getX(), v.getY() - a.getV1().getY()) < 1.0 ||
                                Math.hypot(v.getX() - a.getV2().getX(), v.getY() - a.getV2().getY()) < 1.0) {
                            adyacenteAConstruccion = true;
                            break;
                        }
                    }
                }

                // Buscar adyacencia a carretera por posición
                boolean adyacenteACarretera = false;
                for (Arista otra : ultimaListaAristas) {
                    if (otra != a && otra.tieneCarretera() &&
                            otra.getCarretera().getPropietario() == jugador) {
                        // Encontrar el vértice compartido entre las dos aristas
                        double[] verticeCompartido = null;

                        if (Math.hypot(otra.getV1().getX() - a.getV1().getX(),
                                otra.getV1().getY() - a.getV1().getY()) < 1.0) {
                            verticeCompartido = new double[]{a.getV1().getX(), a.getV1().getY()};
                        } else if (Math.hypot(otra.getV1().getX() - a.getV2().getX(),
                                otra.getV1().getY() - a.getV2().getY()) < 1.0) {
                            verticeCompartido = new double[]{a.getV2().getX(), a.getV2().getY()};
                        } else if (Math.hypot(otra.getV2().getX() - a.getV1().getX(),
                                otra.getV2().getY() - a.getV1().getY()) < 1.0) {
                            verticeCompartido = new double[]{a.getV1().getX(), a.getV1().getY()};
                        } else if (Math.hypot(otra.getV2().getX() - a.getV2().getX(),
                                otra.getV2().getY() - a.getV2().getY()) < 1.0) {
                            verticeCompartido = new double[]{a.getV2().getX(), a.getV2().getY()};
                        }

                        if (verticeCompartido != null) {
                            // Verificar que el vértice compartido no tenga construcción de otro jugador
                            boolean bloqueado = false;
                            for (Vertice v : ultimaListaVertices) {
                                if (Math.hypot(v.getX() - verticeCompartido[0],
                                        v.getY() - verticeCompartido[1]) < 1.0) {
                                    if (v.getConstruccion() != null &&
                                            v.getConstruccion().getPropietario() != jugador) {
                                        bloqueado = true;
                                    }
                                    break;
                                }
                            }
                            if (!bloqueado) {
                                adyacenteACarretera = true;
                                break;
                            }
                        }
                    }
                }

                if (adyacenteAConstruccion || adyacenteACarretera) {
                    a.construirCarretera(new Carretera(jugador));
                    actualizarEstiloArista(linea, a);
                    if (callback != null) callback.onCaminoColocado();
                } else {
                    System.out.println("Debes construir adyacente a tu aldea o carretera");
                }
            }
        });
        nodosAristas.put(a, linea);
        canvas.getChildren().add(linea);
    }

    private Color obtenerColorJugador(Jugador jugador) {
        if (jugador == null) return Color.GRAY;
        switch (jugador.getColor()) {
            case "rojo":    return Color.RED;
            case "azul":    return Color.BLUE;
            case "verde":   return Color.GREEN;
            case "naranja": return Color.ORANGE;
            default:        return Color.WHITE;
        }
    }

    public void setModo(JuegoView.ModoConstruccion modo) {
        this.modoActual = modo;
    }

    private void actualizarEstiloVertice(Circle nodo, Vertice v) {
        if (v.getConstruccion() == null) {
            nodo.setRadius(8);
            nodo.setFill(Color.rgb(255, 255, 255, 0.3));
            nodo.setStroke(Color.rgb(0, 0, 0, 0.2));
            nodo.setStrokeWidth(1);
        } else {
            Color colorJugador = obtenerColorJugador(v.getConstruccion().getPropietario());
            nodo.setFill(colorJugador);
            nodo.setStroke(Color.DARKGRAY);
            nodo.setStrokeWidth(2);
            if (v.getConstruccion() instanceof Ciudad) {
                nodo.setRadius(14);
                nodo.setStroke(Color.BLACK);
            } else {
                nodo.setRadius(10);
            }
        }
    }

    private void actualizarEstiloArista(Line linea, Arista a) {
        if (a.tieneCarretera()) {
            linea.setStroke(obtenerColorJugador(a.getCarretera().getPropietario()));
            linea.setStrokeWidth(6);
            linea.setOpacity(1.0);
        } else {
            linea.setStroke(Color.rgb(200, 200, 200, 0.3));
            linea.setStrokeWidth(3);
            linea.setOpacity(1.0);
        }
    }
}
