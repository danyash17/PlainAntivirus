package com.example.desktopantivirus;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MiniFileController implements Initializable {
    public Text txtFile;
    public ImageView imageviewFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setData(File file){
        imageviewFile.setImage(new Image(getClass().getResourceAsStream("pics/exe.png")));
        txtFile.setText(file.getName());
    }
}
