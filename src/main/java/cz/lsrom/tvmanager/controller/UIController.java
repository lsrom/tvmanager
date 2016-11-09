package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

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
        System.out.println ("initialize");

        Parent tab = null;

        try {
            tab = FXMLLoader.load(this.getClass().getResource("/fxmls/rename.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        renameTab.setContent(tab);

        initializeBtnClose();
    }

    private void initializeBtnClose (){
        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void initializeBtnAbout (){
        // todo
    }
}
