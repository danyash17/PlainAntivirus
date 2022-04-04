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

import java.io.*;
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
    private static final long PE_OFFSET = 0x3c;
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

    private void scan(File dir) {
        for (File file : dir.listFiles()) {
            if (!file.isDirectory() && isPe(file)) {
                files.add(file);
            }
            if (file.isDirectory()) {
                scan(file);
            }
        }
    }

    private void showFiles(List<File> files) {
        grid.getChildren().clear();
        for (int i = 0; i < files.size(); i++) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("file.fxml"));
            AnchorPane anchorPane = null;
            try {
                anchorPane = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MiniFileController miniFileController = fxmlLoader.getController();
            miniFileController.setData(files.get(i));
            grid.add(anchorPane, 0, i);
        }
    }

    private boolean isPe(File file) {
        byte[] bytes = new byte[2];
        boolean isPe = false;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.read(bytes);
            if (bytes[0] == 0x4d && bytes[1] == 0x5a) {
                //pe header address
                randomAccessFile.seek(PE_OFFSET);
                randomAccessFile.read(bytes);
                //pe
                randomAccessFile.seek(0x100 + bytes[0]);
                randomAccessFile.read(bytes);
                isPe = bytes[0] == 0x50 && bytes[1] == 0x45;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isPe;
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
