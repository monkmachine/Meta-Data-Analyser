package org.dsc.metadataanalyser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MetaDataAnalyser extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MetaDataAnalyserController.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 1200);
        stage.setTitle("Tika Meta Data Analyser");
        stage.getIcons().add(new Image(Objects.requireNonNull(MetaDataAnalyser.class.getResource("tika.png")).openStream()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}