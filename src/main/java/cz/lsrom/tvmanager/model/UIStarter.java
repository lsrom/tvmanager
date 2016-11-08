package cz.lsrom.tvmanager.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by lsrom on 11/8/16.
 */
public class UIStarter extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;

        try {
            root = FXMLLoader.load(UIStarter.class.getResource("/fxmls/main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 800, 450);

        primaryStage.setTitle("TV Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
