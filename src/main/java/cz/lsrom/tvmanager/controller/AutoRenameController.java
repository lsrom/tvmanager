package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.model.PreferencesHandler;
import cz.lsrom.tvmanager.workers.Parser;
import cz.lsrom.tvmanager.workers.Renamer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static cz.lsrom.tvmanager.UIStarter.preferences;

/**
 * Created by lsrom on 11/9/16.
 */
public class AutoRenameController {
    private static Logger logger = LoggerFactory.getLogger(AutoRenameController.class);

    private static String STATUS_FAILED = "Failed";
    private static String STATUS_WORKING = "Working...";

    @FXML private TableView showList;
    @FXML private Button btnAddFiles;
    @FXML private Button btnAddDirectories;
    @FXML private Button btnClearAll;
    @FXML private Button btnRename;
    @FXML private Button btnOkReplacementString;
    @FXML private TextField txtReplacementString;

    private List<File> filesToRename;
    private List<EpisodeFile> episodeFileList;
    private Renamer renamer;

    private ExecutorService addingService;
    private ExecutorService renamingService;
    private ObservableList<Pair<EpisodeFile, ObservableList<String>>> data;

    private String listFilesExtension = "";     // file extension for regex (ext1|ext2|ext3|...)
    private String listSkipFilesContaining = "";    // substrings for regex (sub1|sub2|sub3|...)

    @FXML
    private void initialize() {
        logger.debug("RenamerControlller initializing.");

        // here we'll put all episode files -> this will be the data for the table
        data = FXCollections.observableArrayList();

        renamingService = Executors.newFixedThreadPool(5);

        initializeTable();
        setColumnWidth();
        setColumnValueFactory();

        initializeBtnAddFiles();
        initializeBtnAddDirectories();
        initializeBtnOkReplacementString();
        initializeBtnRename();
        initializeBtnClearAll();

        txtReplacementString.setText(UIStarter.preferences.replacementString);

        initializeKeyboardShortcuts();
        initializeLabelItems();

        // create regex with supported file extensions - these will be loaded
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : preferences.supportedFileExtensions){
            stringBuilder.append(s);
            stringBuilder.append("|");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);     // remove last '|'
        listFilesExtension = stringBuilder.toString();

        // create regex with filename substrings to be skipped during loading
        stringBuilder = new StringBuilder();
        for (String s : preferences.skipFilesContaining){
            stringBuilder.append(s.toLowerCase());
            stringBuilder.append("|");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);     // remove last '|'
        listSkipFilesContaining = stringBuilder.toString();

        Task<Renamer> createRenamerObject = new Task<Renamer>() {
            @Override
            protected Renamer call() throws Exception {
                return new Renamer(preferences);
            }
        };

        createRenamerObject.setOnSucceeded(event -> renamer = createRenamerObject.getValue());

