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
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by lsrom on 11/9/16.
 */
public class RenameController {
    private static Logger logger = LoggerFactory.getLogger(RenameController.class);

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

    private ExecutorService service;
    private ObservableList<Pair<EpisodeFile, ObservableList<String>>> data;

    @FXML
    private void initialize() {
        logger.debug("RenamerControlller initializing.");
        service = Executors.newFixedThreadPool(10);

        initializeTable();
        setColumnWidth();
        setColumnValueFactory();

        initializeBtnAddFiles();
        initializeBtnAddDirectories();
        initializeBtnOkReplacementString();
        initializeBtnRename();
        initializeBtnClearAll();

        txtReplacementString.setText(UIStarter.preferences.replacementString);

        //initializeKeyboardShortcuts();

        Task<Renamer> createRenamerObject = new Task<Renamer>() {
            @Override
            protected Renamer call() throws Exception {
                return new Renamer();
            }
        };

        createRenamerObject.setOnSucceeded(event -> renamer = createRenamerObject.getValue());

        Thread theTvdbLogin = new Thread(createRenamerObject);
        theTvdbLogin.setDaemon(true);
        theTvdbLogin.start();
    }

    private void populateViewWithItems (){
        if (filesToRename == null || filesToRename.isEmpty()){
            return;
        }

        // here we'll put all episode files -> this will be the data for the table
        data = FXCollections.observableArrayList();

        // this task will run asynchronously so UI thread won't be frozen by parsing filenames
        Task<List<EpisodeFile>> getEpisodeFiles = new Task<List<EpisodeFile>>() {
            @Override
            protected List<EpisodeFile> call() throws Exception {
                logger.debug("Parsing files.");
                List<EpisodeFile> list = new ArrayList<>();

                for (File f : filesToRename){
                    EpisodeFile ep = Parser.parse(f);
                    list.add(ep);
                    service.submit(() -> renamer.addShow(ep));
                }

                logger.debug("Parsing done.");
                return list;
            }
        };

        // set listener for when the task ends
        getEpisodeFiles.setOnSucceeded(event -> {
            logger.debug("Files parsed successfully - writing to screen.");
            episodeFileList = getEpisodeFiles.getValue();
            for (EpisodeFile ef : episodeFileList){                          // iterate over all episodes
                ObservableList<String> row = FXCollections.observableArrayList();       // create new row so we don't overwrite the existing one
                row.setAll(ef.getShowName(),
                        ef.getDirectory(),
                        ef.getFile().toString().replace(ef.getDirectory() + "/", ""),   // get original name
                        "Working...");

                data.add(new Pair<>(ef, row));
            }

            showList.getItems().addAll(data);       // set all items to the table

            service.submit(() -> startRenaming());
        });

        Thread episodeGetter = new Thread(getEpisodeFiles);     // create new thread with the task
        episodeGetter.setDaemon(true);
        episodeGetter.start();
    }

    private void startRenaming (){
        try {
            service.awaitTermination(5, TimeUnit.SECONDS);      // todo add this to preferences
        } catch (InterruptedException e) {
            logger.error(e.toString());
        }

        logger.debug("renaming now...");

        for (Pair<EpisodeFile, ObservableList<String>> p : data){
            String newFilename = renamer.getNewFileName(p.getKey(), UIStarter.preferences.replacementString);

            if (newFilename != null){
                Platform.runLater(() -> p.getValue().set(3, newFilename));
            }
        }

        showList.refresh();
    }

    private void initializeBtnAddFiles (){
        btnAddFiles.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Files to Rename");
            fileChooser.setInitialDirectory(new File(UIStarter.preferences.defaultFileChooserOpenLocation));
            filesToRename = fileChooser.showOpenMultipleDialog(showList.getScene().getWindow());

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
                logger.error(e.toString());
            }
        });
    }

    private void initializeBtnClearAll (){
        btnClearAll.setOnAction(event -> {
            showList.getItems().clear();
        });
    }

    private void initializeBtnRename (){
        // todo
    }

    private void initializeTable (){
        showList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // enable multi row selection
        showList.setEditable(true);

        showList.setOnKeyPressed(event -> {
            final Object selectedItem = showList.getSelectionModel().getSelectedItem();

            if (selectedItem != null && event.getCode().equals(KeyCode.DELETE)){
                data.remove(selectedItem);
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


        showList.getScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            System.out.println ("event");
            if (openFiles.match(event)){
                System.out.println ("match");
                btnAddFiles.fire();
            }
        });
    }

    private boolean loadFilesFromDirectory (File dir){
        if (dir == null){
            return false;
        }

        if (dir.isDirectory()){
            filesToRename = Arrays.asList(dir.listFiles());
            return true;
        }

        return false;
    }
}
