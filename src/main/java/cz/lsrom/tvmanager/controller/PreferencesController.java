package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.model.PreferencesHandler;
import cz.lsrom.tvmanager.model.ReplacementToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static cz.lsrom.tvmanager.UIStarter.preferences;

/**
 * Created by lsrom on 11/19/16.
 */
public class PreferencesController {
    private static Logger logger = LoggerFactory.getLogger(PreferencesController.class);

    public static final String RENAME_HISTORY_FILE = "rename_history.log";

    @FXML private TextField txtDefaultOpenLocation;
    @FXML private Button btnChooseDefaultOpenLocation;
    @FXML private TextField txtTvDirectory;
    @FXML private Button btnChoseTvDirectory;
    @FXML private TextArea renameText;
    @FXML private ListView<String> renameTokensList;
    @FXML private TextField txtRenameFormat;
    @FXML private Button btnRenameFormat;
    @FXML private CheckBox checkSkipEmptyResolutionToken;
    @FXML private CheckBox checkAggressivelySkipEmptyResolutionToken;
    @FXML private CheckBox checkSaveRenameHistory;
    @FXML private ChoiceBox<String> choiceRenameHistory;
    @FXML private TextField txtDownloadDirectory;
    @FXML private Button btnDownloadDirectory;
    @FXML private TextField txtFileExtensions;
    @FXML private Button btnFileExtensions;
    @FXML private CheckBox checkDownloadDirectory;
    @FXML private TextField txtSkipFiles;
    @FXML private Button btnSkipFiles;
    @FXML private CheckBox checkRemoveRenamed;
    @FXML private CheckBox checkMoveAfterRename;
    @FXML private TextField txtMoveAfterRename;
    @FXML private Button btnMoveAfterRename;

    @FXML
    public void initialize (){
        initializeTxtDefaultOpenLocation();
        initializeBtnChooseDefaultOpenLocation();
        initializeTxtTvDirectory();
        initializeBtnChooseTvDirectory();

        initializeRenameLabel();
        initializeTokensList();
        initializeTxtRenameFormat();
        initializeBtnRenameFormat();

        initializeCheckSkipEmptyResolutionToken();
        initializeCheckAggressivelySkipEmptyResolutionToken();
        initializeCheckSaveRenameHistory();
        initializeChoiceRenameHistory();

        initializeTxtDownloadDirectory();
        initializeBtnDownloadDirectory();
        initializeTxtFileExtensions();
        initializeBtnFileExtensions();
        initializeCheckDownloadDirectory();
        initializeBtnSkipFiles();
        initializeTxtSkipFiles();

        initializeCheckRemoveRenamed();

        initializeBtnMoveAfterRename();
        initializeCheckMoveAfterRename();
        initializeTxtMoveAfterRename();
    }

    private void initializeCheckMoveAfterRename (){
        checkMoveAfterRename.setDisable(preferences.tvShowDirectory.isEmpty());
        checkMoveAfterRename.setSelected(preferences.moveAfterRename);

        checkMoveAfterRename.setTooltip(new Tooltip("Move renamed files to: " +
                preferences.tvShowDirectory + File.separator +
                "ShowName" + File.separator + preferences.seasonFormat));

        checkMoveAfterRename.setOnAction(event -> {
            preferences.moveAfterRename = checkMoveAfterRename.isSelected();
            savePreferences();

            txtMoveAfterRename.setDisable(!checkMoveAfterRename.isSelected());
            btnMoveAfterRename.setDisable(!checkMoveAfterRename.isSelected());
        });
    }

    private void initializeTxtMoveAfterRename (){
        txtMoveAfterRename.setDisable(!preferences.moveAfterRename);
        txtMoveAfterRename.setText(preferences.seasonFormat);

        txtMoveAfterRename.setTooltip(new Tooltip("Beginning of the season directory name. If empty, " +
                "all episodes in this season will be placed directly into the show directory.\r\n" +
                "For season number you can use the same format as in replacement pattern (%2s for example)."));
    }

    private void initializeBtnMoveAfterRename (){
        btnMoveAfterRename.setDisable(!preferences.moveAfterRename);

        btnMoveAfterRename.setOnAction(event -> {
            preferences.seasonFormat = txtMoveAfterRename.getText();
            savePreferences();
        });
    }

    private void initializeCheckRemoveRenamed (){
        checkRemoveRenamed.setSelected(preferences.removeRenamedFiles);

        checkRemoveRenamed.setTooltip(new Tooltip("Remove successfully renamed files from the table?"));

        checkRemoveRenamed.setOnAction(event -> {
            preferences.removeRenamedFiles = checkRemoveRenamed.isSelected();
            savePreferences();
        });
    }

    private void initializeTxtSkipFiles (){
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : preferences.skipFilesContaining){
            if (stringBuilder.length() != 0){stringBuilder.append(", ");}
            stringBuilder.append(s);
        }

        txtSkipFiles.setText(stringBuilder.toString());