        Thread theTvdbLogin = new Thread(createRenamerObject);
        theTvdbLogin.setDaemon(true);
        theTvdbLogin.start();
    }

    private void initializeLabelItems (){
        setItems(data.size());
    }

    private void populateViewWithItems (){
        if (filesToRename == null || filesToRename.isEmpty()){
            return;
        }

        addingService = Executors.newFixedThreadPool(10);

        // this task will run asynchronously so UI thread won't be frozen by parsing filenames
        Task<List<EpisodeFile>> getEpisodeFiles = new Task<List<EpisodeFile>>() {
            @Override
            protected List<EpisodeFile> call() throws Exception {
                logger.debug("Parsing files.");
                List<EpisodeFile> list = new ArrayList<>();
                int added = 0;

                for (File f : filesToRename){
                    EpisodeFile ep = Parser.parse(f);
                    list.add(ep);
                    addingService.submit(() -> renamer.addShow(ep));
                    added++;
                }

                addingService.shutdown();

                final int finalAdded = added;
                Platform.runLater(() -> setItems(finalAdded));

                logger.debug("Parsing done.");
                return list;
            }
        };

        // set listener for when the task ends
        getEpisodeFiles.setOnSucceeded(event -> {
            logger.debug("Files parsed successfully - writing to screen.");
            episodeFileList = getEpisodeFiles.getValue();
            boolean added = false;
            for (EpisodeFile ef : episodeFileList){                          // iterate over all episodes
                File f = ef.getFile();

                ObservableList<String> row = FXCollections.observableArrayList();       // create new row so we don't overwrite the existing one
                row.setAll(ef.getShowName(),
                        ef.getDirectory(),
                        ef.getFile().toString().replace(ef.getDirectory() + "/", ""), // get original name
                        STATUS_WORKING);

                if (isNewFile(f)){
                    added = data.add(new Pair<>(ef, row));
                }
            }

            showList.getItems().clear();
            showList.getItems().addAll(data);       // set all items to the table

            if (added){
                renamingService.submit(() -> startRenaming());
            }
            showList.refresh();
        });

        Thread episodeGetter = new Thread(getEpisodeFiles);     // create new thread with the task
        episodeGetter.setDaemon(true);
        episodeGetter.start();
    }

    /**
     * Shows the amount of added files in the status bar. If passed number is lower then zero, method ends without any
     * effect.
     *
     * @param amount How many files are in the table.
     */
    private void setItems (int amount){
        if (UIController.labelItemsStatic == null){return;} // check if UI is initialized
        if (amount < 0){return;}    // we don't want to set negative value

        UIController.labelItemsStatic.setText(amount + " items");
        UIController.labelItemsStatic.setTooltip(new Tooltip("There is " + amount + " items in the table."));
    }

    /**
     * Shows the number of successfully renamed files in the status bar. If passed number is lower then zero, methods ends
     * without any effect.
     *
     * @param amount How many files was successfully renamed.
     */
    private void setRenamed (int amount){
        if (UIController.labelItemsStatic == null){return;} // check if UI is initialized
        if (amount < 0){return;}    // we don't want to set negative value

        UIController.labelItemsStatic.setText(amount + " renamed");
        UIController.labelItemsStatic.setTooltip(new Tooltip(amount + " items successfully renamed, out of " + data.size()));
    }

    private boolean isNewFile (File file){
        if (showList.getItems().isEmpty()){return true;}

        for (Object o : showList.getItems()){
            if (((Pair<EpisodeFile, ObservableList<String>>)o).getKey().getFile().equals(file)){
                logger.debug("Attempting to add already added file. Skipping.");
                return false;
            }
        }

        return true;
    }

    private void startRenaming (){
        boolean interrupted = false;
        try {
            interrupted = addingService.awaitTermination(UIStarter.preferences.awaitTermination, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }

        logger.debug("Renaming interruption: {}.", interrupted ? "service finished." : "timeout.");

        for (Pair<EpisodeFile, ObservableList<String>> p : data){
            String newFilename = renamer.getNewFileName(p.getKey(), UIStarter.preferences.replacementString);

            if (newFilename != null){
                p.getKey().setNewFilename(newFilename);
                Platform.runLater(() -> p.getValue().set(3, newFilename));
            } else {
                Platform.runLater(() -> p.getValue().set(3, STATUS_FAILED));
            }
        }

        showList.refresh();
    }

    private void initializeBtnAddFiles (){
        btnAddFiles.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Files to Rename");
            fileChooser.setInitialDirectory(new File(UIStarter.preferences.defaultFileChooserOpenLocation));
            filesToRename = getFiles(fileChooser.showOpenMultipleDialog(showList.getScene().getWindow()));

            populateViewWithItems();
        });
    }

    private void initializeBtnAddDirectories (){
        btnAddDirectories.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Directories");
            directoryChooser.setInitialDirectory(new File(UIStarter.preferences.defaultFileChooserOpenLocation));
            File dir = directoryChooser.showDialog(showList.getScene().getWindow());

            if (loadFilesFromDirectory(dir)){
                populateViewWithItems();
            }
        });
    }

    private void initializeBtnOkReplacementString (){
        btnOkReplacementString.setOnAction(event -> {
            String replacementString = txtReplacementString.getText();

            UIStarter.preferences.replacementString = replacementString;
            try {
                PreferencesHandler.savePreferences(UIStarter.preferences);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }

            renamingService.submit(() -> startRenaming());
        });
    }

    private void initializeBtnClearAll (){
        btnClearAll.setOnAction(event -> {
            showList.getItems().clear();
            data.clear();
        });
    }

    private void initializeBtnRename (){
        btnRename.setOnAction(event -> {
            ObservableList<Pair<EpisodeFile, ObservableList<String>>> selected = showList.getSelectionModel().getSelectedItems();
            int successfullyRenamed = 0;

            for (Pair<EpisodeFile, ObservableList<String>> p : selected.size() == 0 ? data : selected){
                try {
                    // only rename file if status is not failed
                    if (!p.getValue().get(3).equals(STATUS_FAILED) && !p.getValue().get(3).equals(STATUS_WORKING)){
                        p = new Pair<>(renamer.rename(p.getKey()), p.getValue());
                        p.getValue().set(2, p.getValue().get(3));

                        successfullyRenamed++;

                        if (preferences.removeRenamedFiles){
                            final Pair<EpisodeFile, ObservableList<String>> finalP = p;
                            Platform.runLater(() -> removeAndRefresh(finalP));
                        }
                    }
                    setRenamed(successfullyRenamed);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

            renamer.forceFlushHistory();
            showList.refresh();
        });
    }

    // todo this works but the table doesnt refresh
    private void removeAndRefresh(final Pair<EpisodeFile, ObservableList<String>> finalP){
        data.remove(finalP);
        showList.refresh();
    }

    private void initializeTable (){
        showList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // enable multi row selection
        showList.setEditable(true);

        showList.setOnKeyPressed(event -> {
            final ObservableList selectedItems = showList.getSelectionModel().getSelectedItems();

            if (selectedItems != null && event.getCode().equals(KeyCode.DELETE)){
                data.removeAll(selectedItems);

                showList.getItems().setAll(data);
            }
        });
    }

    private void setColumnWidth (){
        ObservableList<TableColumn> columns = showList.getColumns();

        // set width of columns - works even when window is resized
        columns.get(0).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(10));
        columns.get(1).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(10));
        columns.get(2).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(22));
        columns.get(3).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(22));
    }

    private void setColumnValueFactory (){
        ObservableList<TableColumn> columns = showList.getColumns();

        columns.get(0).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().get(0));
            }
        });

        columns.get(1).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().get(1));
            }
        });

        columns.get(2).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().get(2));
            }
        });

        columns.get(3).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Pair<EpisodeFile, ObservableList<String>>, String> param) {
                return new SimpleStringProperty(param.getValue().getValue().get(3));
            }
        });
    }

    private void initializeKeyboardShortcuts (){
        final KeyCombination openFiles = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        final KeyCombination openDir = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);

        EventHandler<KeyEvent> eventHandler = event -> {
            if (openDir.match(event)) {
                btnAddDirectories.fire();
            } else if (openFiles.match(event)){
                btnAddFiles.fire();
            }
        };

        showList.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
            }

            if (newValue != null) {
                newValue.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
            }
        });
    }

    private boolean loadFilesFromDirectory (File dir){
        if (dir == null){
            return false;
        }

        if (dir.isDirectory()){
            filesToRename = listFilesInDir(dir.toString());
            return true;
        }

        return false;
    }

    /**
     * List all files in given directory. If directory is null or empty,
     * new empty ArrayList is returned.
     *
     * Note that this method looks even through subdirectories but returns only files.
     *
     * @param dir String path to directory from which the function should start listing files.
     * @return List of Strings containing all files or empty list if no such files exist.
     */
    private List<File> listFilesInDir (String dir){
        if (dir == null){ return new ArrayList<>(); }   // end in case of empty/null dir parameter and return empty list

        List<Path> list = new ArrayList<>();            // stores all found files and directories

        try (Stream<Path> stream = Files.list(Paths.get(dir))){
            stream.forEach(list::add);  // add each found file to list
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        List<File> files = new ArrayList<>();

        for (Path p : list){
            File file = new File(p.toUri());
            String path = p.toString();
            if (file.isDirectory()){
                // if file is directory, list all files from it recursively
                files.addAll(listFilesInDir(file.toString()));
            } else if (path.matches(".*(" + listFilesExtension + ")$") && !path.toLowerCase().matches(".*(" + listSkipFilesContaining + ").*")){
                files.add(file);       // add normal file to list
            }
        }

        return files;
    }

    /**
     * Returns all the files that match criteria from Preferences about which files should be loaded.
     * If given parameter is null or empty list, it returns empty list.
     *
     * @param list List of files from which only files matching the criteria will be returned.
     * @return List of files matching the criteria set in Preferences.
     */
    private List<File> getFiles (List<File> list){
        if (list == null || list.isEmpty()){return list;}

        List<File> files = new ArrayList<>();

        for (File f : list){
            String path = f.toString();
            if (f.isDirectory()){
                // if file is directory, list all files from it recursively
                files.addAll(listFilesInDir(f.toString()));
            } else if (path.matches(".*(" + listFilesExtension + ")$") && !path.toLowerCase().matches(".*(" + listSkipFilesContaining + ").*")){
                files.add(f);       // add normal file to list
            }
        }

        return files;
    }
}
