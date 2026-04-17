package ui;

import javafx.application.Application;
import javafx.stage.Stage;



public class MainApp extends Application {

    private MapaVisual mapaVisual;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new MenuInicio(stage).mostrar();
    }
}
