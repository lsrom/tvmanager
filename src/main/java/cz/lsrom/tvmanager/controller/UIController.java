package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lsrom on 11/8/16.
 */
public class UIController {
    private static Logger logger = LoggerFactory.getLogger(UIStarter.class);

    @FXML private Tab renameTab;

    @FXML private MenuItem btnPreferences;
    @FXML private MenuItem btnClose;
    @FXML private MenuItem btnAbout;

    private UIStarter uiStarter;

    public void setUiStarter (UIStarter uiStarter){
        this.uiStarter = uiStarter;
    }

    @FXML
    private void initialize() {
        Parent tab = null;

        try {
            tab = FXMLLoader.load(this.getClass().getResource("/fxmls/automatic_rename.fxml"));
        } catch (IOException e) {
            logger.error(e.toString());
        }

        renameTab.setContent(tab);

        initializeBtnPreferences();
        initializeBtnClose();
    }

    private void initializeBtnPreferences (){
        btnPreferences.setOnAction(event -> {
            Parent root;

            try {
                root = FXMLLoader.load(this.getClass().getResource("/fxmls/preferences.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Preferences");
                stage.setScene(new Scene(root, 400, 600));
                stage.show();
            }
            catch (IOException e) {
                logger.error(e.toString());
            }
        });

        btnPreferences.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
    }

    private void initializeBtnClose (){
        btnClose.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });

        btnClose.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
    }

    private void initializeBtnAbout (){
        // todo
    }
}
