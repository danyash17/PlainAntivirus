package com.example.desktopantivirus;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

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

    private String dbName;
    private String tableName;

    public void establishDatabaseConnection() {
        Stage window = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("connector.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatabaseConnectionWindowController databaseConnectionWindowController = fxmlLoader.getController();
        databaseConnectionWindowController.setByteManagerController(this);
        window.getIcons().add(new Image(getClass().getResourceAsStream("pics/antivirus.png")));
        window.setTitle("Database connection window");
        window.setScene(new Scene(root, 313, 383));
        window.show();
        window.setResizable(false);
    }

    public void populateSuccessfulConnection(Stage stage) {
        stage.close();
        imageviewDatabase.setImage(new Image(getClass().getResourceAsStream("pics/database.png")));
        txtDatabase.setText(dbName + "/" + tableName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageviewDatabase.setImage(new Image(getClass().getResourceAsStream("pics/no-database.png")));
        imageviewDatabase.setPickOnBounds(true);
        imageviewDatabase.setOnMouseClicked(e -> establishDatabaseConnection());
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
