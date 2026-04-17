package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Point2D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.*;
import logic.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MainApp extends Application {

    private MapaVisual mapaVisual;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Contenedor del mapa y el motor visual
        Pane mapPane = new Pane();
        MapaCatan logicaCatan = new MapaCatan();
        GestorTurnos gestor = new GestorTurnos();
        Jugador j1 = new Jugador("Juan");
        Jugador j2 = new Jugador("Ana");

        gestor.registrarJugador(j1);
        gestor.registrarJugador(j2);
        MapaVisual mapaVisual = new MapaVisual(mapPane, gestor);


        mapPane.setPrefSize(1000, 1000);

        List<Hexagono> hexagonos = new ArrayList<>(logicaCatan.getMapa().values());
        Set<Vertice> setVertices = new HashSet<>();
        Set<Arista> setAristas = new HashSet<>();

        for (Hexagono hex : hexagonos) {
            setVertices.addAll(hex.getVertices());
            setAristas.addAll(hex.getAristas());
        }

        List<Vertice> listaVertices = new ArrayList<>(setVertices);
        List<Arista> listaAristas = new ArrayList<>(setAristas);

        for (Vertice v : listaVertices) {
            double xPromedio = v.getTilesAdyacentes().stream().mapToDouble(t -> TableroUtils.hexToPixel(t.coord)[0]).average().orElse(0);
            double yPromedio = v.getTilesAdyacentes().stream().mapToDouble(t -> TableroUtils.hexToPixel(t.coord)[1]).average().orElse(0);
            v.setPosicionPixeles(xPromedio, yPromedio);
        }

        mapaVisual.renderizarMapa(hexagonos, listaVertices, listaAristas);

        // Envolver mapa en Group
        Group zoomGroup = new Group(mapPane);

        //ScrollPane para navegar por el mapa
        ScrollPane scrollPane = new ScrollPane(zoomGroup);
        scrollPane.setPannable(true); // Arrastrar el mapa con el clic derecho
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setStyle("-fx-background-color: #2c3e50;");
        scrollPane.getContent().setStyle("-fx-background-color: #2c3e50;");


        //Lógica del Zoom
        zoomGroup.setOnScroll(event -> {
            double scaleFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;
            double oldScale = zoomGroup.getScaleX();

            //Limita el zoom
            double newScale = Math.max(0.8, Math.min(oldScale * scaleFactor, 2.5));
            double factor = newScale / oldScale;

            // Calculamos la posición del ratón RELATIVA al grupo
            double mouseX = event.getX();
            double mouseY = event.getY();

            zoomGroup.setScaleX(newScale);
            zoomGroup.setScaleY(newScale);

            // Ajustamos la traslación para que el punto bajo el ratón NO se mueva
            zoomGroup.setTranslateX(zoomGroup.getTranslateX() - (mouseX * (factor - 1)));
            zoomGroup.setTranslateY(zoomGroup.getTranslateY() - (mouseY * (factor - 1)));

            event.consume();
        });

        final Point2D[] dragAnchor = new Point2D[1];

        zoomGroup.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown() && dragAnchor[0] != null) {
                double deltaX = event.getX() - dragAnchor[0].getX();
                double deltaY = event.getY() - dragAnchor[0].getY();

                double newX = zoomGroup.getTranslateX() + deltaX;
                double newY = zoomGroup.getTranslateY() + deltaY;

                // --- LIMITAR ARRASTRE ---
                // Esto evita que el mapa se mueva más allá de los bordes de la ventana
                double minX = -100; // Ajusta según el tamaño de tu ventana
                double maxX = 100;
                double minY = -100;
                double maxY = 100;

                zoomGroup.setTranslateX(Math.max(minX, Math.min(newX, maxX)));
                zoomGroup.setTranslateY(Math.max(minY, Math.min(newY, maxY)));
            }
        });

        double centroX = (900 - 1000) / 2.0;
        double centroY = (700 - 1000) / 2.0;

        zoomGroup.setTranslateX(centroX);
        zoomGroup.setTranslateY(centroY);

        Pane root = new Pane();
        root.getChildren().add(scrollPane);

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Catan");
        Label turnoLabel = new Label();
        Label dadoLabel = new Label("Resultado: -");
        Button botonDado = new Button("Tirar dado");

        // Posiciones
        turnoLabel.setLayoutX(20);
        turnoLabel.setLayoutY(20);

        dadoLabel.setLayoutX(20);
        dadoLabel.setLayoutY(50);

        botonDado.setLayoutX(20);
        botonDado.setLayoutY(80);

        // Estilo
        turnoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        dadoLabel.setStyle("-fx-text-fill: white;");

        // Inicializar turno
        if (gestor.obtenerTurnoActual() != null) {
            turnoLabel.setText("Turno de: " + gestor.obtenerTurnoActual().getNombre());
        } else {
            turnoLabel.setText("Turno de: -");
        }

        // Dado
        Dado dado = new Dado();

        // Acción botón
        botonDado.setOnAction(e -> {
            int resultado = dado.lanzar();
            dadoLabel.setText("Resultado: " + resultado);

            gestor.pasarTurno();

            if (gestor.obtenerTurnoActual() != null) {
                turnoLabel.setText("Turno de: " + gestor.obtenerTurnoActual().getNombre());
            }
        });

        // Agregar a pantalla
        root.getChildren().addAll(turnoLabel, dadoLabel, botonDado);
        stage.setScene(scene);

        stage.show();
    }
}
