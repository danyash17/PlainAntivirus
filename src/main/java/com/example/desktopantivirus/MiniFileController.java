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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pane.setOnMouseClicked(e-> {
            try {
                Runtime.getRuntime().exec("D:\\Programs\\Notepad++\\notepad++.exe " + file.getPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    public void setData(File file){
        imageviewFile.setImage(new Image(getClass().getResourceAsStream("pics/exe.png")));
        txtFile.setText(file.getName());
        txtPath.setText(file.getPath());
        this.file = file;
    }
}
