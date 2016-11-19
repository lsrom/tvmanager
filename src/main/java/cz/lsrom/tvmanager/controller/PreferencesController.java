package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import cz.lsrom.tvmanager.model.PreferencesHandler;
import cz.lsrom.tvmanager.model.ReplacementToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by lsrom on 11/19/16.
 */
public class PreferencesController {
    private static Logger logger = LoggerFactory.getLogger(PreferencesController.class);

    @FXML private TextArea renameText;
    @FXML private ListView<String> renameTokensList;
    @FXML private TextField txtRenameFormat;
    @FXML private Button btnRenameFormat;

    @FXML
    public void initialize (){
        initializeRenameLabel();
        initializeTokensList();
        initializeTxtRenameFormat();
        initializeBtnRenameFormat();
    }

    private void initializeRenameLabel (){
        renameText.setWrapText(true);
        renameText.setEditable(false);
        renameText.setMinHeight(100);
    }

    private void initializeTokensList (){
        renameTokensList.getItems().addAll(
                ReplacementToken.SHOW_NAME.getToken() + " : " + ReplacementToken.SHOW_NAME.getTokenDescription(),
                ReplacementToken.SEASON_NUM.getToken() + " : " + ReplacementToken.SEASON_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_NUM.getToken() + " : " + ReplacementToken.EPISODE_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_ABS_NUM.getToken() + " : " + ReplacementToken.EPISODE_ABS_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_TITLE.getToken() + " : " + ReplacementToken.EPISODE_TITLE.getTokenDescription(),
                ReplacementToken.EPISODE_RESOLUTION.getToken() + " : " + ReplacementToken.EPISODE_RESOLUTION.getTokenDescription()
        );

        renameTokensList.setTooltip(new Tooltip("Double click item to append it to current rename format string."));

        renameTokensList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                String currentItem = renameTokensList.getSelectionModel().getSelectedItem();
                txtRenameFormat.appendText(currentItem.split("\\:")[0].trim());
            }
        });
    }

    private void initializeBtnRenameFormat (){
        btnRenameFormat.setOnAction(event -> {
            String replacementString = txtRenameFormat.getText();

            UIStarter.preferences.replacementString = replacementString;
            try {
                PreferencesHandler.savePreferences(UIStarter.preferences);
            } catch (IOException e) {
                logger.error(e.toString());
            }
        });
    }

    private void initializeTxtRenameFormat (){
        txtRenameFormat.setText(UIStarter.preferences.replacementString);
        txtRenameFormat.requestFocus();
    }
}
