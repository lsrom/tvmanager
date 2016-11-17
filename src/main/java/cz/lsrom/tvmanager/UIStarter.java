package cz.lsrom.tvmanager;

import cz.lsrom.tvmanager.controller.UIController;
import cz.lsrom.tvmanager.model.Preferences;
import cz.lsrom.tvmanager.model.PreferencesHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lsrom on 11/8/16.
 */
public class UIStarter extends Application {
    private static Logger logger = LoggerFactory.getLogger(UIStarter.class);

    public static Preferences preferences;

    public static void main(String[] args) {
        if (PreferencesHandler.preferencesExist()){
            preferences = PreferencesHandler.loadPreferences();
            logger.info("Preferences loaded.");
        } else {
            preferences = new Preferences();
            try {
                PreferencesHandler.savePreferences(preferences);
            } catch (IOException e) {
                logger.error(e.toString());
            }

            logger.info("New preferences created and saved.");
        }

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
