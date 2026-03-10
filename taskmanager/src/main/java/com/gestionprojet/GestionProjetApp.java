package com.gestionprojet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GestionProjetApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("E-rindra");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        showLoginView();
    }

    public static void showLoginView() throws IOException {
        Parent root = FXMLLoader.load(
            GestionProjetApp.class.getResource("/com/gestionprojet/views/login.fxml")
        );
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void showDashboardView() throws IOException {
        Parent root = FXMLLoader.load(
            GestionProjetApp.class.getResource("/com/gestionprojet/views/dashboard.fxml")
        );
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}