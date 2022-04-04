package com.example.desktopantivirus;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MiniFileController implements Initializable {
    public Text txtFile;
    public ImageView imageviewFile;
    public AnchorPane pane;
    public Text txtPath;
    private File file;
    private static final int MAX_TXT_LENGTH = 80;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pane.setOnMouseClicked(e -> {
            try {
                Runtime.getRuntime().exec("C:\\Windows\\System32\\notepad.exe " + file.getPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void setData(File file) {
        String path = file.getPath();
        switch (path.substring(path.lastIndexOf("."))) {
            case ".exe": {
                imageviewFile.setImage(new Image(getClass().getResourceAsStream("pics/exe.png")));
                break;
            }
            case ".dll": {
                imageviewFile.setImage(new Image(getClass().getResourceAsStream("pics/dll.png")));
                break;
            }
            default:
                imageviewFile.setImage(new Image(getClass().getResourceAsStream("pics/unknown.png")));
        }
        fitText(txtFile, file.getName());
        fitText(txtPath, path);
        this.file = file;
    }

    private void fitText(Text text, String value) {
        int length = value.length();
        if (length > MAX_TXT_LENGTH) {
            int nearestSlashIndex = value.substring(length - MAX_TXT_LENGTH, MAX_TXT_LENGTH).indexOf('\\');
            text.setText(".." + value.substring(length - MAX_TXT_LENGTH + nearestSlashIndex, length));
        } else {
            text.setText(value);
        }
    }
}
