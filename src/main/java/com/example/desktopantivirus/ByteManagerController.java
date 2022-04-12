package com.example.desktopantivirus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ByteManagerController implements Initializable {
    @FXML
    public Button btnAdd;
    @FXML
    public Button btnUpdate;
    @FXML
    public Button btnDelete;
    @FXML
    public Button btnPull;
    @FXML
    public Button btnPush;
    @FXML
    public ImageView imageviewDatabase;
    @FXML
    public Text txtDatabase;
    @FXML
    public GridPane grid;
    @FXML
    public TextField byteTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewDatabase.setImage(new Image(getClass().getResourceAsStream("pics/no-database.png")));

    }
}
