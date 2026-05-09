package ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import logic.*;
import logic.bot.BotJugador;
import model.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import structures.ArregloDinamico;

import java.awt.*;
import java.util.*;
import java.util.List;

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

    //Bots
    private List<Boolean> esBots;
    private List<BotJugador> bots = new ArrayList<>();

    private MapaCatan logicaCatan;

    private Map<Recurso, Label> labelsRecursos = new HashMap<>();
    private Map<Recurso, HBox> filasRecursos = new HashMap<>();
    private VBox inventarioPanel;
    private Label bannerRecursos;
    private Label totalLabel;
    private javafx.scene.canvas.Canvas canvasDado1;
    private javafx.scene.canvas.Canvas canvasDado2;
    private List<Label> tarjetasJugadores;
    private List<String> nombresJugadores;
    private final String[] coloresJugador = {"#C0392B", "#2471A3", "#1E8449", "#D68910"};


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
    private Button botonVerCartas;
    private Button btnComprarCarta;
    private Button btnIntercambio;
    private Label labelFase;

    public JuegoView(Stage stage, GestorTurnos gestor, List<Boolean> esBots) {
        this.stage = stage;
        this.gestor = gestor;
        this.esBots = esBots;
    }

    public interface CallbackConstruccion {
        void onAldeaColocada();
        void onCaminoColocado();
        void onConstruccionRealizada();
    }

    public void iniciar() {
        System.out.println("Jugador actual: " + gestor.obtenerTurnoActual());
        Pane mapPane = new Pane();
        this.logicaCatan = new MapaCatan();
        this.mapaVisual = new MapaVisual(mapPane, gestor);

        botonDado = new Button("Tirar dados");
        botonFinTurno = new Button("Finalizar turno");
        btnAldea = new Button("Aldea");
        btnCiudad = new Button("Ciudad");
        btnCamino = new Button("Camino");
        botonVerCartas = new Button("Ver mis cartas");

        this.mapaVisual.setLogicaCatan(this.logicaCatan);
        this.mapaVisual.setCallbackVictoria(ganador -> mostrarPantallaVictoria(ganador));
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

        // Crear bots para los jugadores que lo sean
        List<Jugador> todosJugadores = obtenerListaJugadores();
        for (int i = 0; i < 4; i++) {
            if (esBots.get(i)) {
                BotJugador bot = new BotJugador(
                        todosJugadores.get(i),
                        this.logicaCatan,
                        this.gestor,
                        this.listaVertices,
                        this.listaAristas
                );
                bots.add(bot);
                System.out.println("Bot creado para: " + todosJugadores.get(i).getNombre());
            }
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

        // --- PANEL IZQUIERDO (Turno + Dados) ---
        VBox panelIzquierdo = new VBox(12);
        panelIzquierdo.setLayoutX(15);
        panelIzquierdo.setLayoutY(15);
        panelIzquierdo.setPrefWidth(175);
        panelIzquierdo.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.92);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 12;"
        );
        Label tituloPanel = new Label("⚔  Jugadores");
        tituloPanel.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

