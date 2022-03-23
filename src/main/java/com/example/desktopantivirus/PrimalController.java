package com.example.desktopantivirus;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PrimalController implements Initializable {
    @FXML
    public GridPane grid;
    @FXML
    public Button btnScan;
    @FXML
    public Text txtDirectory;
    @FXML
    public ImageView imageviewFolder;

    private static final int MAX_TXT_LENGTH = 68;
    private File directory;
    private List<File> files;

    private void chooseDirectory() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Set directory");
        directory = chooser.showDialog(new Stage());
        if (directory != null) {
            imageviewFolder.setImage(new Image(getClass().getResourceAsStream("pics/opened-folder.png")));
            if (directory.getPath().length() > MAX_TXT_LENGTH) {
                txtDirectory.setText(directory.getPath().substring(0, MAX_TXT_LENGTH) + "..");
            } else {
                txtDirectory.setText(directory.getPath());
            }
        }
    }

    private void doScan(File dir) {
        files = new LinkedList<>();
        scan(dir);
        showFiles(files);
    }

    private void scan(File dir){
        for (File file : dir.listFiles()) {
            if(!file.isDirectory() && isExecutable(file)){
                files.add(file);
            }
            if (file.isDirectory()){
                scan(file);
            }
        }
    }

    private void showFiles(List<File> files) {
        grid.getChildren().clear();
        for (int i =0;i<files.size();i++) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("file.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MiniFileController imageController = fxmlLoader.getController();
            imageController.setData(files.get(i));
            grid.add(anchorPane, 0, i);
        }
    }

    private boolean isExecutable(File file){
        byte[] firstBytes = new byte[2];
        try {
            FileInputStream input = new FileInputStream(file);
            input.read(firstBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return firstBytes[0] == 0x4d && firstBytes[1] == 0x5a;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewFolder.setImage(new Image(getClass().getResourceAsStream("pics/closed-folder.png")));
        imageviewFolder.setPickOnBounds(true);
        imageviewFolder.setOnMouseClicked(e -> chooseDirectory());
        btnScan.setOnMouseClicked(e -> doScan(directory));
        btnScan.getStyleClass().add("btnScan");
    }
}
