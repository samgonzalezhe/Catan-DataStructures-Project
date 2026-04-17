package ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.Dado;
import logic.GestorTurnos;
import model.*;

import java.util.*;

public class JuegoView {

    private Stage stage;
    private GestorTurnos gestor;

    public JuegoView(Stage stage, GestorTurnos gestor) {
        this.stage = stage;
        this.gestor = gestor;
    }

    public void iniciar() {
        System.out.println("Jugador actual: " + gestor.obtenerTurnoActual());
        Pane mapPane = new Pane();
        MapaCatan logicaCatan = new MapaCatan();
        GestorTurnos gestor = new GestorTurnos();
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
            double xPromedio = v.getTilesAdyacentes().stream()
                    .mapToDouble(t -> TableroUtils.hexToPixel(t.coord)[0]).average().orElse(0);
            double yPromedio = v.getTilesAdyacentes().stream()
                    .mapToDouble(t -> TableroUtils.hexToPixel(t.coord)[1]).average().orElse(0);
            v.setPosicionPixeles(xPromedio, yPromedio);
        }

        mapaVisual.renderizarMapa(hexagonos, listaVertices, listaAristas);

        // Envolver mapa en Group para zoom y pan
        Group zoomGroup = new Group(mapPane);

        // Centrar el mapa al inicio
        double centroX = (900 - 1000) / 2.0;
        double centroY = (700 - 1000) / 2.0;
        zoomGroup.setTranslateX(centroX);
        zoomGroup.setTranslateY(centroY);

        // Contenedor principal sin ScrollPane
        Pane root = new Pane(zoomGroup);
        root.setStyle("-fx-background-color: #2c3e50;");
        root.setClip(new javafx.scene.shape.Rectangle(900, 700));

        // ZOOM con scroll del ratón
        zoomGroup.setOnScroll(event -> {
            event.consume();

            double scaleFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;
            double oldScale = zoomGroup.getScaleX();
            double newScale = Math.max(0.5, Math.min(oldScale * scaleFactor, 3.0));

            zoomGroup.setScaleX(newScale);
            zoomGroup.setScaleY(newScale);

            double f = (newScale / oldScale) - 1;
            double dx = (event.getX() - (zoomGroup.getBoundsInParent().getWidth() / 2
                    + zoomGroup.getTranslateX())) * f;
            double dy = (event.getY() - (zoomGroup.getBoundsInParent().getHeight() / 2
                    + zoomGroup.getTranslateY())) * f;

            zoomGroup.setTranslateX(zoomGroup.getTranslateX() - dx);
            zoomGroup.setTranslateY(zoomGroup.getTranslateY() - dy);
        });

        // PAN con clic derecho y arrastre
        final double[] dragStart = new double[2];

        zoomGroup.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) {
                dragStart[0] = event.getSceneX() - zoomGroup.getTranslateX();
                dragStart[1] = event.getSceneY() - zoomGroup.getTranslateY();
            }
        });

        zoomGroup.setOnMouseDragged(event -> {
            if (event.isSecondaryButtonDown()) {
                double newX = event.getSceneX() - dragStart[0];
                double newY = event.getSceneY() - dragStart[1];

                // Límites generosos para no perder el mapa de vista
                zoomGroup.setTranslateX(Math.max(-600, Math.min(newX, 600)));
                zoomGroup.setTranslateY(Math.max(-600, Math.min(newY, 600)));
            }
        });

        // --- PANEL DE TURNO Y DADO (esquina superior izquierda) ---
        VBox panel = new VBox(10);
        panel.setLayoutX(15);
        panel.setLayoutY(15);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.85);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 12 16;"
        );

        Label turnoLabel = new Label();
        turnoLabel.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-weight: bold;"
        );

        Label dadoLabel = new Label("Dado: —");
        dadoLabel.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'Georgia';"
        );

        Button botonDado = new Button("⚄  Tirar dado");
        botonDado.setStyle(
                "-fx-background-color: #8B1A1A;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 6 14;" +
                        "-fx-cursor: hand;"
        );

        // Hover del botón
        botonDado.setOnMouseEntered(e ->
                botonDado.setStyle(
                        "-fx-background-color: #A52020;" +
                                "-fx-text-fill: #D4A843;" +
                                "-fx-font-family: 'Georgia';" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: #D4A843;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;" +
                                "-fx-padding: 6 14;" +
                                "-fx-cursor: hand;"
                )
        );
        botonDado.setOnMouseExited(e ->
                botonDado.setStyle(
                        "-fx-background-color: #8B1A1A;" +
                                "-fx-text-fill: #D4A843;" +
                                "-fx-font-family: 'Georgia';" +
                                "-fx-font-size: 13px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: #D4A843;" +
                                "-fx-border-width: 1;" +
                                "-fx-border-radius: 4;" +
                                "-fx-background-radius: 4;" +
                                "-fx-padding: 6 14;" +
                                "-fx-cursor: hand;"
                )
        );

        // Inicializar turno
        turnoLabel.setText(gestor.obtenerTurnoActual() != null
                ? "Turno: " + gestor.obtenerTurnoActual().getNombre()
                : "Turno: —");

        // Acción del dado
        Dado dado = new Dado();
        botonDado.setOnAction(e -> {
            int resultado = dado.lanzar();
            dadoLabel.setText("Dado: " + resultado);
            logicaCatan.producirRecursos(resultado);
            gestor.pasarTurno();
            turnoLabel.setText(gestor.obtenerTurnoActual() != null
                    ? "Turno: " + gestor.obtenerTurnoActual().getNombre()
                    : "Turno: —");
        });

        panel.getChildren().addAll(turnoLabel, dadoLabel, botonDado);
        root.getChildren().add(panel);


        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Catan");
        stage.setScene(scene);
        stage.show();
    }
}
