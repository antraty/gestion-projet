package com.taskmanager;
import com.taskmanager.config.AppConfig;
import com.taskmanager.services.DatabaseService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;   
    
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppConfig.load();
        DatabaseService.getInstance().initialize();

        FXMLLoader loader = new FXMLLoader();
        InputStream mainFxml = Main.class.getResourceAsStream("/views/main-layout.fxml");
        loader.setLocation(Main.class.getResource("/views/main-layout.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());

        primaryStage.setTitle("TaskManager");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        DatabaseService.getInstance().close();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}