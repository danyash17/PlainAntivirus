package com.example.desktopantivirus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationStarter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationStarter.class.getResource("primal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 824, 554);
        stage.setTitle("DesktopAntivirus1.0");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("pics/antivirus.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}