package ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import model.Dado;
import model.GestorTurnos;
import model.Jugador;
import model.*;

import java.util.*;

public class JuegoView {

    private Stage stage;
    private GestorTurnos gestor;
    private MapaVisual mapaVisual;
    private List<Vertice> listaVertices;
    private List<Arista> listaAristas;
    //Fase inicial del juego
    private enum FaseJuego { INICIAL, NORMAL }
    private FaseJuego faseActual = FaseJuego.INICIAL;
    private int construccionesIniciales = 0;
    private boolean aldeaInicialPuesta = false;
    //Estado de construccion
    enum ModoConstruccion { NINGUNO, ALDEA, CIUDAD, CAMINO }
    private ModoConstruccion modoActual = ModoConstruccion.NINGUNO;
    //Manejar direccion turnos
    private boolean rondaInversa = false;
    //Nuevo
    private Map<Recurso, Label> labelsRecursos = new HashMap<>();
    private Map<Recurso, HBox> filasRecursos = new HashMap<>();
    private VBox inventarioPanel;

    private final String estiloBotonConstruir =
            "-fx-background-color: #1A0F0A;" +
                    "-fx-text-fill: #D4A843;" +
                    "-fx-font-family: 'Georgia';" +
                    "-fx-font-size: 12px;" +
                    "-fx-border-color: #D4A843;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 4;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 6 10;" +
                    "-fx-cursor: hand;";

    private final String estiloBotonActivo =
            "-fx-background-color: #D4A843;" +
                    "-fx-text-fill: #1A0F0A;" +
                    "-fx-font-family: 'Georgia';" +
                    "-fx-font-size: 12px;" +
                    "-fx-border-color: #D4A843;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 4;" +
                    "-fx-background-radius: 4;" +
                    "-fx-padding: 6 10;" +
                    "-fx-cursor: hand;";

    // Botones
    private Button btnAldea;
    private Button btnCiudad;
    private Button btnCamino;
    private Button botonDado;
    private Button botonFinTurno;
    private Label labelFase;

    public JuegoView(Stage stage, GestorTurnos gestor) {
        this.stage = stage;
        this.gestor = gestor;
    }

    public interface CallbackConstruccion {
        void onAldeaColocada();
        void onCaminoColocado();
        void onConstruccionRealizada();
    }

    public void iniciar() {
        System.out.println("Jugador actual: " + gestor.obtenerTurnoActual());
        Pane mapPane = new Pane();
        MapaCatan logicaCatan = new MapaCatan();
        this.mapaVisual = new MapaVisual(mapPane, gestor);

        botonDado = new Button("Tirar dados");
        botonFinTurno = new Button("Finalizar turno");
        btnAldea = new Button("Aldea");
        btnCiudad = new Button("Ciudad");
        btnCamino = new Button("Camino");

        btnAldea.setStyle(estiloBotonConstruir);
        btnCiudad.setStyle(estiloBotonConstruir);
        btnCamino.setStyle(estiloBotonConstruir);

        //Indica si es fase inicial
        labelFase = new Label("Fase inicial\nColoca tu aldea");
        labelFase.setText("Fase inicial\n" +
                this.gestor.obtenerTurnoActual().getNombre() +
                ": coloca tu aldea");
        labelFase.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 12px;" +
                        "-fx-text-alignment: center;"
        );

        botonDado.setDisable(true);
        btnCiudad.setDisable(true);
        btnAldea.setDisable(false);
        btnCamino.setDisable(true);
        botonFinTurno.setDisable(true);

        this.mapaVisual.setCallback(new CallbackConstruccion() {
            @Override
            public void onAldeaColocada() {
                aldeaInicialPuesta = true;
                labelFase.setText("Ahora coloca\ntu camino");
                btnAldea.setDisable(true);
                btnCamino.setDisable(false);
                btnCamino.setStyle(estiloBotonActivo);
                mapaVisual.setModo(ModoConstruccion.CAMINO);
                modoActual = ModoConstruccion.CAMINO;
                btnCamino.setStyle(estiloBotonActivo);
            }

            @Override
            public void onCaminoColocado() {
                labelFase.setText("Turno completado\nFinaliza el turno");
                btnCamino.setDisable(true);
                mapaVisual.setModo(ModoConstruccion.NINGUNO);
                modoActual = ModoConstruccion.NINGUNO;
                botonFinTurno.setDisable(false);
            }
            
            @Override
            public void onConstruccionRealizada() {
                actualizarInventario(); // 🔥 ESTA ES LA CLAVE
            }
        });

