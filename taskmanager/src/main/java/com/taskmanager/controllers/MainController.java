package com.taskmanager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane rootPane;

    public void initialize() {
        // Load login view by default
        try {
            setCenter("/views/login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCenter(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Node node = loader.load();
        rootPane.setCenter(node);
    }
}