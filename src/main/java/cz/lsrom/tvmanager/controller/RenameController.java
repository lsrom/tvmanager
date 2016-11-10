package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.model.EpisodeFile;
import cz.lsrom.tvmanager.workers.Parser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lsrom on 11/9/16.
 */
public class RenameController {
    @FXML private TableView showList;
    @FXML private Button btnAddFiles;
    @FXML private Button btnAddDirectories;
    @FXML private Button btnClearAll;
    @FXML private Button btnRename;

    private List<File> filesToRename;
    private List<EpisodeFile> episodeFileList;


    @FXML
    private void initialize() {
        initializeTable();
        setColumnWidth();
        setColumnValueFactory();

        initializeBtnAddFiles();
        initializeBtnAddDirectories();

        //initializeKeyboardShortcuts();
    }

    private void populateViewWithItems (){
        if (filesToRename == null || filesToRename.isEmpty()){
            return;
        }

        // here we'll put all episode files -> this will be the data for the table
        ObservableList<ObservableList> data = FXCollections.observableArrayList();

        // this task will run asynchronously so UI thread won't be frozen by parsing filenames
        Task<List<EpisodeFile>> getEpisodeFiles = new Task<List<EpisodeFile>>() {
            @Override
            protected List<EpisodeFile> call() throws Exception {
                List<EpisodeFile> list = new ArrayList<>();

                for (File f : filesToRename){
                    list.add(Parser.parse(f));
                }

                return list;
            }
        };

        // set listener for when the task ends
        getEpisodeFiles.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                for (EpisodeFile ef : getEpisodeFiles.getValue()){                          // iterate over all episodes
                    ObservableList<String> row = FXCollections.observableArrayList();       // create new row so we don't overwrite the existing one
                    row.setAll(ef.getShowName(),
                            ef.getDirectory(),
                            ef.getFile().toString().replace(ef.getDirectory() + "/", ""),   // get original name
                            "TODO",                                                         // todo add new name
                            "Downloading");                                                     // todo ?

                    data.add(row);
                }

                showList.getItems().addAll(data);       // set all items to the table
            }
        });

        Thread episodeGetter = new Thread(getEpisodeFiles);     // create new thread with the task
        episodeGetter.setDaemon(true);
        episodeGetter.start();
    }

    private void initializeBtnAddFiles (){
        btnAddFiles.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Files to Rename");
                filesToRename = fileChooser.showOpenMultipleDialog(showList.getScene().getWindow());

                populateViewWithItems();
            }
        });
    }

    private void initializeBtnAddDirectories (){
        btnAddDirectories.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("Choose Directories");
                File dir = directoryChooser.showDialog(showList.getScene().getWindow());

                if (loadFilesFromDirectory(dir)){
                    populateViewWithItems();
                }
            }
        });
    }

    private void initializeBtnClearAll (){}

    private void initializeBtnRename (){}

    private void initializeTable (){
        showList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);  // enable multi row selection
    }

    private void setColumnWidth (){
        ObservableList<TableColumn> columns = showList.getColumns();

        // set width of columns - works even when window is resized
        columns.get(0).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(12));
        columns.get(1).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(2).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(3).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(4).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(7));
    }

    private void setColumnValueFactory (){
        ObservableList<TableColumn> columns = showList.getColumns();

        columns.get(0).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                return new SimpleStringProperty(param.getValue().get(0));
            }
        });

        columns.get(1).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                return new SimpleStringProperty(param.getValue().get(1));
            }
        });

        columns.get(2).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                return new SimpleStringProperty(param.getValue().get(2));
            }
        });

        columns.get(3).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                return new SimpleStringProperty(param.getValue().get(3));
            }
        });

        columns.get(4).setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<String>,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<ObservableList<String>, String> param) {
                return new SimpleStringProperty(param.getValue().get(4));
            }
        });
    }



    private void initializeKeyboardShortcuts (){
        final KeyCombination openFiles = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        final KeyCombination openDir = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);


        showList.getScene().addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println ("event");
                if (openFiles.match(event)){
                    System.out.println ("match");
                    btnAddFiles.fire();
                }
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
