package org.dsc.metadataanalyser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.dsc.metadataanalyser.utilties.TikaController;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class MetaDataAnalyser extends Application {
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        TikaController tc = TikaController.getInstance();

        URI tikaJarURI = Objects.requireNonNull(MetaDataAnalyserController.class.getResource("tika-server-standard-2.6.0.jar")).toURI();
        File tikaJarFile = new File(tikaJarURI.getPath());
        URI tikaConfigURI = Objects.requireNonNull(MetaDataAnalyserController.class.getResource("tika-server-config-default.xml")).toURI();
        File tikaConfigFile = new File(tikaConfigURI.getPath());
        String [] command = {"java","-jar",tikaJarFile.getAbsolutePath(),"-c",tikaConfigFile.getAbsolutePath()};
        tc.setDirectory(tikaConfigFile.getParent());

        tc.setCmd(command);
        tc.run();
        Thread closeChildThread = new Thread(() -> tc.getProcess().destroy());
        Runtime.getRuntime().addShutdownHook(closeChildThread);

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