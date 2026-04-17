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
import model.Arista;
import model.Ciudad;
import model.Hexagono;
import logic.TipoTerreno;
import model.Vertice;

import java.util.*;

public class MapaVisual {
    private final Pane canvas;
    private final Map<TipoTerreno, ImagePattern> texturas = new HashMap<>();
    private static final Double GROSOR_BORDE = 4.0;
    private static final Color COLOR_BORDE = Color.rgb(50, 50, 50);
    private GestorTurnos gestorTurnos;
    private List<Hexagono> ultimaListaHexagonos;
    private List<Vertice> ultimaListaVertices;
    private List<Arista> ultimaListaAristas;

    public MapaVisual(Pane canvas, GestorTurnos gestor) {
        this.canvas = canvas;
        this.canvas.setStyle("-fx-background-color: #2c3e50;"); // Fondo oscuro (Cambiar a azul mar)
        this.gestorTurnos = gestor;
        cargarTexturas();
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
        this.ultimaListaHexagonos = hexagonos;
        this.ultimaListaVertices = vertices;
        this.ultimaListaAristas = aristas;

        canvas.getChildren().clear();

        Set<Vertice> verticesDibujados = new HashSet<>();
        Set<Arista> aristasDibujadas = new HashSet<>();

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

            //DIBUJAR ARISTAS DEL HEXAGONO
            for (Arista a : hex.getAristas()) {
                if (!aristasDibujadas.contains(a)) {
                    dibujarArista(a);
                    aristasDibujadas.add(a);
                }
            }

            //DIBUJAR VERTICE DEL HEXAGONO
            for (Vertice v : hex.getVertices()) {
                // Solo dibujamos si no se ha dibujado antes (puedes usar un Set<Vertice> visitados)
                if (!verticesDibujados.contains(v)) {
                    dibujarVertice(v);
                    verticesDibujados.add(v);
                }
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

        if (v.getConstruccion() == null) {
            // Slot vacío: Un punto gris sutil
            nodo.setFill(Color.rgb(255, 255, 255, 0.3));
            nodo.setStroke(Color.rgb(0, 0, 0, 0.2));

            // Animación simple de hover
            nodo.setOnMouseEntered(e -> {
                nodo.setRadius(12);
                nodo.setFill(Color.WHITE);
            });
            nodo.setOnMouseExited(e -> {
                nodo.setRadius(8);
                nodo.setFill(Color.rgb(255, 255, 255, 0.3));
            });
        } else {
            // Hay construcción: Aldea o Ciudad
            Color colorJugador = obtenerColorJugador(v.getConstruccion().getPropietario());
            nodo.setFill(colorJugador);
            nodo.setStroke(Color.BLACK);
            nodo.setStrokeWidth(2);

            if (v.getConstruccion() instanceof Ciudad) {
                nodo.setRadius(15); // Las ciudades son más grandes
                // Podrías usar un Polygon para que parezca una casita
            }
        }

        // Evento de construcción
        nodo.setOnMouseClicked(e -> manejarClicVertice(v));

        canvas.getChildren().add(nodo);
    }

    private void manejarClicVertice(Vertice v) {
        Jugador actual = gestorTurnos.obtenerTurnoActual();

        if (v.construirAldea(actual)) {
            System.out.println("¡Aldea construida en el vértice " + v.getId() + "!");

            renderizarMapa(ultimaListaHexagonos, ultimaListaVertices, ultimaListaAristas);
        } else {
            System.out.println("No puedes construir aquí. Revisa las reglas.");
        }
    }

    //DIBUJAR ARISTAS

    private void dibujarArista(Arista a) {
        double x1 = a.getV1().getX();
        double y1 = a.getV1().getY();
        double x2 = a.getV2().getX();
        double y2 = a.getV2().getY();

        Line linea = new Line(x1, y1, x2, y2);

        // Se pinta solo si hay carretera
        if (a.tieneCarretera()) {
            linea.setStroke(obtenerColorJugador(a.getCarretera().getPropietario()));
            linea.setStrokeWidth(6.0);
        } else {
            linea.setStroke(Color.rgb(200, 200, 200, 0.3));
            linea.setStrokeWidth(3.0);

            // Efecto hover para indicar que es un lugar interactivo
            linea.setOnMouseEntered(e -> linea.setStroke(Color.rgb(255, 255, 255, 0.6)));
            linea.setOnMouseExited(e -> linea.setStroke(Color.rgb(200, 200, 200, 0.3)));

            // Clic para construir carretera:
            // linea.setOnMouseClicked(e -> manejarClicArista(a));
        }

        canvas.getChildren().add(linea);
    }

    private Color obtenerColorJugador(Jugador jugador) {
        if (jugador == null) return Color.GRAY;

        // Asumiendo que tu Jugador tiene un nombre o ID
        switch (jugador.getNombre().toLowerCase()) {
            case "rojo": return Color.RED;
            case "azul": return Color.BLUE;
            case "verde": return Color.GREEN;
            case "naranja": return Color.ORANGE;
            default: return Color.WHITE;
        }
    }
}
