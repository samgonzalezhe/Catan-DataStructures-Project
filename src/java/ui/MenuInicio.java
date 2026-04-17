package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import logic.GestorTurnos;
import logic.Jugador;

import java.util.ArrayList;
import java.util.List;

public class MenuInicio {
    private Stage stage;

    public MenuInicio(Stage stage) {
        this.stage = stage;
    }

    public void mostrar() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #4B251C;");

        Label titulo = new Label("CATAN");
        titulo.setFont(Font.font("Georgia", FontWeight.BOLD, 48));
        titulo.setTextFill(Color.WHITE);

        Label subtitulo = new Label("Ingresa los nombres de los jugadores");
        subtitulo.setFont(Font.font("Georgia", 16));
        subtitulo.setTextFill(Color.LIGHTGRAY);

        // Colores asignados a cada jugador
        String[] colores = {"Rojo", "Azul", "Verde", "Naranja"};
        String[] estilosBorde = {
                "-fx-border-color: #e74c3c;",
                "-fx-border-color: #3498db;",
                "-fx-border-color: #2ecc71;",
                "-fx-border-color: #e67e22;"
        };

        List<TextField> campos = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            HBox fila = new HBox(12);
            fila.setAlignment(Pos.CENTER);

            Label etiqueta = new Label("Jugador " + (i + 1) + " (" + colores[i] + "):");
            etiqueta.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
            etiqueta.setTextFill(Color.WHITE);
            etiqueta.setMinWidth(200);

            TextField campo = new TextField();
            campo.setPromptText("Nombre del jugador " + (i + 1));
            campo.setFont(Font.font("Georgia", 14));
            campo.setPrefWidth(250);
            campo.setStyle(
                    "-fx-background-color: #34495e;" +
                            "-fx-text-fill: white;" +
                            "-fx-prompt-text-fill: #7f8c8d;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-radius: 4;" +
                            "-fx-background-radius: 4;" +
                            "-fx-padding: 8;" +
                            estilosBorde[i]
            );

            campos.add(campo);
            fila.getChildren().addAll(etiqueta, campo);
            root.getChildren().add(fila);
        }

        Label error = new Label("");
        error.setTextFill(Color.RED);
        error.setFont(Font.font("Verdana", 13));

        Button btnJugar = new Button("¡Jugar!");
        btnJugar.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        btnJugar.setPrefWidth(200);
        btnJugar.setStyle(
                "-fx-background-color: #8B1A1A;" +
                        "-fx-text-fill: D4A843;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 20;"
        );

        btnJugar.setOnAction(e -> {
            // Validar que todos los campos estén llenos
            List<String> nombres = new ArrayList<>();
            boolean valido = true;

            for (TextField campo : campos) {
                String nombre = campo.getText().trim();
                if (nombre.isEmpty()) {
                    valido = false;
                    break;
                }
                nombres.add(nombre);
            }

            if (!valido) {
                error.setText("Por favor ingresa el nombre de todos los jugadores.");
                return;
            }

            // Crear jugadores y registrarlos en el gestor
            GestorTurnos gestor = new GestorTurnos();
            for (int i = 0; i < nombres.size(); i++) {
                // El nombre incluye el color para que obtenerColorJugador funcione
                Jugador j = new Jugador(nombres.get(i));
                j.setColor(colores[i].toLowerCase()); // ver nota abajo
                gestor.registrarJugador(j);
            }

            new JuegoView(stage, gestor).iniciar();
        });

        root.getChildren().addAll(titulo, subtitulo, error, btnJugar);

        Scene scene = new Scene(root, 900, 700);
        stage.setTitle("Catan - Menú");
        stage.setScene(scene);
        stage.show();
    }
}