        mapPane.setPrefSize(1000, 1000);

        List<Hexagono> hexagonos = new ArrayList<>(logicaCatan.getMapa().values());
        Set<Vertice> setVertices = new HashSet<>();
        Set<Arista> setAristas = new HashSet<>();

        for (Hexagono hex : hexagonos) {
            setVertices.addAll(hex.getVertices());
            setAristas.addAll(hex.getAristas());
        }

        this.listaVertices = new ArrayList<>(setVertices);
        this.listaAristas = new ArrayList<>(setAristas);

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

        // --- PANEL DE TURNO Y DADO ---//
        VBox panelLateral = new VBox(12);
        panelLateral.setLayoutX(710);
        panelLateral.setLayoutY(15);
        panelLateral.setPrefWidth(175);
        panelLateral.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.92);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 12;"
        );

        Label tituloPanel = new Label("Jugadores");

        tituloPanel.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        // --- TARJETAS DE JUGADORES ---
        String[] coloresJugador = {"#C0392B", "#2471A3", "#1E8449", "#D68910"};
        List<Label> tarjetasJugadores = new ArrayList<>();
        List<String> nombresJugadores = new ArrayList<>();

        Jugador jugadorActualTemp = this.gestor.obtenerTurnoActual();
        for (int i = 0; i < 4; i++) {
            nombresJugadores.add(this.gestor.obtenerTurnoActual().getNombre());
            this.gestor.pasarTurno();
        }

        VBox tarjetasBox = new VBox(8);
        for (int i = 0; i < nombresJugadores.size(); i++) {
            Label tarjeta = new Label(nombresJugadores.get(i));
            tarjeta.setPrefWidth(151);
            tarjeta.setStyle(
                    "-fx-background-color: #1A0F0A;" +
                            "-fx-text-fill: " + coloresJugador[i] + ";" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: " + coloresJugador[i] + ";" +
                            "-fx-border-width: 1.5;" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 6 10;"
            );
            tarjetasJugadores.add(tarjeta);
            tarjetasBox.getChildren().add(tarjeta);
        }

        // Iluminar el primero al inicio
        actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                this.gestor.obtenerTurnoActual().getNombre(), coloresJugador);

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.setStyle("-fx-background-color: #D4A843;");

        // --- DADOS GRÁFICOS ---
        Label tituloDados = new Label("Dados");
        tituloDados.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        HBox dadosBox = new HBox(10);
        dadosBox.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.canvas.Canvas canvasDado1 = new javafx.scene.canvas.Canvas(55, 55);
        javafx.scene.canvas.Canvas canvasDado2 = new javafx.scene.canvas.Canvas(55, 55);
        dibujarDado(canvasDado1, 1);
        dibujarDado(canvasDado2, 1);

        dadosBox.getChildren().addAll(canvasDado1, canvasDado2);

        Label totalLabel = new Label("Total: —");
        totalLabel.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;"
        );

        botonDado.setMaxWidth(Double.MAX_VALUE);
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
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;"
        );
        botonDado.setOnMouseEntered(e -> botonDado.setStyle(
                "-fx-background-color: #A52020;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;"
        ));
        botonDado.setOnMouseExited(e -> botonDado.setStyle(
                "-fx-background-color: #8B1A1A;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;"
        ));

        // --- ACCIÓN DEL BOTÓN ---
        Dado dado = new Dado();
        botonDado.setOnAction(e -> {
            int resultado = dado.lanzar();
            dibujarDado(canvasDado1, dado.getDado1());
            dibujarDado(canvasDado2, dado.getDado2());
            totalLabel.setText("Total: " + resultado);
            logicaCatan.producirRecursos(resultado);
            
            actualizarInventario();//Nuevo

            botonDado.setDisable(true);
            botonFinTurno.setDisable(false);
        });

        botonFinTurno.setMaxWidth(Double.MAX_VALUE);
        botonFinTurno.setDisable(true); // empieza deshabilitado
        botonFinTurno.setStyle(
                "-fx-background-color: #1A4A1A;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;" +
                        "-fx-opacity: 0.5;"
        );

        // TODO: En fase inicial, validar que no se puede construir aldea
        // entre dos carreteras enemigas (regla oficial de Catan pendiente)

        botonFinTurno.setOnAction(e -> {
            if (faseActual == FaseJuego.INICIAL) {
                if (!aldeaInicialPuesta) {
                    labelFase.setText("Debes colocar\ntu aldea primero");
                    return;
                }
                aldeaInicialPuesta = false;

                if (!rondaInversa) {
                    if (this.gestor.esUltimoJugador()) {
                        rondaInversa = true;
                        labelFase.setText("Fase inicial - Ronda 2\n" +
                                this.gestor.obtenerTurnoActual().getNombre() +
                                ": coloca tu aldea");
                    } else {
                        this.gestor.pasarTurno();
                        actualizarInventario();
                        actualizarColorInventario();
                        actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                                this.gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                        labelFase.setText("Fase inicial\n" +
                                this.gestor.obtenerTurnoActual().getNombre() +
                                ": coloca tu aldea");
                    }
                } else {
                    if (this.gestor.esPrimerJugador()) {
                        faseActual = FaseJuego.NORMAL;
                        labelFase.setText("¡Fase normal!");
                        habilitarBotonesFaseNormal(btnAldea, btnCiudad, btnCamino, botonDado);
                        mapaVisual.setFaseInicial(false);
                        return;
                    } else {
                        this.gestor.pasarTurnoReversa();
                        actualizarInventario();
                        actualizarColorInventario();
                        actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                                this.gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                        labelFase.setText("Fase inicial - Ronda 2\n" +
                                this.gestor.obtenerTurnoActual().getNombre() +
                                ": coloca tu aldea");
                    }
                }

                // Resetear para siguiente jugador
                btnAldea.setDisable(false);
                btnCamino.setDisable(true);
                botonFinTurno.setDisable(true);
                btnAldea.setStyle(estiloBotonActivo);
                btnCamino.setStyle(estiloBotonConstruir);
                modoActual = ModoConstruccion.ALDEA;
                mapaVisual.setModo(ModoConstruccion.ALDEA);

            } else {
                // Turno normal
                this.gestor.pasarTurno();
                actualizarInventario();
                actualizarColorInventario();
                actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                    this.gestor.obtenerTurnoActual().getNombre(), coloresJugador);

                totalLabel.setText("Total: —");
                dibujarDado(canvasDado1, 1);
                dibujarDado(canvasDado2, 1);
                botonDado.setDisable(false);
                botonFinTurno.setDisable(true);
                botonFinTurno.setStyle(
                    "-fx-background-color: #1A4A1A;" +
                            "-fx-text-fill: #D4A843;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: #D4A843;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 8 14;" +
                            "-fx-cursor: hand;" +
                            "-fx-opacity: 0.5;"
                );
                modoActual = ModoConstruccion.NINGUNO;
                mapaVisual.setModo(ModoConstruccion.NINGUNO);
                btnAldea.setDisable(false);
                btnCiudad.setDisable(false);
                btnCamino.setDisable(false);
                btnAldea.setStyle(estiloBotonConstruir);
                btnCiudad.setStyle(estiloBotonConstruir);
                btnCamino.setStyle(estiloBotonConstruir);
            }
        });

        botonFinTurno.setOnMouseEntered(e -> {
            if (!botonFinTurno.isDisabled()) botonFinTurno.setStyle(
                    "-fx-background-color: #2E6B2E;" +
                            "-fx-text-fill: #D4A843;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: #D4A843;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 8 14;" +
                            "-fx-cursor: hand;"
            );
        });
        botonFinTurno.setOnMouseExited(e -> {
            if (!botonFinTurno.isDisabled()) botonFinTurno.setStyle(
                    "-fx-background-color: #1A4A1A;" +
                            "-fx-text-fill: #D4A843;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: #D4A843;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 8 14;" +
                            "-fx-cursor: hand;"
            );
        });

        // --- SECCIÓN CONSTRUCCIÓN ---
        javafx.scene.control.Separator sep2 = new javafx.scene.control.Separator();
        sep2.setStyle("-fx-background-color: #D4A843;");

        Label tituloConstruccion = new Label("⚒  Construir");
        tituloConstruccion.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

