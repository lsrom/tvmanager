package cz.lsrom.tvmanager;

import cz.lsrom.tvmanager.controller.UIController;
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

        UIController mainController = new UIController();
        mainController.setUiStarter(this);

        Scene scene = new Scene(root, 1280, 640);

        primaryStage.setTitle("TV Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