// --- PANEL DERECHO (Construir + Cartas) ---
        VBox panelDerecho = new VBox(12);
        panelDerecho.setLayoutX(710);
        panelDerecho.setLayoutY(15);
        panelDerecho.setPrefWidth(175);
        panelDerecho.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.92);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 12;"
        );


        // --- TARJETAS DE JUGADORES ---
        tarjetasJugadores = new ArrayList<>();
        nombresJugadores = new ArrayList<>();

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

        canvasDado1 = new javafx.scene.canvas.Canvas(55, 55);
        canvasDado2 = new javafx.scene.canvas.Canvas(55, 55);
        dibujarDado(canvasDado1, 1);
        dibujarDado(canvasDado2, 1);

        dadosBox.getChildren().addAll(canvasDado1, canvasDado2);

        totalLabel = new Label("Total: —");
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

        //Recursos tras tirar los dados
        bannerRecursos = new Label();
        bannerRecursos.setLayoutX(200);
        bannerRecursos.setLayoutY(660);
        bannerRecursos.setPrefWidth(500);
        bannerRecursos.setWrapText(true);
        bannerRecursos.setVisible(false);
        bannerRecursos.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.92);" +
                        "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 12px;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 6 10;"
        );
        root.getChildren().add(bannerRecursos);

        //MAZO DE DESARROLLO

        botonVerCartas.setMaxWidth(Double.MAX_VALUE);
        botonVerCartas.setStyle(estiloBotonConstruir);

        botonVerCartas.setOnAction(e -> mostrarPanelCartas());

        javafx.scene.control.Separator sep3 = new javafx.scene.control.Separator();
        Label tituloCartas = new Label("⚔  Desarrollo");
        tituloCartas.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        btnComprarCarta = new Button("Comprar carta");
        btnComprarCarta.setMaxWidth(Double.MAX_VALUE);
        btnComprarCarta.setStyle(estiloBotonConstruir);

        btnComprarCarta.setOnAction(e -> {
            Jugador jugador = gestor.obtenerTurnoActual();
            CartaDesarrollo carta = logicaCatan.comprarCartaDesarrollo(jugador);
            if (carta != null) {
                jugador.agregarCartaDesarrollo(carta);
                actualizarInventario();
                mostrarCartaObtenida(carta);
            }
        });



        // --- ACCIÓN DEL BOTÓN ---
        Dado dado = new Dado();
        botonDado.setOnAction(e -> {
            int resultado = dado.lanzar();
            dibujarDado(canvasDado1, dado.getDado1());
            dibujarDado(canvasDado2, dado.getDado2());
            totalLabel.setText("Total: " + resultado);


            if (resultado == 7) {
                // Activar modo ladrón
                activarModoLadron();
            } else {
                String resumen = logicaCatan.producirRecursos(resultado);
                logicaCatan.producirRecursos(resultado);
                actualizarInventario();
                botonDado.setDisable(true);
                botonFinTurno.setDisable(false);
                mostrarResumenRecursos(resumen, resultado);
            }

            btnAldea.setDisable(!gestor.obtenerTurnoActual().tieneRecursos(Aldea.COSTO));
            btnCiudad.setDisable(!gestor.obtenerTurnoActual().tieneRecursos(Ciudad.COSTO));
            btnCamino.setDisable(!gestor.obtenerTurnoActual().tieneRecursos(Carretera.COSTO));
            btnComprarCarta.setDisable(!gestor.obtenerTurnoActual().tieneRecursos(CartaDesarrollo.COSTO));
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

                if (obtenerBotActual() != null) {
                    deshabilitarControlesParaBot();
                    ejecutarFaseInicialBot();
                } else {
                    btnAldea.setDisable(false);
                    btnCamino.setDisable(true);
                    botonFinTurno.setDisable(true);
                    btnAldea.setStyle(estiloBotonActivo);
                    btnCamino.setStyle(estiloBotonConstruir);
                    modoActual = ModoConstruccion.ALDEA;
                    mapaVisual.setModo(ModoConstruccion.ALDEA);
                }

            } else {
                // Turno normal
                this.gestor.pasarTurno();
                actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                        this.gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                totalLabel.setText("Total: —");
                dibujarDado(canvasDado1, 1);
                dibujarDado(canvasDado2, 1);
                actualizarInventario();
                actualizarColorInventario();
                modoActual = ModoConstruccion.NINGUNO;
                mapaVisual.setModo(ModoConstruccion.NINGUNO);
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

                if (obtenerBotActual() != null) {
                    deshabilitarControlesParaBot();
                    ejecutarTurnoBot();
                } else {
                    btnAldea.setDisable(false);
                    btnCamino.setDisable(true);
                    botonFinTurno.setDisable(true);
                    btnAldea.setStyle(estiloBotonActivo);
                    btnCamino.setStyle(estiloBotonConstruir);
                    modoActual = ModoConstruccion.ALDEA;
                    mapaVisual.setModo(ModoConstruccion.ALDEA);
                }
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

        // Contenido panel izquierdo - agregar uno por uno
        panelIzquierdo.getChildren().add(tituloPanel);
        panelIzquierdo.getChildren().add(tarjetasBox);
        panelIzquierdo.getChildren().add(new javafx.scene.control.Separator());
        panelIzquierdo.getChildren().add(tituloDados);
        panelIzquierdo.getChildren().add(dadosBox);
        panelIzquierdo.getChildren().add(totalLabel);
        panelIzquierdo.getChildren().add(botonDado);
        panelIzquierdo.getChildren().add(botonFinTurno);
        panelIzquierdo.getChildren().add(labelFase);

// Contenido panel derecho - agregar uno por uno
        panelDerecho.getChildren().add(new javafx.scene.control.Separator());
        panelDerecho.getChildren().add(tituloConstruccion);
        panelDerecho.getChildren().add(btnAldea);
        panelDerecho.getChildren().add(btnCiudad);
        panelDerecho.getChildren().add(btnCamino);
        panelDerecho.getChildren().add(new javafx.scene.control.Separator());
        panelDerecho.getChildren().add(tituloCartas);
        panelDerecho.getChildren().add(btnComprarCarta);
        panelDerecho.getChildren().add(botonVerCartas);

        root.getChildren().addAll(panelIzquierdo, panelDerecho);

        //INTERCAMBIO
        javafx.scene.control.Separator sepIntercambio = new javafx.scene.control.Separator();
        Label tituloIntercambio = new Label("Intercambio");
        tituloIntercambio.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        btnIntercambio = new Button("Proponer intercambio");
        btnIntercambio.setMaxWidth(Double.MAX_VALUE);
        btnIntercambio.setStyle(estiloBotonConstruir);
        btnIntercambio.setOnAction(e -> mostrarSelectorJugador());

        panelDerecho.getChildren().addAll(sepIntercambio, tituloIntercambio, btnIntercambio);

        inventarioPanel = crearPanelInventario();
        root.getChildren().add(inventarioPanel);
        actualizarInventario();
        actualizarColorInventario();

        // Si el primer jugador es bot, iniciar fase inicial bot
        if (obtenerBotActual() != null) {
            javafx.application.Platform.runLater(() -> ejecutarFaseInicialBot());
        }

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Catan");
        stage.setScene(scene);
        crearPanelInfo(root);
        crearPanelCostos(root);
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
        mapaVisual.setFaseInicial(false);
        // Resetear modo
        modoActual = ModoConstruccion.NINGUNO;
        mapaVisual.setModo(ModoConstruccion.NINGUNO);
        labelFase.setText("¡Fase normal!\nTira los dados");

        if (obtenerBotActual() != null) {
            deshabilitarControlesParaBot();
            javafx.application.Platform.runLater(() -> ejecutarTurnoBot());
        } else {
            habilitarControlesParaHumano();
        }
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

    private void crearPanelCostos(Pane root) {
        Button btnCostos = new Button("$");
        btnCostos.setLayoutX(55); // ← al lado del botón "?"
        btnCostos.setLayoutY(650);
        btnCostos.setStyle(
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

        VBox panelCostos = new VBox(10);
        panelCostos.setLayoutX(55);
        panelCostos.setLayoutY(15);
        panelCostos.setPrefWidth(280);
        panelCostos.setVisible(false);
        panelCostos.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 14 12;"
        );

        Label titulo = new Label("Costos de construcción");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        panelCostos.getChildren().addAll(titulo, sep);

        // Construcciones con sus costos
        String[][] construcciones = {
                {"Aldea",     "1 Madera, 1 Ladrillo,\n1 Trigo, 1 Oveja"},
                {"Ciudad",    "2 Trigo, 3 Piedra"},
                {"Camino",    "1 Madera, 1 Ladrillo"},
                {"Carta Dev", "1 Trigo, 1 Oveja,\n1 Piedra"},
        };

        String[] iconos = {"🏠", "🏙", "🛣",  "🃏"};



        for (int i = 0; i < construcciones.length; i++) {
            javafx.scene.control.Separator sepCon = new javafx.scene.control.Separator();

            HBox fila = new HBox(10);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label icono = new Label(iconos[i]);
            icono.setStyle("-fx-font-size: 20px;");

            VBox info = new VBox(2);
            Label nombre = new Label(construcciones[i][0]);
            nombre.setStyle(
                    "-fx-text-fill: #D4A843;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;"
            );

            Label costo = new Label(construcciones[i][1]);
            costo.setStyle(
                    "-fx-text-fill: #C4956A;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 11px;"
            );

            info.getChildren().addAll(nombre, costo);
            fila.getChildren().addAll(icono, info);

            panelCostos.getChildren().addAll(fila, sepCon);
        }

        btnCostos.setOnAction(e -> panelCostos.setVisible(!panelCostos.isVisible()));
        root.getChildren().addAll(panelCostos, btnCostos);
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

    //OBLIGAR AL JUGADOR A USAR EL LADRON
    private void activarModoLadron() {
        labelFase.setText("¡7! Mueve\nel ladrón");
        mapaVisual.setModoLadron(true);

        btnAldea.setDisable(true);
        btnCiudad.setDisable(true);
        btnCamino.setDisable(true);
        botonDado.setDisable(true);
        botonFinTurno.setDisable(true);

        mapaVisual.resaltarHexagonosParaLadron(logicaCatan, gestor.obtenerTurnoActual(),
                hexSeleccionado -> {
                    // Mover ladrón y obtener mensaje de robo
                    String mensajeRobo = logicaCatan.moverLadronYRobar(
                            gestor.obtenerTurnoActual(), hexSeleccionado);

                    mapaVisual.setModoLadron(false);
                    mapaVisual.dibujarLadron(hexSeleccionado);
                    labelFase.setText("¡Fase normal!\nTira los dados");

                    // Mostrar banner
                    bannerRecursos.setText(mensajeRobo);
                    bannerRecursos.setVisible(true);
                    javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                            javafx.util.Duration.seconds(3)
                    );
                    pausa.setOnFinished(ev -> bannerRecursos.setVisible(false));
                    pausa.play();

                    actualizarInventario();

                    // Rehabilitar botones
                    btnAldea.setDisable(false);
                    btnCiudad.setDisable(false);
                    btnCamino.setDisable(false);
                    botonDado.setDisable(true);
                    botonFinTurno.setDisable(false);
                }
        );
    }

    private void mostrarPantallaVictoria(Jugador ganador) {
        // Deshabilitar todo
        btnAldea.setDisable(true);
        btnCiudad.setDisable(true);
        btnCamino.setDisable(true);
        botonDado.setDisable(true);
        botonFinTurno.setDisable(true);

        // Panel de victoria
        VBox panelVictoria = new VBox(20);
        panelVictoria.setAlignment(javafx.geometry.Pos.CENTER);
        panelVictoria.setLayoutX(200);
        panelVictoria.setLayoutY(200);
        panelVictoria.setPrefWidth(500);
        panelVictoria.setPrefHeight(300);
        panelVictoria.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 40;"
        );

        Label titulo = new Label("¡Victoria!");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 48px;" +
                        "-fx-font-weight: bold;"
        );

        Label nombreGanador = new Label(ganador.getNombre() + " ha ganado");
        nombreGanador.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 24px;"
        );

        Label puntos = new Label(logicaCatan.calcularPuntos(ganador) + " puntos de victoria");
        puntos.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 16px;"
        );

        Button btnNuevaPartida = new Button("Nueva partida");
        btnNuevaPartida.setStyle(
                "-fx-background-color: #8B1A1A;" +
                        "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 20;" +
                        "-fx-cursor: hand;"
        );
        btnNuevaPartida.setOnAction(e -> new MenuInicio(stage).mostrar());

        panelVictoria.getChildren().addAll(titulo, nombreGanador, puntos, btnNuevaPartida);

        // Obtener el root actual y agregar el panel
        Pane root = (Pane) stage.getScene().getRoot();
        root.getChildren().add(panelVictoria);
    }

    //Nuevo
    private VBox crearPanelInventario() {
        VBox panel = new VBox(8);
        panel.setLayoutX(710);
        panel.setLayoutY(420);
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

    private void mostrarCartaObtenida(CartaDesarrollo carta) {
        Pane root = (Pane) stage.getScene().getRoot();

        VBox popup = new VBox(12);
        popup.setAlignment(javafx.geometry.Pos.CENTER);
        popup.setLayoutX(300);
        popup.setLayoutY(150);
        popup.setPrefWidth(250);
        popup.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label("¡Carta obtenida!");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        // Imagen de la carta
        String rutaCarta = obtenerRutaCarta(carta);
        javafx.scene.image.ImageView imgCarta = new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(getClass().getResourceAsStream(rutaCarta))
        );
        imgCarta.setFitWidth(120);
        imgCarta.setFitHeight(180);
        imgCarta.setPreserveRatio(true);

        Label descripcion = new Label(carta.getDescripcion());
        descripcion.setWrapText(true);
        descripcion.setStyle(
                "-fx-text-fill: #C4956A;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 11px;" +
                        "-fx-text-alignment: center;"
        );

        Button btnCerrar = new Button("Aceptar");
        btnCerrar.setStyle(estiloBotonConstruir);
        btnCerrar.setOnAction(e -> root.getChildren().remove(popup));

        popup.getChildren().addAll(titulo, imgCarta, descripcion, btnCerrar);
        root.getChildren().add(popup);
    }

    private String obtenerRutaCarta(CartaDesarrollo carta) {
        if (carta instanceof CartaCaballero) return "/cartaCaballero.png";
        if (carta instanceof CartaPuntoVictoria) return "/cartaPuntoVictoria.png";
        if (carta instanceof CartaMonopolio) return "/cartaMonopolio.png";
        if (carta instanceof CartaConstruccionCaminos) return "/cartaConstruccionCaminos.png";
        return "/cartaReverso.png";
    }

    private void mostrarPanelCartas() {
        Pane root = (Pane) stage.getScene().getRoot();
        Jugador jugador = gestor.obtenerTurnoActual();
        List<CartaDesarrollo> cartas = jugador.getCartasDesarrollo();

        VBox panel = new VBox(10);
        panel.setLayoutX(200);
        panel.setLayoutY(100);
        panel.setPrefWidth(500);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label("Cartas de " + jugador.getNombre());
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        HBox filasCartas = new HBox(10);
        filasCartas.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        if (cartas.isEmpty()) {
            Label sinCartas = new Label("No tienes cartas de desarrollo");
            sinCartas.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia';");
            filasCartas.getChildren().add(sinCartas);
        } else {
            for (CartaDesarrollo carta : cartas) {
                if (!carta.isUsada()) {
                    VBox celdaCarta = crearCeldaCarta(carta, panel, root);
                    filasCartas.getChildren().add(celdaCarta);
                }
            }
        }

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle(estiloBotonConstruir);
        btnCerrar.setOnAction(e -> root.getChildren().remove(panel));

        panel.getChildren().addAll(titulo, filasCartas, btnCerrar);
        root.getChildren().add(panel);
    }

    private VBox crearCeldaCarta(CartaDesarrollo carta, VBox panel, Pane root) {
        VBox celda = new VBox(6);
        celda.setAlignment(javafx.geometry.Pos.CENTER);
        celda.setStyle(
                "-fx-background-color: #1A0F0A;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 6;"
        );

        javafx.scene.image.ImageView img = new javafx.scene.image.ImageView(
                new javafx.scene.image.Image(getClass().getResourceAsStream(obtenerRutaCarta(carta)))
        );
        img.setFitWidth(80);
        img.setFitHeight(120);
        img.setPreserveRatio(true);

        Button btnUsar = new Button("Usar");
        btnUsar.setStyle(estiloBotonConstruir);
        btnUsar.setOnAction(e -> {
            root.getChildren().remove(panel);
            usarCarta(carta);
        });

        celda.getChildren().addAll(img, btnUsar);
        return celda;
    }

    private void usarCarta(CartaDesarrollo carta) {
        Jugador jugador = gestor.obtenerTurnoActual();

        if (carta instanceof CartaCaballero) {
            // Activar modo ladrón igual que cuando sale 7
            activarModoLadron();
            carta.setUsada(true);

        } else if (carta instanceof CartaPuntoVictoria) {
            carta.realizarAccion(jugador, null);
            actualizarInventario();
            // Verificar victoria
            Jugador ganador = logicaCatan.verificarVictoria();
            if (ganador != null) mostrarPantallaVictoria(ganador);

        } else if (carta instanceof CartaMonopolio) {
            mostrarSelectorMonopolio(carta, jugador);

        } else if (carta instanceof CartaConstruccionCaminos) {
            carta.setUsada(true);
            activarModoConstruccionCaminos();
        }
    }

    private void mostrarSelectorMonopolio(CartaDesarrollo carta, Jugador jugador) {
        Pane root = (Pane) stage.getScene().getRoot();

        VBox panel = new VBox(12);
        panel.setLayoutX(300);
        panel.setLayoutY(200);
        panel.setPrefWidth(280);
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label("Monopolio\n¿Qué recurso eliges?");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-alignment: center;"
        );

        HBox botones = new HBox(8);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        for (Recurso r : Recurso.values()) {
            Button btnRecurso = new Button(r.toString());
            btnRecurso.setStyle(estiloBotonConstruir);
            btnRecurso.setOnAction(e -> {
                // Obtener todos los jugadores
                ArregloDinamico<Jugador> todos = obtenerTodosJugadores();
                ((CartaMonopolio) carta).realizarAccion(jugador, r, todos);
                actualizarInventario();
                root.getChildren().remove(panel);
            });
            botones.getChildren().add(btnRecurso);
        }

        panel.getChildren().addAll(titulo, botones);
        root.getChildren().add(panel);
    }

    private ArregloDinamico<Jugador> obtenerTodosJugadores() {
        ArregloDinamico<Jugador> todos = new ArregloDinamico<>(4);
        Jugador primero = gestor.obtenerTurnoActual();
        todos.append(primero);
        gestor.pasarTurno();
        while (gestor.obtenerTurnoActual() != primero) {
            todos.append(gestor.obtenerTurnoActual());
            gestor.pasarTurno();
        }
        return todos;
    }

    private void activarModoConstruccionCaminos() {
        // Contador de caminos gratis restantes
        labelFase.setText("Construye 2\ncaminos gratis");
        mapaVisual.setCaminosGratis(2);
        modoActual = ModoConstruccion.CAMINO;
        mapaVisual.setModo(ModoConstruccion.CAMINO);
        btnCamino.setStyle(estiloBotonActivo);
    }

    private void mostrarSelectorJugador() {
        Pane root = (Pane) stage.getScene().getRoot();
        Jugador oferente = gestor.obtenerTurnoActual();

        VBox panel = new VBox(12);
        panel.setLayoutX(250);
        panel.setLayoutY(180);
        panel.setPrefWidth(300);
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label("¿Con quién intercambiar?");
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        VBox botonesJugadores = new VBox(8);
        botonesJugadores.setAlignment(javafx.geometry.Pos.CENTER);

        // Obtener todos los jugadores menos el actual
        List<Jugador> todosJugadores = obtenerListaJugadores();
        for (Jugador receptor : todosJugadores) {
            if (receptor == oferente) continue;

            Button btnJugador = new Button(receptor.getNombre());
            btnJugador.setMaxWidth(Double.MAX_VALUE);
            btnJugador.setStyle(estiloBotonConstruir);
            btnJugador.setOnAction(e -> {
                root.getChildren().remove(panel);
                mostrarPanelIntercambio(oferente, receptor);
            });
            botonesJugadores.getChildren().add(btnJugador);
        }

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle(estiloBotonConstruir);
        btnCancelar.setMaxWidth(Double.MAX_VALUE);
        btnCancelar.setOnAction(e -> root.getChildren().remove(panel));

        panel.getChildren().addAll(titulo, botonesJugadores, btnCancelar);
        root.getChildren().add(panel);
    }

    private List<Jugador> obtenerListaJugadores() {
        List<Jugador> lista = new ArrayList<>();
        Jugador primero = gestor.obtenerTurnoActual();
        lista.add(primero);
        gestor.pasarTurno();
        while (gestor.obtenerTurnoActual() != primero) {
            lista.add(gestor.obtenerTurnoActual());
            gestor.pasarTurno();
        }
        return lista;
    }

    private void mostrarPanelIntercambio(Jugador oferente, Jugador receptor) {
        Pane root = (Pane) stage.getScene().getRoot();
        Intercambio intercambio = new Intercambio(oferente, receptor);

        VBox panel = new VBox(12);
        panel.setLayoutX(150);
        panel.setLayoutY(100);
        panel.setPrefWidth(500);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label(oferente.getNombre() + " → " + receptor.getNombre());
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        // Dos columnas: oferta y pedido
        HBox columnas = new HBox(20);
        columnas.setAlignment(javafx.geometry.Pos.CENTER);

        VBox colOferta = crearColumnaRecursos("Ofrezco", intercambio, true);
        VBox colPedido = crearColumnaRecursos("Pido a cambio", intercambio, false);

        columnas.getChildren().addAll(colOferta, colPedido);

        // Botones aceptar/rechazar
        HBox botones = new HBox(12);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnEnviar = new Button("Enviar propuesta");
        btnEnviar.setStyle(
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
        btnEnviar.setOnAction(e -> {
            root.getChildren().remove(panel);
            mostrarConfirmacionReceptor(intercambio, root);
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setStyle(estiloBotonConstruir);
        btnCancelar.setOnAction(e -> root.getChildren().remove(panel));

        botones.getChildren().addAll(btnEnviar, btnCancelar);
        panel.getChildren().addAll(titulo, columnas, botones);
        root.getChildren().add(panel);
    }

    private VBox crearColumnaRecursos(String titulo, Intercambio intercambio, boolean esOferta) {
        VBox col = new VBox(8);
        col.setAlignment(javafx.geometry.Pos.CENTER);
        col.setPrefWidth(210);
        col.setStyle(
                "-fx-background-color: #1A0F0A;" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 10;"
        );

        Label tituloCol = new Label(titulo);
        tituloCol.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );
        col.getChildren().add(tituloCol);

        for (Recurso r : Recurso.values()) {
            HBox fila = new HBox(8);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Imagen recurso
            javafx.scene.image.ImageView img = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(getClass().getResourceAsStream(obtenerRutaRecurso(r)))
            );
            img.setFitWidth(25);
            img.setFitHeight(25);

            Label nombreRecurso = new Label(r.toString());
            nombreRecurso.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia'; -fx-font-size: 11px;");
            nombreRecurso.setPrefWidth(60);

            // Spinner de cantidad
            javafx.scene.control.Spinner<Integer> spinner = new javafx.scene.control.Spinner<>(0, 10, 0);
            spinner.setPrefWidth(70);
            spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (esOferta) {
                    intercambio.agregarOferta(r, newVal);
                } else {
                    intercambio.agregarPedido(r, newVal);
                }
            });

            fila.getChildren().addAll(img, nombreRecurso, spinner);
            col.getChildren().add(fila);
        }

        return col;
    }

    private void mostrarConfirmacionReceptor(Intercambio intercambio, Pane root) {
        VBox panel = new VBox(12);
        panel.setLayoutX(200);
        panel.setLayoutY(150);
        panel.setPrefWidth(400);
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label titulo = new Label("Propuesta de " + intercambio.getOferente().getNombre());
        titulo.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;"
        );

        // Resumen del intercambio
        Label resumenOferta = new Label("Te ofrece: " + formatearRecursos(intercambio.getOferta()));
        resumenOferta.setWrapText(true);
        resumenOferta.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia'; -fx-font-size: 12px;");

        Label resumenPedido = new Label("Pide a cambio: " + formatearRecursos(intercambio.getPedido()));
        resumenPedido.setWrapText(true);
        resumenPedido.setStyle("-fx-text-fill: #C4956A; -fx-font-family: 'Georgia'; -fx-font-size: 12px;");

        HBox botones = new HBox(12);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnAceptar = new Button("✓ Aceptar");
        btnAceptar.setStyle(
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
        btnAceptar.setOnAction(e -> {
            boolean exito = intercambio.ejecutar();
            root.getChildren().remove(panel);
            if (exito) {
                actualizarInventario();
                mostrarMensaje("¡Intercambio realizado!", root);
            } else {
                mostrarMensaje("Intercambio fallido:\nrecursos insuficientes", root);
            }
        });

        Button btnRechazar = new Button("✗ Rechazar");
        btnRechazar.setStyle(
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
        btnRechazar.setOnAction(e -> {
            root.getChildren().remove(panel);
            mostrarMensaje("Intercambio rechazado", root);
        });

        if (esBotJugador(intercambio.getReceptor())) {
            BotJugador bot = getBotDeJugador(intercambio.getReceptor());
            boolean acepta = bot.evaluarIntercambio(
                    intercambio.getOferta(),  // lo que el bot recibe
                    intercambio.getPedido()   // lo que el bot entrega
            );

            if (acepta) {
                boolean exito = intercambio.ejecutar();
                actualizarInventario();
                mostrarMensaje(exito ?
                        "✓ " + intercambio.getReceptor().getNombre() +
                                " aceptó el intercambio" :
                        "Intercambio fallido", root);
            } else {
                mostrarMensaje("✗ " + intercambio.getReceptor().getNombre() +
                        " rechazó el intercambio", root);
            }
            return;
        }

        botones.getChildren().addAll(btnAceptar, btnRechazar);
        panel.getChildren().addAll(titulo, resumenOferta, resumenPedido, botones);
        root.getChildren().add(panel);
    }

    private String formatearRecursos(Map<Recurso, Integer> recursos) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Recurso, Integer> e : recursos.entrySet()) {
            if (e.getValue() > 0) {
                sb.append(e.getValue()).append(" ").append(e.getKey()).append(", ");
            }
        }
        if (sb.length() > 2) sb.setLength(sb.length() - 2);
        return sb.length() > 0 ? sb.toString() : "Nada";
    }

    private void mostrarMensaje(String mensaje, Pane root) {
        VBox panel = new VBox(12);
        panel.setLayoutX(300);
        panel.setLayoutY(250);
        panel.setPrefWidth(250);
        panel.setAlignment(javafx.geometry.Pos.CENTER);
        panel.setStyle(
                "-fx-background-color: rgba(44, 24, 16, 0.97);" +
                        "-fx-border-color: #D4A843;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 20;"
        );

        Label label = new Label(mensaje);
        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill: #D4A843;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-alignment: center;"
        );

        Button btnOk = new Button("OK");
        btnOk.setStyle(estiloBotonConstruir);
        btnOk.setOnAction(e -> root.getChildren().remove(panel));

        panel.getChildren().addAll(label, btnOk);
        root.getChildren().add(panel);
    }

    private void mostrarResumenRecursos(String resumen, int numeroDado) {
        bannerRecursos.setText("Dado " + numeroDado + ": " + resumen.replace("\n", " | "));
        bannerRecursos.setVisible(true);

        // Desaparece solo después de 3 segundos
        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(3)
        );
        pausa.setOnFinished(e -> bannerRecursos.setVisible(false));
        pausa.play();
    }

    private BotJugador obtenerBotActual() {
        Jugador actual = gestor.obtenerTurnoActual();
        for (BotJugador bot : bots) {
            if (bot.getJugador() == actual) return bot;
        }
        return null;
    }

    private void ejecutarTurnoBot() {
        BotJugador bot = obtenerBotActual();
        if (bot == null) return;

        deshabilitarControlesParaBot();
        labelFase.setText("Turno del bot\n" + bot.getJugador().getNombre());
        labelFase.setText("Bot pensando...");
        botonDado.setDisable(true);
        botonFinTurno.setDisable(true);
        btnAldea.setDisable(true);
        btnCiudad.setDisable(true);
        btnCamino.setDisable(true);

        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(1.5)
        );

        pausa.setOnFinished(e -> {
            Dado dado = new Dado();
            int resultado = dado.lanzar();
            dibujarDado(canvasDado1, dado.getDado1());
            dibujarDado(canvasDado2, dado.getDado2());
            totalLabel.setText("Total: " + resultado);

            bot.setUltimoDado(resultado);

            if (resultado == 7) {
                // Mover ladrón sin llamar ejecutarTurno completo
                bot.moverLadronInteligente();
                Hexagono posLadron = logicaCatan.getLadron().getPosicion();
                if (posLadron != null) mapaVisual.dibujarLadron(posLadron);
            } else {
                String resumen = logicaCatan.producirRecursos(resultado);
                mostrarResumenRecursos(resumen, resultado);
            }

            javafx.animation.PauseTransition pausaAccion = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2.0));

            pausaAccion.setOnFinished(ev -> {
                // Ejecutar árbol de decisiones UNA sola vez
                bot.ejecutarTurno();

                // Actualizar solo los nodos que cambiaron
                for (Vertice v : listaVertices) {
                    mapaVisual.actualizarVertice(v);
                }
                for (Arista a : listaAristas) {
                    mapaVisual.actualizarArista(a);
                }

                actualizarInventario();

                Jugador ganador = logicaCatan.verificarVictoria();
                if (ganador != null) {
                    mostrarPantallaVictoria(ganador);
                    return;
                }

                javafx.animation.PauseTransition pausaFin = new javafx.animation.PauseTransition(
                        javafx.util.Duration.seconds(1.0)
                );
                pausaFin.setOnFinished(ef -> {
                    gestor.pasarTurno();
                    actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                            gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                    actualizarInventario();
                    actualizarColorInventario();
                    totalLabel.setText("Total: —");
                    dibujarDado(canvasDado1, 1);
                    dibujarDado(canvasDado2, 1);
                    labelFase.setText("¡Fase normal!\nTira los dados");

                    if (obtenerBotActual() != null) {
                        ejecutarTurnoBot();
                    } else {
                        habilitarControlesParaHumano();
                    }
                });
                pausaFin.play();
            });
            pausaAccion.play();
        });
        pausa.play();
    }

    private void deshabilitarControlesParaBot() {
        botonDado.setDisable(true);
        botonFinTurno.setDisable(true);
        btnAldea.setDisable(true);
        btnCiudad.setDisable(true);
        btnCamino.setDisable(true);
        btnComprarCarta.setDisable(true);
        botonVerCartas.setDisable(true);
        btnIntercambio.setDisable(true);
    }

    private void habilitarControlesParaHumano() {
        botonDado.setDisable(false);
        btnAldea.setDisable(true);
        btnCiudad.setDisable(true);
        btnCamino.setDisable(true);
        btnComprarCarta.setDisable(true);
        botonVerCartas.setDisable(false);
        btnIntercambio.setDisable(false);
        botonFinTurno.setDisable(true);
    }

    private void ejecutarFaseInicialBot() {
        BotJugador bot = obtenerBotActual();
        if (bot == null) return;

        labelFase.setText("Bot colocando\naldea inicial...");

        javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                javafx.util.Duration.seconds(1.5)
        );

        pausa.setOnFinished(e -> {
            Vertice mejorVertice = bot.encontrarMejorVerticeInicial();
            if (mejorVertice != null) {
                mejorVertice.construirAldeaDirecto(bot.getJugador());
                mapaVisual.actualizarVertice(mejorVertice); // ← sin renderizado completo
            }

            javafx.animation.PauseTransition pausaCamino = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(1.5)
            );

            pausaCamino.setOnFinished(ev -> {
                Arista mejorArista = bot.encontrarAristaInicialAdyacente(mejorVertice);
                if (mejorArista != null) {
                    mejorArista.construirCarretera(new Carretera(bot.getJugador()));
                    mapaVisual.actualizarArista(mejorArista); // ← sin renderizado completo
                }
                procesarFinTurnoInicial();
            });
            pausaCamino.play();
        });
        pausa.play();
    }

    private void procesarFinTurnoInicial() {
        aldeaInicialPuesta = true;
        construccionesIniciales++;
        aldeaInicialPuesta = false;

        if (!rondaInversa) {
            if (this.gestor.esUltimoJugador()) {
                rondaInversa = true;
                labelFase.setText("Fase inicial - Ronda 2\n" +
                        gestor.obtenerTurnoActual().getNombre() + ": coloca tu aldea");
            } else {
                this.gestor.pasarTurno();
                actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                        gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                labelFase.setText("Fase inicial\n" +
                        gestor.obtenerTurnoActual().getNombre() + ": coloca tu aldea");
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
                actualizarTarjetas(tarjetasJugadores, nombresJugadores,
                        gestor.obtenerTurnoActual().getNombre(), coloresJugador);
                labelFase.setText("Fase inicial - Ronda 2\n" +
                        gestor.obtenerTurnoActual().getNombre() + ": coloca tu aldea");
            }
        }

        // ← UN SOLO bloque, sin duplicados
        if (obtenerBotActual() != null) {
            deshabilitarControlesParaBot();
            ejecutarFaseInicialBot();
        } else {
            btnAldea.setDisable(false);
            btnCamino.setDisable(true);
            botonFinTurno.setDisable(true);
            btnAldea.setStyle(estiloBotonActivo);
            btnCamino.setStyle(estiloBotonConstruir);
            modoActual = ModoConstruccion.ALDEA;
            mapaVisual.setModo(ModoConstruccion.ALDEA);
        }
    }

    private boolean esBotJugador(Jugador jugador) {
        return bots.stream().anyMatch(b -> b.getJugador() == jugador);
    }

    private BotJugador getBotDeJugador(Jugador jugador) {
        return bots.stream()
                .filter(b -> b.getJugador() == jugador)
                .findFirst().orElse(null);
    }
}