// Acciones de los botones de construcción
        btnAldea.setOnAction(e -> {
            if (modoActual == ModoConstruccion.ALDEA) {
                modoActual = ModoConstruccion.NINGUNO;
                btnAldea.setStyle(estiloBotonConstruir);
            } else {
                modoActual = ModoConstruccion.ALDEA;
                btnAldea.setStyle(estiloBotonActivo);
                btnCiudad.setStyle(estiloBotonConstruir);
                btnCamino.setStyle(estiloBotonConstruir);
            }
            mapaVisual.setModo(modoActual);
        });

        btnCiudad.setOnAction(e -> {
            if (modoActual == ModoConstruccion.CIUDAD) {
                modoActual = ModoConstruccion.NINGUNO;
                btnCiudad.setStyle(estiloBotonConstruir);
            } else {
                modoActual = ModoConstruccion.CIUDAD;
                btnCiudad.setStyle(estiloBotonActivo);
                btnAldea.setStyle(estiloBotonConstruir);
                btnCamino.setStyle(estiloBotonConstruir);
            }
            mapaVisual.setModo(modoActual);
        });

        btnCamino.setOnAction(e -> {
            if (modoActual == ModoConstruccion.CAMINO) {
                modoActual = ModoConstruccion.NINGUNO;
                btnCamino.setStyle(estiloBotonConstruir);
            } else {
                modoActual = ModoConstruccion.CAMINO;
                btnCamino.setStyle(estiloBotonActivo);
                btnAldea.setStyle(estiloBotonConstruir);
                btnCiudad.setStyle(estiloBotonConstruir);
            }
            mapaVisual.setModo(modoActual);
        });

        panelLateral.getChildren().addAll(sep2, tituloConstruccion, btnAldea, btnCiudad, btnCamino);

        panelLateral.getChildren().addAll(
                tituloPanel, tarjetasBox, sep, tituloDados, dadosBox, totalLabel, botonDado, botonFinTurno
        );
        root.getChildren().add(panelLateral);
        
        inventarioPanel = crearPanelInventario();
        root.getChildren().add(inventarioPanel);
        actualizarInventario();
        actualizarColorInventario();

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Catan");
        stage.setScene(scene);
        crearPanelInfo(root);
        stage.show();
    }

    private void actualizarTarjetas(List<Label> tarjetas, List<String> nombres,
                                    String nombreActual, String[] colores) {
        for (int i = 0; i < tarjetas.size(); i++) {
            boolean esTurno = nombres.get(i).equals(nombreActual);
            tarjetas.get(i).setStyle(
                    "-fx-background-color: " + (esTurno ? colores[i] : "#1A0F0A") + ";" +
                            "-fx-text-fill: " + (esTurno ? "#1A0F0A" : colores[i]) + ";" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: " + colores[i] + ";" +
                            "-fx-border-width: " + (esTurno ? "2.5" : "1.5") + ";" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 6 10;"
            );
        }
    }

    private void dibujarDado(javafx.scene.canvas.Canvas canvas, int valor) {
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        // Limpiar
        gc.clearRect(0, 0, w, h);

        // Fondo del dado
        gc.setFill(javafx.scene.paint.Color.web("#F5E6C8"));
        gc.fillRoundRect(3, 3, w - 6, h - 6, 10, 10);

        // Borde
        gc.setStroke(javafx.scene.paint.Color.web("#8B4513"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(3, 3, w - 6, h - 6, 10, 10);

        // Puntos
        gc.setFill(javafx.scene.paint.Color.web("#2C1810"));
        double r = 5; // radio del punto
        double c = w / 2; // centro
        double[][] posiciones = getPosicionesPuntos(valor, w, h);
        for (double[] pos : posiciones) {
            gc.fillOval(pos[0] - r, pos[1] - r, r * 2, r * 2);
        }
    }

    private double[][] getPosicionesPuntos(int valor, double w, double h) {
        double m = 14; // margen
        double c = w / 2;
        double cm = h / 2;
        switch (valor) {
            case 1: return new double[][]{{c, cm}};
            case 2: return new double[][]{{m, m}, {w-m, h-m}};
            case 3: return new double[][]{{m, m}, {c, cm}, {w-m, h-m}};
            case 4: return new double[][]{{m, m}, {w-m, m}, {m, h-m}, {w-m, h-m}};
            case 5: return new double[][]{{m, m}, {w-m, m}, {c, cm}, {m, h-m}, {w-m, h-m}};
            case 6: return new double[][]{{m, m}, {w-m, m}, {m, cm}, {w-m, cm}, {m, h-m}, {w-m, h-m}};
            default: return new double[][]{};
        }
    }

    private void habilitarBotonesFaseNormal(Button btnAldea, Button btnCiudad,
                                            Button btnCamino, Button botonDado) {
        btnAldea.setDisable(false);
        btnCiudad.setDisable(false);
        btnCamino.setDisable(false);
        botonDado.setDisable(false);
        botonFinTurno.setDisable(true);

        // Resetear estilos
        btnAldea.setStyle(estiloBotonConstruir);
        btnCiudad.setStyle(estiloBotonConstruir);
        btnCamino.setStyle(estiloBotonConstruir);

        // Resetear modo
        modoActual = ModoConstruccion.NINGUNO;
        mapaVisual.setModo(ModoConstruccion.NINGUNO);

        labelFase.setText("¡Fase normal!\nTira los dados");
    }

    private void crearPanelInfo(Pane root) {
        Button btnInfo = new Button("?");
        btnInfo.setLayoutX(15);  // ← izquierda
        btnInfo.setLayoutY(650); // ← abajo del panel lateral
        btnInfo.setStyle(
                "-fx-background-color: #8B1A1A;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-min-width: 32;" +
                        "-fx-min-height: 32;" +
                        "-fx-cursor: hand;"
        );

        VBox panelInfo = new VBox(8);
        panelInfo.setLayoutX(15);
        panelInfo.setLayoutY(15);
        panelInfo.setPrefWidth(340);
        panelInfo.setVisible(false);
        panelInfo.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 12;"
        );

        Label titulo = new Label("Terrenos y recursos");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        // Cabecera
        HBox cabecera = new HBox(0);
        cabecera.setPrefWidth(316);
        Label colTerreno = new Label("Terreno");
        colTerreno.setPrefWidth(158);
        colTerreno.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia'; -fx-font-size: 11px; -fx-font-weight: bold;");
        Label colRecurso = new Label("Recurso");
        colRecurso.setPrefWidth(158);
        colRecurso.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia'; -fx-font-size: 11px; -fx-font-weight: bold;");
        cabecera.getChildren().addAll(colTerreno, colRecurso);

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();

        // Filas terreno + recurso
        String[][] filas = {
                {"Bosque",   "/bosqueCatan.png",   "Madera",   "/maderaCatan.png"},
                {"Colina",   "/colinaCatan.png",   "Ladrillo", "/ladrilloCatan.png"},
                {"Campo",    "/campoCatan.png",    "Trigo",    "/trigoCatan.png"},
                {"Pasto",    "/pastoCatan.png",    "Oveja",    "/ovejaCatan.png"},
                {"Montaña",  "/montanaCatan.png",  "Piedra",   "/piedraCatan.png"},
                {"Desierto", "/desiertoCatan.png", "—",        null}
        };

        panelInfo.getChildren().addAll(titulo, sep, cabecera);

        for (String[] fila : filas) {
            HBox filaCelda = new HBox(0);

            VBox celdaTerreno = crearCeldaInfo(fila[0], fila[1]);
            celdaTerreno.setPrefWidth(158);

            VBox celdaRecurso = crearCeldaInfo(fila[2], fila[3]);
            celdaRecurso.setPrefWidth(158);

            filaCelda.getChildren().addAll(celdaTerreno, celdaRecurso);
            panelInfo.getChildren().add(filaCelda);
        }

        btnInfo.setOnAction(e -> panelInfo.setVisible(!panelInfo.isVisible()));

        root.getChildren().addAll(panelInfo, btnInfo);
    }

    private VBox crearCeldaInfo(String nombre, String rutaImagen) {
        VBox celda = new VBox(4);
        celda.setAlignment(javafx.geometry.Pos.CENTER);
        celda.setPadding(new javafx.geometry.Insets(4));

        if (rutaImagen != null) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                        getClass().getResourceAsStream(rutaImagen)
                );
                javafx.scene.image.ImageView imgView = new javafx.scene.image.ImageView(img);
                imgView.setFitWidth(55);
                imgView.setFitHeight(55);
                imgView.setPreserveRatio(true);
                celda.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println("Error cargando: " + rutaImagen);
            }
        }

        Label label = new Label(nombre);
        label.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 11px;"
        );
        celda.getChildren().add(label);

        return celda;
    }
    
    //Nuevo
    private VBox crearPanelInventario() {
        VBox panel = new VBox(8);
        panel.setLayoutX(15);
        panel.setLayoutY(160);
        panel.setPrefWidth(175);

        panel.setStyle(
            "-fx-background-color: rgba(44, 24, 16, 0.92);" +
            "-fx-border-color: #D4A843;" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;"
        );

        Label titulo = new Label("Recursos");
        titulo.setStyle(
            "-fx-text-fill: #D4A843;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );

        panel.getChildren().add(titulo);

        for (Recurso r : Recurso.values()) {

    //  Imagen del recurso
            Image img = new Image(getClass().getResourceAsStream(obtenerRutaRecurso(r)));
            ImageView icono = new ImageView(img);
            icono.setFitWidth(30);
            icono.setFitHeight(30);

    //  Cantidad
            Label cantidad = new Label("0");
            cantidad.setStyle(
                "-fx-text-fill: #D4A843;" +
                "-fx-font-family: 'Georgia';" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
            );

            labelsRecursos.put(r, cantidad); // 🔥 CLAVE

    // 🔹 Fila: imagen + número
            HBox fila = new HBox(10, icono, cantidad);
            fila.setAlignment(Pos.CENTER_LEFT);

            panel.getChildren().add(fila);
        }
        return panel ;
    }
    
    private String obtenerRutaRecurso(Recurso r) {
        switch (r) {
            case MADERA: return "/maderaCatan.png";
            case LADRILLO: return "/ladrilloCatan.png";
            case TRIGO: return "/trigoCatan.png";
            case OVEJA: return "/ovejaCatan.png";
            case PIEDRA: return "/piedraCatan.png";
            default: return null;
        }
    }
    
    private void actualizarInventario() {
    Jugador jugador = gestor.obtenerTurnoActual();
    if (jugador == null) return;

    for (Recurso r : Recurso.values()) {
        int cantidad = jugador.getRecursos().getOrDefault(r, 0);

        Label label = labelsRecursos.get(r);

        if (label != null) {  // 🔥 ESTO ES CLAVE
            label.setText(String.valueOf(cantidad));
        } else {
            System.out.println("Label null para recurso: " + r);
        }
        // controlar botones según recursos
        if (faseActual == FaseJuego.NORMAL) {
            btnAldea.setDisable(!jugador.tieneRecursos(Aldea.COSTO));
            btnCiudad.setDisable(!jugador.tieneRecursos(Ciudad.COSTO));
            btnCamino.setDisable(!jugador.tieneRecursos(Carretera.COSTO));
        }
    }
}
    
    private String obtenerColorJugadorHex(Jugador jugador) {
        if (jugador == null) return "#D4A843";

        switch (jugador.getColor().toLowerCase()) {
            case "rojo": return "#C0392B";
            case "azul": return "#2471A3";
            case "verde": return "#1E8449";
            case "naranja": return "#D68910";
            default: return "#D4A843";
        }
    }
    
    private void actualizarColorInventario() {
        Jugador jugador = gestor.obtenerTurnoActual();
        String color = obtenerColorJugadorHex(jugador);

        inventarioPanel.setStyle(
            "-fx-background-color: rgba(44, 24, 16, 0.92);" +
            "-fx-border-color: " + color + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;"
        );
    }
}
