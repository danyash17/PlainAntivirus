package com.example.desktopantivirus;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DatabaseConnectionWindowController implements Initializable {
    @FXML
    public TextField txtUrl;
    @FXML
    public TextField txtPort;
    @FXML
    public TextField txtDbName;
    @FXML
    public TextField txtLogin;
    @FXML
    public TextField txtPassword;
    @FXML
    public Button btnEstablishConnection;
    @FXML
    public TextField txtTableName;
    @FXML
    public Text txtStatus;

    private ByteManagerController byteManagerController;

    public void establishDatabaseConnection() {
        String url = "jdbc:mysql://" + txtUrl.getText().trim() + ":" + txtPort.getText().trim()
                + "/" + txtDbName.getText().trim() + "?serverTimezone=Europe/Rome";
        String databseUserName = txtLogin.getText().trim();
        String databasePassword = txtPassword.getText().trim();
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, databseUserName, databasePassword);
            if (con != null) {
                DatabaseMetaData dbm = con.getMetaData();
                ResultSet tables = dbm.getTables(null, null, txtTableName.getText().trim(), null);
                if (!tables.next()) {
                    throw new NullPointerException();
                }
                byteManagerController.setConnection(con);
                Stage stage = (Stage) btnEstablishConnection.getScene().getWindow();
                byteManagerController.setTableName(txtTableName.getText().trim());
                byteManagerController.setDbName(txtDbName.getText().trim());
                byteManagerController.populateSuccessfulConnection(stage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            txtStatus.setText("SQL ERROR OCCURED, CHECK DATA IN TEXTFIELDS");
        } catch (NullPointerException e) {
            e.printStackTrace();
            txtStatus.setText("ENTERED TABLE DOES NOT EXIST");

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnEstablishConnection.setOnAction(e -> establishDatabaseConnection());
    }

    public void setByteManagerController(ByteManagerController byteManagerController) {
        this.byteManagerController = byteManagerController;
    }
}
