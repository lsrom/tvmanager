package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;

/**
 * Created by lsrom on 11/8/16.
 */
public class UIController {
    @FXML private Tab renameTab;

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
            e.printStackTrace();
        }

        renameTab.setContent(tab);

        initializeBtnClose();
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
