package cz.lsrom.tvmanager.controller;

import cz.lsrom.tvmanager.UIStarter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
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

    UIStarter uiStarter;

    public void setUiStarter (UIStarter uiStarter){
        this.uiStarter = uiStarter;
    }

    @FXML
    private void initialize() {
        System.out.println ("rename controller");

        setColumnWidth();
        setColumnValueFactory();

        initializeBtnAddFiles();
        initializeBtnAddDirectories();
    }

    private void populateViewWithItems (){
        if (filesToRename == null || filesToRename.isEmpty()){
            return;
        }

        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        ObservableList<String> row = FXCollections.observableArrayList();

        row.addAll("first", "second", "third", "forth", "fifth");
        data.add(row);

        showList.getItems().setAll(data);
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

    private void setColumnWidth (){
        ObservableList<TableColumn> columns = showList.getColumns();

        // set width of columns - works even when window is resized
        columns.get(0).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(1).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(2).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(3).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(15));
        columns.get(4).prefWidthProperty().bind(showList.widthProperty().divide(64).multiply(4));
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