        txtSkipFiles.setTooltip(new Tooltip("Files containing these substrings will not be loaded. These substring are case insensitive so the following are all the same: \"sample\", \"Sample\", \"SaMpLe\"."));
    }

    private void initializeBtnSkipFiles (){
        btnSkipFiles.setOnAction(event -> {
            String str = txtSkipFiles.getText().replaceAll("[ ]{1,}", "");
            preferences.skipFilesContaining = str.split(",");

            savePreferences();
        });
    }

    private void initializeCheckDownloadDirectory (){
        checkDownloadDirectory.setSelected(preferences.preloadFromDownloadDirectory);

        checkDownloadDirectory.setTooltip(new Tooltip("If enabled, TV Manager will load files from Download directory on every startup."));

        checkDownloadDirectory.setOnAction(event -> {
            preferences.preloadFromDownloadDirectory = checkDownloadDirectory.isSelected();
            savePreferences();
        });
    }

    private void initializeTxtDownloadDirectory (){
        txtDownloadDirectory.setText(preferences.tvShowDownloadDirectory);

        if (preferences.tvShowDownloadDirectory.isEmpty()){
            checkDownloadDirectory.setDisable(true);
        }

        txtDownloadDirectory.setTooltip(new Tooltip("Pick the directory in which you download new episodes of your TV shows."));
    }

    private void initializeBtnDownloadDirectory (){
        btnDownloadDirectory.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose your download location.");
            File dir = directoryChooser.showDialog(btnChooseDefaultOpenLocation.getScene().getWindow());

            txtDownloadDirectory.setText(dir == null ? "" : dir.getAbsolutePath().toString() + File.separator);

            preferences.tvShowDownloadDirectory = dir == null ? "" : dir.getAbsolutePath().toString() + File.separator;

            if (!preferences.tvShowDownloadDirectory.isEmpty()){
                checkDownloadDirectory.setDisable(false);
            }

            savePreferences();
        });
    }

    private void initializeTxtFileExtensions (){
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : preferences.supportedFileExtensions){
            if (stringBuilder.length() != 0){stringBuilder.append(", ");}
            stringBuilder.append(s);
        }

        txtFileExtensions.setText(stringBuilder.toString());

        txtFileExtensions.setTooltip(new Tooltip("Set which files should be loaded to TV Manager by their extensions. Separate extensions with commas."));
    }

    private void initializeBtnFileExtensions (){
        btnFileExtensions.setOnAction(event -> {
            String str = txtFileExtensions.getText().replaceAll("[ ]{1,}", "");
            preferences.supportedFileExtensions = str.split(",");

            savePreferences();
        });
    }

    private void initializeTxtDefaultOpenLocation (){
        txtDefaultOpenLocation.setText(preferences.defaultFileChooserOpenLocation);

        txtDefaultOpenLocation.setTooltip(new Tooltip("This is where all pick file/directory dialogs will start " +
                "so it might be wise to pick your download directory."));
    }

    private void initializeBtnChooseDefaultOpenLocation (){
        btnChooseDefaultOpenLocation.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose default location for file chooser.");
            File dir = directoryChooser.showDialog(btnChooseDefaultOpenLocation.getScene().getWindow());

            txtDefaultOpenLocation.setText(dir == null ? "" : dir.getAbsolutePath().toString());

            preferences.defaultFileChooserOpenLocation = dir == null ? "" : dir.getAbsolutePath().toString();
            savePreferences();
        });
    }

    private void initializeTxtTvDirectory (){
        txtTvDirectory.setText(preferences.tvShowDirectory);

        txtTvDirectory.setTooltip(new Tooltip("This should be the root directory of your TV show collection."));
    }

    private void initializeBtnChooseTvDirectory (){
        btnChoseTvDirectory.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose default location for file chooser.");
            File dir = directoryChooser.showDialog(btnChoseTvDirectory.getScene().getWindow());

            txtTvDirectory.setText(dir == null ? "" : dir.getAbsolutePath().toString() + File.separator);

            preferences.tvShowDirectory = dir == null ? "" : dir.getAbsolutePath().toString();
            savePreferences();
        });
    }

    private void initializeRenameLabel (){
        renameText.setWrapText(true);
        renameText.setEditable(false);
        renameText.setMinHeight(100);
    }

    private void initializeChoiceRenameHistory (){
        final String defaultLocation = "Default Location";
        final String showDir = "Show Directory";
        final String customLocation = "Custom Location";

        choiceRenameHistory.setDisable(!preferences.saveRenameHistory);
        choiceRenameHistory.setTooltip(new Tooltip("Choose where the file with rename history should be saved."));

        if (preferences.saveRenameHistory && !preferences.saveRenameHistoryToShowDir){
            choiceRenameHistory.getItems().setAll(
                    defaultLocation,
                    showDir,
                    preferences.customRenameHistoryLocation
            );

            choiceRenameHistory.setValue(preferences.customRenameHistoryLocation);
        } else {
            choiceRenameHistory.getItems().setAll(
                    defaultLocation,
                    showDir,
                    customLocation
            );
        }

        choiceRenameHistory.setOnAction(event -> {
            String selected = choiceRenameHistory.getValue();

            if (selected != null && selected.equals(defaultLocation)){
                preferences.saveRenameHistoryToShowDir = false;

                preferences.customRenameHistoryLocation = preferences.tvManagerPreferencesDirectory.endsWith(File.separator) ?
                        preferences.tvManagerPreferencesDirectory + RENAME_HISTORY_FILE :
                        preferences.tvManagerPreferencesDirectory + File.separator + RENAME_HISTORY_FILE;
            } else if (selected != null && selected.equals(showDir)){
                preferences.saveRenameHistoryToShowDir = true;

                savePreferences();
            } else if (selected != null && selected.equals(customLocation)){
                preferences.saveRenameHistoryToShowDir = false;

                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setInitialDirectory(new File(preferences.tvManagerPreferencesDirectory));
                directoryChooser.setTitle("Choose location for saving rename history");
                File dir = directoryChooser.showDialog(choiceRenameHistory.getScene().getWindow());

                preferences.customRenameHistoryLocation = dir.getAbsolutePath().toString();

                choiceRenameHistory.getItems().set(1, preferences.customRenameHistoryLocation);
                choiceRenameHistory.setValue(preferences.customRenameHistoryLocation);

                savePreferences();
            }
        });
    }

    private void initializeCheckSaveRenameHistory (){
        checkSaveRenameHistory.setSelected(preferences.saveRenameHistory);
        checkSaveRenameHistory.setTooltip(new Tooltip("Do you want to save file with rename history? \r\n" +
                "It might be useful to know original name of the file, for example for downloading subtitles."));

        checkSaveRenameHistory.setOnAction(event -> {
            if (checkSaveRenameHistory.isSelected()){
                preferences.saveRenameHistory = checkSaveRenameHistory.isSelected();

                choiceRenameHistory.setDisable(false);

                savePreferences();
            } else {
                choiceRenameHistory.setDisable(true);
            }
        });
    }

    private void initializeCheckAggressivelySkipEmptyResolutionToken (){
        checkAggressivelySkipEmptyResolutionToken.setDisable(!preferences.skipEmptyResolutionToken);
        checkAggressivelySkipEmptyResolutionToken.setSelected(preferences.aggressivelySkipEmptyResolutionToken);

        checkAggressivelySkipEmptyResolutionToken.setTooltip(new Tooltip("If enabled, all non-space characters around empty " +
                "resolution token will be erased \r\nand all multiple spaces replaced with one."));

        checkAggressivelySkipEmptyResolutionToken.setOnAction(event -> {
            preferences.aggressivelySkipEmptyResolutionToken = checkAggressivelySkipEmptyResolutionToken.isSelected();
            savePreferences();
        });
    }

    private void initializeCheckSkipEmptyResolutionToken (){
        checkSkipEmptyResolutionToken.setSelected(preferences.skipEmptyResolutionToken);

        checkSkipEmptyResolutionToken.setTooltip(new Tooltip("If enabled, resolution token which has not been replaced with resolution string is removed. \r\n" +
                "Any directly attached non-space characters are left in the string. \r\n" +
                "Multiple spaces are replaced with single space."));

        checkSkipEmptyResolutionToken.setOnAction(event -> {
            preferences.skipEmptyResolutionToken = checkSkipEmptyResolutionToken.isSelected();
            savePreferences();

            if (checkSkipEmptyResolutionToken.isSelected()){
                checkAggressivelySkipEmptyResolutionToken.setDisable(false);
            } else {
                checkAggressivelySkipEmptyResolutionToken.setDisable(true);
            }
        });
    }

    private void initializeTokensList (){
        renameTokensList.getItems().addAll(
                ReplacementToken.SHOW_NAME.getToken() + " \t: " + ReplacementToken.SHOW_NAME.getTokenDescription(),
                ReplacementToken.SEASON_NUM.getToken() + " \t: " + ReplacementToken.SEASON_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_NUM.getToken() + " \t: " + ReplacementToken.EPISODE_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_ABS_NUM.getToken() + " \t: " + ReplacementToken.EPISODE_ABS_NUM.getTokenDescription(),
                ReplacementToken.EPISODE_TITLE.getToken() + " \t: " + ReplacementToken.EPISODE_TITLE.getTokenDescription(),
                ReplacementToken.EPISODE_RESOLUTION.getToken() + " \t: " + ReplacementToken.EPISODE_RESOLUTION.getTokenDescription()
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
            preferences.replacementString = txtRenameFormat.getText();

            savePreferences();
        });
    }

    private void initializeTxtRenameFormat (){
        txtRenameFormat.setText(preferences.replacementString);
        txtRenameFormat.requestFocus();
    }

    private void savePreferences (){
        try {
            PreferencesHandler.savePreferences(preferences);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
